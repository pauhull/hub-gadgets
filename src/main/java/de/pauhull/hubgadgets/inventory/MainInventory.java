package de.pauhull.hubgadgets.inventory;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.util.ItemBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
public class MainInventory implements Listener {

    @Getter(AccessLevel.NONE)
    private HubGadgets hubGadgets;

    private String title;
    private ItemStack boots;
    private ItemStack pets;
    private ItemStack balloons;
    private ItemStack skulls;
    private ItemStack ranks;

    public MainInventory(HubGadgets hubGadgets) {

        this.hubGadgets = hubGadgets;
        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        Configuration config = hubGadgets.getConfiguration();

        this.title = hubGadgets.getConfiguration().getString("MainInventory.Title");
        this.boots = new ItemBuilder(Material.DIAMOND_BOOTS)
                .displayName(config.getString("MainInventory.Item.Boots"))
                .flag(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        this.pets = new ItemBuilder(Material.MONSTER_EGG)
                .spawnEgg(EntityType.OCELOT)
                .displayName(config.getString("MainInventory.Item.Pets"))
                .build();
        this.balloons = new ItemBuilder(Material.LEASH)
                .displayName(config.getString("MainInventory.Item.Balloons"))
                .build();
        this.skulls = new ItemBuilder(Material.SKULL_ITEM)
                .skull("leStylex")
                .displayName(config.getString("MainInventory.Item.Skulls"))
                .build();
        this.ranks = new ItemBuilder(Material.GOLD_INGOT)
                .displayName(config.getString("MainInventory.Item.Ranks"))
                .build();
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        inventory.setItem(10, boots);
        inventory.setItem(11, pets);
        inventory.setItem(13, ranks);
        inventory.setItem(15, skulls);
        inventory.setItem(16, balloons);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack stack = event.getCurrentItem();

        if (!inventory.getTitle().equals(title)) {
            return;
        }

        event.setCancelled(true);

        if (stack == null) {
            return;
        }

        if (boots.equals(stack)) {

            hubGadgets.getBootsInventory().show(player);
        } else if (pets.getType() == stack.getType()) {

            hubGadgets.getPetInventory().show(player);
        }
    }

}
