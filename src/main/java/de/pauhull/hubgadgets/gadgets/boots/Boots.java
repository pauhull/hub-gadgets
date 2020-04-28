package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.Price;
import de.pauhull.hubgadgets.inventory.GadgetInventory;
import de.pauhull.hubgadgets.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Boots implements Gadget {

    @Getter
    private static List<Boots> boots = new ArrayList<>();

    @Getter
    protected ItemStack item, selectedItem, boughtItem, unboughtItem;

    @Getter
    protected Price price;

    @Getter
    private GadgetInventory gadgetInventory;

    Boots(Color color, String configName) {

        HubGadgets.getInstance().getGadgets().add(this);
        boots.add(this);

        Price.Type type = Price.Type.valueOf(HubGadgets.getInstance().getConfiguration()
                .getString("Boots.Item." + configName + ".Price.Type"));
        double value = HubGadgets.getInstance().getConfiguration()
                .getDouble("Boots.Item." + configName + ".Price.Value");
        this.price = new Price(type, value);

        this.item = buildItem(color, configName);
        this.selectedItem = buildSelectedItem();
        this.boughtItem = buildBoughtItem();
        this.unboughtItem = buildUnboughtItem();
        this.gadgetInventory = HubGadgets.getInstance().getBootsInventory();
    }

    public static Boots getBootsByItem(ItemStack stack) {

        if (stack == null) {
            return null;
        }

        for (Boots boots : boots) {
            if (boots.isItem(stack)) {
                return boots;
            }
        }

        return null;
    }

    public static Boots getBootsByName(String className) {

        for (Boots boots : boots) {
            if (boots.getClass().getSimpleName().equalsIgnoreCase(className)) {
                return boots;
            }
        }


        return null;
    }

    public static Boots getBootsByPlayer(Player player) {

        return getBootsByItem(player.getInventory().getBoots());
    }

    public static void unequip(Player player) {

        Boots boots = getBootsByPlayer(player);
        HubGadgets.getInstance().getDatabase().unequipGadget(player.getUniqueId(), boots);

        player.getInventory().setBoots(null);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    }

    abstract public void playEffect(Player player);

    public void equip(Player player) {

        if (player.getInventory().getBoots() != null) {

            Boots boots = getBootsByPlayer(player);

            if (boots != null) {
                unequip(player);
                equip(player);
                return;
            }

            Collection<ItemStack> drop = player.getInventory().addItem(player.getInventory().getBoots()).values();
            drop.forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
            player.getInventory().setBoots(null);
        }

        player.getInventory().setBoots(getItem());
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

        HubGadgets.getInstance().getDatabase().equipGadget(player.getUniqueId(), this);
    }

    protected boolean isItem(ItemStack stack) {

        return item.equals(stack)
                || selectedItem.equals(stack)
                || boughtItem.equals(stack)
                || unboughtItem.equals(stack);
    }

    protected ItemStack buildItem(Color leatherColor, String configName) {

        return new ItemBuilder(Material.LEATHER_BOOTS)
                .leatherColor(leatherColor)
                .displayName(HubGadgets.getInstance().getConfiguration().getString("Boots.Item." + configName + ".Name"))
                .unbreakable(true)
                .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                .build();
    }

    protected ItemStack buildSelectedItem() {

        return new ItemBuilder(getItem())
                .enchantment(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .lore(HubGadgets.getInstance().getConfiguration().getString("ClickToUnselect").split("\n"))
                .build();
    }

    protected ItemStack buildUnboughtItem() {

        if (price.getValue() == 0) {
            return boughtItem;
        }

        return new ItemBuilder(getItem())
                .lore(HubGadgets.getInstance().getConfiguration().getString("ClickToBuy")
                        .replace("%PRICE%", price.toString())
                        .split("\n"))
                .build();
    }

    protected ItemStack buildBoughtItem() {

        return new ItemBuilder(getItem())
                .lore(HubGadgets.getInstance().getConfiguration()
                        .getString("ClickToSelect")
                        .replace("%STATUS%", HubGadgets.getInstance().getConfiguration()
                                .getString(price.getValue() == 0 ? "Free" : "Bought"))
                        .split("\n"))
                .build();
    }

}
