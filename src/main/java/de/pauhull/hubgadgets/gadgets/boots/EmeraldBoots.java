package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 26.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class EmeraldBoots extends Boots {

    EmeraldBoots() {
        super(Color.LIME, "EmeraldBoots");
    }

    @Override
    public void playEffect(Player player) {

        player.getWorld().playEffect(player.getLocation(), Effect.HAPPY_VILLAGER, 0);
    }
}
