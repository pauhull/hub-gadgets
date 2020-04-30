package de.pauhull.hubgadgets.command;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets.command

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.entity.SilverfishBalloon;
import net.minecraft.server.v1_12_R1.EnumMoveType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

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

        SilverfishBalloon silverfishBalloon = new SilverfishBalloon(player.getWorld());
        silverfishBalloon.setPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        silverfishBalloon.setLeashHolder(((CraftPlayer) player).getHandle(), true);

        Bukkit.getScheduler().runTaskTimer(hubGadgets, () -> {
            if (silverfishBalloon.getLeashHolder() == null) {
                silverfishBalloon.killEntity();
                return;
            }

            silverfishBalloon.move(EnumMoveType.SELF, 0, 0.2, 0);
            silverfishBalloon.fallDistance = 0;
        }, 2, 2);

        hubGadgets.getMainInventory().show(player);

        return true;
    }

}
