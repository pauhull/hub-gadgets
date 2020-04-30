package de.pauhull.hubgadgets.inventory.gadget;

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

        this.title = config.getString("Boots.Title");
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        this.updateInventory(inventory, player, () -> {
            player.openInventory(inventory);
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        });
    }

    private void updateInventory(Inventory inventory, Player player, Runnable callback) {

        Boots selectedBoots = Boots.getBootsByPlayer(player);

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

                callback.run();
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

            Boots boots = Boots.getBootsByItem(stack);

            if (boots != null) {

                if (stack.equals(boots.getBoughtItem())) {

                    if (boots.isPremium() && !player.hasPermission(boots.getPermission())) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
                        player.sendMessage(HubGadgets.getInstance().getConfiguration().getStringWithPrefix("NoPermission"));
                        return;
                    }

                    boots.equip(player);
                    updateInventory(inventory, player, player::updateInventory);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                } else if (stack.equals(boots.getSelectedItem())) {

                    Boots.unequip(player);
                    updateInventory(inventory, player, player::updateInventory);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                } else if (stack.equals(boots.getUnboughtItem())) {

                    hubGadgets.getBuyInventory().show(player, boots);
                }
            }
        }
    }

}
