package de.pauhull.hubgadgets.inventory;

// Project: hub-gadgets
// Class created on 29.04.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.pets.Pet;
import de.pauhull.hubgadgets.gadgets.pets.PetInfo;
import de.pauhull.hubgadgets.util.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PetRenameInventory implements Listener {

    private ItemStack rename;
    private ItemStack remove;

    public PetRenameInventory(HubGadgets hubGadgets) {

        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        this.rename = new ItemBuilder(Material.ANVIL)
                .displayName(hubGadgets.getConfiguration().getString("Pets.Rename"))
                .build();

        this.remove = new ItemBuilder(Material.REDSTONE)
                .displayName(hubGadgets.getConfiguration().getString("Pets.Remove"))
                .build();
    }

    public void show(Player player) {

        PetInfo petInfo = Pet.getPetByPlayer(player);

        if (petInfo == null) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 9, petInfo.getEntity().getCustomName());
        inventory.setItem(3, rename);
        inventory.setItem(5, remove);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory.getSize() != 9 || !inventory.getItem(3).equals(rename) || !inventory.getItem(5).equals(remove)) {
            return;
        }

        event.setCancelled(true);

        PetInfo petInfo = Pet.getPetByPlayer(player);

        if (petInfo == null) {
            return;
        }

        if (rename.equals(stack)) {

            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            new AnvilGUI.Builder()
                    .item(new ItemStack(Material.MONSTER_EGG, 1, petInfo.getPetType().getEntityType().getTypeId()))
                    .text(petInfo.getEntity().getCustomName().replace("§", "&"))
                    .plugin(HubGadgets.getInstance())
                    .onComplete(((ignored, result) -> {
                        if (result == null) {
                            return AnvilGUI.Response.text("");
                        }

                        if (HubGadgets.getInstance().getSwearwordFilter().containsSwearword(result)) {
                            player.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 1, 1);
                            return AnvilGUI.Response.text(result);
                        }

                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                        petInfo.getEntity().setCustomName(ChatColor.translateAlternateColorCodes('&', result));
                        HubGadgets.getInstance().getDatabase().setPetName(player.getUniqueId(), petInfo.getPetType(), petInfo.getEntity().getCustomName());
                        return AnvilGUI.Response.close();
                    }))
                    .open(player);
        } else if (remove.equals(stack)) {

            Pet.despawn(player);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            player.closeInventory();
        }
    }

}
