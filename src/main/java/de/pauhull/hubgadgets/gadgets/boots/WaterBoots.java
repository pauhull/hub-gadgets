package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 26.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class WaterBoots extends Boots {

    WaterBoots() {

        super(Color.BLUE, "WaterBoots");
    }

    @Override
    public void playEffect(Player player) {

        player.getWorld().spawnParticle(Particle.WATER_SPLASH, player.getLocation(), 0);
    }

}
