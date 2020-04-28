package de.pauhull.hubgadgets;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets

import de.pauhull.hubgadgets.command.GadgetsCommand;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.data.Database;
import de.pauhull.hubgadgets.data.sql.mysql.MySQLDatabase;
import de.pauhull.hubgadgets.data.sql.sqlite.SQLiteDatabase;
import de.pauhull.hubgadgets.economy.Economy;
import de.pauhull.hubgadgets.economy.mcstats.McStatsEconomy;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.boots.BootsManager;
import de.pauhull.hubgadgets.inventory.BootsInventory;
import de.pauhull.hubgadgets.inventory.BuyInventory;
import de.pauhull.hubgadgets.inventory.MainInventory;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class HubGadgets extends JavaPlugin {

    @Getter
    private static HubGadgets instance;
    private ScheduledExecutorService scheduler;
    private ExecutorService executorService;
    private Configuration configuration;
    private MainInventory mainInventory;
    private BootsInventory bootsInventory;
    private BootsManager bootsManager;
    private BuyInventory buyInventory;
    private List<Gadget> gadgets;
    private Database database;
    private Economy economy;

    @Override
    public void onEnable() {

        instance = this;

        this.gadgets = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.executorService = Executors.newCachedThreadPool();
        this.configuration = new Configuration(this, "config.yml");
        this.mainInventory = new MainInventory(this);
        this.bootsInventory = new BootsInventory(this);
        this.bootsManager = new BootsManager(this);
        this.buyInventory = new BuyInventory(this);
        this.economy = new McStatsEconomy(this);

        if (configuration.getString("DatabaseType").equalsIgnoreCase("SQLite")) {
            this.database = new SQLiteDatabase(this);
        } else {
            this.database = new MySQLDatabase(this);
        }

        new GadgetsCommand(this);
    }

    @Override
    public void onDisable() {

        this.database.close();
        this.scheduler.shutdown();
        this.executorService.shutdown();
    }
}
