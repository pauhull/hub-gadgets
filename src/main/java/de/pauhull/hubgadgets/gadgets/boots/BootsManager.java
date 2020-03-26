package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.manager

import de.pauhull.hubgadgets.HubGadgets;
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

                Boots boots = Boots.getBoots(player);

                if (boots != null) {
                    boots.playEffect(player);
                }
            }

        }, 0, 1000 / 20, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getSlot() == 36) { // boots slot

            Boots boots = Boots.getBoots(event.getCurrentItem());

            if (boots != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Boots boots = Boots.getBoots(event.getItemDrop().getItemStack());

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
            Boots boots = Boots.getBoots(stack);

            if (boots != null) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        //TODO: re-equip boots
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        //TODO: equip boots
    }
}
