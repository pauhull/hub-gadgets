package de.pauhull.hubgadgets.util;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.util

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack stack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {

        this.stack = new ItemStack(material);
        this.itemMeta = stack.getItemMeta();
    }

    public ItemBuilder(ItemStack stack) {

        this.stack = stack.clone();
        this.itemMeta = stack.getItemMeta();
    }

    public ItemBuilder amount(int amount) {

        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(short durability) {

        stack.setDurability(durability);
        return this;
    }

    public ItemBuilder displayName(String displayName) {

        itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {

        itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder lore(String... lore) {

        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder unbreakable(boolean unbreakable) {

        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder spawnEgg(EntityType entityType) {

        stack.setItemMeta(itemMeta);
        stack.setType(Material.MONSTER_EGG);
        stack.setDurability(entityType.getTypeId());
        itemMeta = stack.getItemMeta();
        return this;
    }

    public ItemBuilder skull(String skullOwner) {

        stack.setItemMeta(itemMeta);
        stack.setType(Material.SKULL_ITEM);
        stack.setDurability((short) 3);
        SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
        skullMeta.setOwner(skullOwner);
        itemMeta = skullMeta;
        return this;
    }

    public ItemBuilder flag(ItemFlag... itemFlags) {

        itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder leatherColor(Color color) {

        LeatherArmorMeta meta = (LeatherArmorMeta) itemMeta;
        meta.setColor(color);
        itemMeta = meta;
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {

        stack.setItemMeta(itemMeta);
        stack.addUnsafeEnchantment(enchantment, level);
        itemMeta = stack.getItemMeta();
        return this;
    }

    public ItemStack build() {

        stack.setItemMeta(itemMeta);
        return stack;
    }

}
