package de.pauhull.hubgadgets.gadgets;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets

import de.pauhull.hubgadgets.inventory.gadget.GadgetInventory;
import org.bukkit.inventory.ItemStack;

public interface Gadget {

    String getPermission();

    boolean isPremium();

    Price getPrice();

    ItemStack getItem();

    GadgetInventory getGadgetInventory();

    String getName();

}
