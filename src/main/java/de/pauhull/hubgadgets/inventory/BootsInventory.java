package de.pauhull.hubgadgets.inventory;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.gadgets.boots.Boots;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BootsInventory implements GadgetInventory {

    @Getter(AccessLevel.NONE)
    private HubGadgets hubGadgets;

    private String title;

    public BootsInventory(HubGadgets hubGadgets) {

        this.hubGadgets = hubGadgets;
        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        Configuration config = hubGadgets.getConfiguration();

        this.title = config.getString("BootsInventory.Title");
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        this.updateInventory(inventory, player);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
    }

    private void updateInventory(Inventory inventory, Player player) {

        Boots selectedBoots = Boots.getBoots(player);

        hubGadgets.getDatabase().getGadgets(player.getUniqueId(), gadgets -> {
            Bukkit.getScheduler().runTask(hubGadgets, () -> { // synchronous

                int slot = 10;

                for (Boots boots : Boots.getBoots()) {

                    if (selectedBoots == boots) {
                        inventory.setItem(slot++, boots.getSelectedItem());
                    } else {
                        if (gadgets.contains(boots)) { // player has bought boots
                            inventory.setItem(slot++, boots.getBoughtItem());
                        } else {
                            inventory.setItem(slot++, boots.getUnboughtItem());
                        }
                    }
                }
            });
        });
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

        if (stack != null) {

            Boots boots = Boots.getBoots(stack);

            if (boots != null) {

                if (stack.equals(boots.getBoughtItem())) {

                    boots.equip(player);
                    updateInventory(inventory, player);
                } else if (stack.equals(boots.getSelectedItem())) {

                    Boots.unequip(player);
                    updateInventory(inventory, player);
                } else if (stack.equals(boots.getUnboughtItem())) {

                    hubGadgets.getBuyInventory().show(player, boots);
                }
            }
        }
    }

}
