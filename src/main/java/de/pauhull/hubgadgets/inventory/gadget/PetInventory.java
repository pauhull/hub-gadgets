package de.pauhull.hubgadgets.inventory.gadget;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.gadgets.pets.Pet;
import de.pauhull.hubgadgets.gadgets.pets.PetInfo;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetInventory implements GadgetInventory {

    private HubGadgets hubGadgets;
    private String title;

    public PetInventory(HubGadgets hubGadgets) {

        this.hubGadgets = hubGadgets;
        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        Configuration config = hubGadgets.getConfiguration();

        this.title = config.getString("Pets.Title");
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, title);

        this.updateInventory(inventory, player, () -> {

            player.openInventory(inventory);
            player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
        });
    }

    private void updateInventory(Inventory inventory, Player player, Runnable callback) {

        hubGadgets.getDatabase().getGadgets(player.getUniqueId(), gadgets -> {
            Bukkit.getScheduler().runTask(hubGadgets, () -> { // synchronous

                int slot = 10;

                PetInfo petInfo = Pet.getPetByPlayer(player);

                for (Pet pet : Pet.getPets()) {

                    if (petInfo != null && petInfo.getPetType() == pet) {
                        inventory.setItem(slot++, pet.getSelectedItem());
                    } else {
                        if (gadgets.contains(pet)) {
                            inventory.setItem(slot++, pet.getBoughtItem());
                        } else {
                            inventory.setItem(slot++, pet.getUnboughtItem());
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

            Pet pet = Pet.getPetByItem(stack);

            if (pet != null) {

                if (pet.isBoughtItem(stack)) {

                    if (pet.isPremium() && !player.hasPermission(pet.getPermission())) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
                        player.sendMessage(HubGadgets.getInstance().getConfiguration().getStringWithPrefix("NoPermission"));
                        return;
                    }

                    pet.spawn(player);
                    updateInventory(inventory, player, player::updateInventory);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                } else if (pet.isSelectedItem(stack)) {

                    Pet.despawn(player);
                    updateInventory(inventory, player, player::updateInventory);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                } else if (pet.isUnboughtItem(stack)) {

                    hubGadgets.getBuyInventory().show(player, pet);
                }
            }
        }
    }
}
