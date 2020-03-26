package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 26.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class LavaBoots extends Boots {

    LavaBoots() {

        super(Color.ORANGE, "LavaBoots");
    }

    @Override
    public void playEffect(Player player) {

        player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 0);
    }
}
