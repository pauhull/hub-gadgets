package de.pauhull.hubgadgets.gadgets.pets;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.pets

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.Price;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager implements Listener {

    @Getter
    private Map<UUID, PetInfo> spawnedPets;

    public PetManager(HubGadgets hubGadgets) {

        this.spawnedPets = new HashMap<>();

        ConfigurationSection section = hubGadgets.getConfiguration().getSection("Pets.Item");

        for (String key : section.getKeys(false)) {

            String displayName = ChatColor.translateAlternateColorCodes('&', section.getString(key + ".Name"));
            EntityType entityType = EntityType.valueOf(section.getString(key + ".EntityType"));
            double value = section.getDouble(key + ".Price.Value");
            Price.Type type = Price.Type.valueOf(section.getString(key + ".Price.Type"));
            Price price = new Price(type, value);
            boolean baby = section.getBoolean(key + ".Baby");
            String permission = section.getString(key + ".Permission");

            new Pet(entityType, key, displayName, price, baby, permission);
        }

        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(hubGadgets, () -> {
            for (UUID uuid : spawnedPets.keySet()) {

                Player player = Bukkit.getPlayer(uuid);
                Entity entity = spawnedPets.get(uuid).getEntity();

                if (player == null) {
                    continue;
                }

                if (entity instanceof Wolf) {
                    if (((Wolf) entity).isAngry()) {
                        ((Wolf) entity).setAngry(false);
                        ((Wolf) entity).setTarget(null);
                    }
                }

                if (entity instanceof Sittable) {
                    if (((Sittable) entity).isSitting()) {
                        continue;
                    }
                }

                double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());

                if (distanceSquared > 20 * 20) {

                    entity.teleport(player.getLocation());
                } else {

                    if (distanceSquared > 4 * 4) {
                        ((EntityInsentient) ((CraftEntity) entity).getHandle()).getNavigation()
                                .a(player.getLocation().getX(),
                                        player.getLocation().getY(),
                                        player.getLocation().getZ(),
                                        1.75f);
                    }
                }
            }
        }, 1, 1);
    }

    public void removeAllPets() {

        spawnedPets.clear();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata("pet")) {
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        PetInfo petInfo = Pet.getPetByPlayer(player);

        if (player.isSneaking() && petInfo != null && petInfo.getEntity() == entity) {
            event.setCancelled(true);
            HubGadgets.getInstance().getPetRenameInventory().show(player);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        for (PetInfo petInfo : spawnedPets.values()) {
            if (petInfo.getEntity() == event.getEntity()) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        Pet.despawn(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setBaby();
        villager.setVelocity(player.getLocation().getDirection().multiply(5));

        Bukkit.getScheduler().runTaskLater(HubGadgets.getInstance(), () -> {
            villager.getWorld().createExplosion(villager.getLocation(), 5f);
        }, 20);
    }
}
