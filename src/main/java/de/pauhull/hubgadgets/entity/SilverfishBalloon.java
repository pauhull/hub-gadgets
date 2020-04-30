package de.pauhull.hubgadgets.entity;

// Project: hub-gadgets
// Class created on 30.04.2020 by Paul
// Package de.pauhull.hubgadgets.entity

import net.minecraft.server.v1_12_R1.EntitySilverfish;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

public class SilverfishBalloon extends EntitySilverfish {

    public SilverfishBalloon(World world) {
        super(((CraftWorld) world).getHandle());
        setSilent(true);
        setInvulnerable(true);
    }

    @Override
    protected void r() {
    }
}
