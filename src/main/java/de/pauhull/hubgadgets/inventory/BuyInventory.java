package de.pauhull.hubgadgets.inventory;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.Price;
import de.pauhull.hubgadgets.util.ItemBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class BuyInventory implements Listener {

    @Getter(AccessLevel.NONE)
    private HubGadgets hubGadgets;
    private String title;
    private ItemStack buy;
    private ItemStack cancel;
    private Map<Inventory, Gadget> inventories;

    public BuyInventory(HubGadgets hubGadgets) {

        this.inventories = new HashMap<>();
        this.hubGadgets = hubGadgets;
        Bukkit.getPluginManager().registerEvents(this, hubGadgets);

        Configuration config = hubGadgets.getConfiguration();

        this.title = config.getString("BuyInventory.Title");
        this.buy = new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName(config.getString("BuyInventory.Item.Buy"))
                .build();
        this.cancel = new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName(config.getString("BuyInventory.Item.Cancel"))
                .build();
    }

    public void show(Player player, Gadget gadget) {

        hubGadgets.getDatabase().hasGadget(player.getUniqueId(), gadget, hasGadget -> {
            Bukkit.getScheduler().runTask(hubGadgets, () -> {

                if (hasGadget) {
                    player.sendMessage(hubGadgets.getConfiguration().getStringWithPrefix("BuyInventory.Messages.GadgetAlreadyBought"));
                    return;
                }

                Inventory inventory = Bukkit.createInventory(null, 27, title);

                ItemStack stack = new ItemBuilder(gadget.getItem())
                        .lore(hubGadgets.getConfiguration().getString("ClickToBuy")
                                .replace("%PRICE%", gadget.getPrice().toString())
                                .split("\n"))
                        .build();

                ItemStack buyStack = new ItemBuilder(buy)
                        .displayName(hubGadgets.getConfiguration().getString("BuyInventory.Item.Buy")
                                .replace("%PRICE%", gadget.getPrice().toString()))
                        .build();

                inventory.setItem(11, buyStack);
                inventory.setItem(13, stack);
                inventory.setItem(15, cancel);

                player.openInventory(inventory);
                player.playSound(player.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                inventories.put(inventory, gadget);
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

        Gadget gadget = inventories.get(inventory);

        if (gadget == null) {
            return;
        }

        if (stack == null) {
            return;
        }

        if (buy.getType() == stack.getType()) {

            Consumer<Double> consumer = value -> {
                Bukkit.getScheduler().runTask(hubGadgets, () -> {

                    if (value < gadget.getPrice().getValue()) {
                        Price missing = new Price(gadget.getPrice().getType(), gadget.getPrice().getValue() - value);
                        player.sendMessage(hubGadgets.getConfiguration().getStringWithPrefix("NotEnoughMoney").replace("%MISSING%", missing.toString()));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
                        return;
                    }

                    if (gadget.getPrice().getType() == Price.Type.COINS) {
                        hubGadgets.getEconomy().setCoins(player.getUniqueId(), value - gadget.getPrice().getValue());
                    } else {
                        hubGadgets.getEconomy().setCredits(player.getUniqueId(), value - gadget.getPrice().getValue());
                    }

                    hubGadgets.getDatabase().giveGadget(player.getUniqueId(), gadget);
                    player.sendMessage(hubGadgets.getConfiguration().getStringWithPrefix("SuccessfullyBought"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    gadget.getGadgetInventory().show(player);
                });
            };

            if (gadget.getPrice().getType() == Price.Type.COINS) {
                hubGadgets.getEconomy().getCoins(player.getUniqueId(), consumer);
            } else {
                hubGadgets.getEconomy().getCredits(player.getUniqueId(), consumer);
            }

            inventories.remove(inventory);
        } else if (cancel.equals(stack)) {

            gadget.getGadgetInventory().show(player);
            inventories.remove(inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        inventories.remove(event.getInventory());
    }

}
