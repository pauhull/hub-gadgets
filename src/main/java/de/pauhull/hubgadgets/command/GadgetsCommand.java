package de.pauhull.hubgadgets.command;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets.command

import de.pauhull.hubgadgets.HubGadgets;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class GadgetsCommand implements CommandExecutor {

    private HubGadgets hubGadgets;

    public GadgetsCommand(HubGadgets hubGadgets) {

        this.hubGadgets = hubGadgets;
        hubGadgets.getCommand("gadgets").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        Silverfish silverfish = (Silverfish) player.getWorld().spawnEntity(player.getLocation(), EntityType.SILVERFISH);
        silverfish.setSilent(true);
        silverfish.setInvulnerable(true);
        silverfish.setLeashHolder(player);
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

        FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation(), Material.WOOL, (byte) (Math.random() * 16));
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        fallingBlock.setHurtEntities(false);
        silverfish.addPassenger(fallingBlock);

        Runnable runnable = () -> {

            if (silverfish.isDead()) {
                return;
            }

            if (!silverfish.isLeashed() || silverfish.getPassengers().size() == 0) {

                silverfish.getWorld().playSound(silverfish.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                silverfish.getWorld().spawnParticle(Particle.CLOUD, silverfish.getLocation(), 0);

                for (Entity e : silverfish.getPassengers()) e.remove();
                silverfish.remove();
                return;
            }

            for (Entity e : silverfish.getPassengers()) e.setTicksLived(1);

            Vector vector = silverfish.getVelocity();
            vector.setY(0.2);
            silverfish.setVelocity(vector);

            Vector pushAwayVector = new Vector();
            for (Entity nearby : silverfish.getNearbyEntities(1.5, 1.5, 1.5)) {
                pushAwayVector.setX(pushAwayVector.getX() + nearby.getLocation().getX() - silverfish.getLocation().getX());
                pushAwayVector.setZ(pushAwayVector.getZ() + nearby.getLocation().getZ() - silverfish.getLocation().getZ());
            }
            pushAwayVector.normalize().multiply(-0.01);
            Vector newVelocity = silverfish.getVelocity();
            newVelocity.add(pushAwayVector);
            silverfish.setVelocity(newVelocity);

            silverfish.setFallDistance(0);
        };

        Bukkit.getScheduler().runTaskTimer(hubGadgets, runnable, 2, 2);

        hubGadgets.getMainInventory().show(player);

        return true;
    }

}
