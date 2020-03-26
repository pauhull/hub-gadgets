package de.pauhull.hubgadgets.command;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets.command

import de.pauhull.hubgadgets.HubGadgets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        hubGadgets.getMainInventory().show(player);

        return true;
    }

}
