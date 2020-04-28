package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.manager

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.Gadget;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Getter
public class BootsManager implements Listener {

    public BootsManager(HubGadgets hubGadgets) {

        new HeartBoots();
        new LavaBoots();
        new EmeraldBoots();
        new PotionBoots();
        new WaterBoots();
        new EnderBoots();
        new RainbowBoots();

        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        hubGadgets.getScheduler().scheduleAtFixedRate(() -> {

            for (Player player : Bukkit.getOnlinePlayers()) {

                Boots boots = Boots.getBootsByPlayer(player);

                if (boots != null) {
                    boots.playEffect(player);
                }
            }

        }, 0, 1000 / 20, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getSlot() == 36) { // boots slot

            Boots boots = Boots.getBootsByItem(event.getCurrentItem());

            if (boots != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Boots boots = Boots.getBootsByItem(event.getItemDrop().getItemStack());

        if (boots != null) {
            event.setCancelled(true);
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Iterator<ItemStack> iterator = event.getDrops().iterator();

        while (iterator.hasNext()) {

            ItemStack stack = iterator.next();
            Boots boots = Boots.getBootsByItem(stack);

            if (boots != null) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        HubGadgets.getInstance().getDatabase().getEquipped(player.getUniqueId(), equipped -> {

            for (Gadget gadget : equipped) {

                if (gadget instanceof Boots) {
                    Boots boots = (Boots) gadget;

                    Bukkit.getScheduler().runTask(HubGadgets.getInstance(), () -> {
                        player.getInventory().setBoots(boots.getItem());
                    });
                }
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        HubGadgets.getInstance().getDatabase().getEquipped(player.getUniqueId(), equipped -> {

            Boots currentBoots = Boots.getBootsByPlayer(player);
            Boots equippedBoots = null;

            for (Gadget gadget : equipped) {
                if (gadget instanceof Boots) {
                    equippedBoots = (Boots) gadget;
                }
            }

            if (equippedBoots != null) {
                equippedBoots.equip(player);
                return;
            }

            if (currentBoots != null) {
                player.getInventory().setBoots(null);
            }
        });
    }
}
