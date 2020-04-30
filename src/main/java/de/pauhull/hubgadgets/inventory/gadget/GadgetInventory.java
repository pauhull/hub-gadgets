package de.pauhull.hubgadgets.inventory.gadget;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.inventory

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface GadgetInventory extends Listener {

    void show(Player player);

}
