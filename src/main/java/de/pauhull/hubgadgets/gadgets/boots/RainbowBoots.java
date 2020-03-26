package de.pauhull.hubgadgets.gadgets.boots;

// Project: hub-gadgets
// Class created on 26.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.boots

import de.pauhull.hubgadgets.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RainbowBoots extends Boots {

    RainbowBoots() {

        super(Color.WHITE, "RainbowBoots");
    }

    @Override
    public void playEffect(Player player) {

        int speed = 2500; // color cycle duration in ms
        float hue = (System.currentTimeMillis() % speed) / (float) speed;
        java.awt.Color javaColor = new java.awt.Color(java.awt.Color.HSBtoRGB(hue, 1f, 1f));
        Color bukkitColor = Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());

        player.getInventory().setBoots(new ItemBuilder(item).leatherColor(bukkitColor).build());

        for (int i = 0; i < 10; i++) {

            Location location = player.getLocation().clone();
            location.add(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);

            player.getWorld().spawnParticle(Particle.REDSTONE,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    0,
                    javaColor.getRed() / 255f,
                    javaColor.getGreen() / 255f,
                    javaColor.getBlue() / 255f,
                    1);
        }
    }

    @Override
    protected boolean isItem(ItemStack stack) {

        if (stack == null || stack.getType() != Material.LEATHER_BOOTS || stack.getItemMeta() == null) {

            return false;
        }

        return stack.getItemMeta().getDisplayName().equals(getItem().getItemMeta().getDisplayName());
    }
}
