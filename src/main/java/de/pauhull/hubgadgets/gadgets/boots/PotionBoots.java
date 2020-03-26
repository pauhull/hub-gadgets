package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 26.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class PotionBoots extends Boots {

    PotionBoots() {

        super(Color.TEAL, "PotionBoots");
    }

    @Override
    public void playEffect(Player player) {

        player.getWorld().playEffect(player.getLocation(), Effect.SPELL, 0);
    }
}
