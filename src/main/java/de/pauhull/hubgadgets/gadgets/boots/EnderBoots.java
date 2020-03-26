package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class EnderBoots extends Boots {

    EnderBoots() {

        super(Color.PURPLE, "EnderBoots");
    }

    @Override
    public void playEffect(Player player) {

        player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
    }
}
