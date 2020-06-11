package de.pauhull.hubgadgets;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets

import de.pauhull.hubgadgets.command.GadgetsCommand;
import de.pauhull.hubgadgets.config.Configuration;
import de.pauhull.hubgadgets.data.Database;
import de.pauhull.hubgadgets.data.sql.mysql.HikariMySQLDatabase;
import de.pauhull.hubgadgets.data.sql.sqlite.HikariSQLiteDatabase;
import de.pauhull.hubgadgets.economy.Economy;
import de.pauhull.hubgadgets.economy.mcstats.McStatsEconomy;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.boots.BootsManager;
import de.pauhull.hubgadgets.gadgets.pets.PetManager;
import de.pauhull.hubgadgets.inventory.BuyInventory;
import de.pauhull.hubgadgets.inventory.MainInventory;
import de.pauhull.hubgadgets.inventory.PetRenameInventory;
import de.pauhull.hubgadgets.inventory.gadget.BootsInventory;
import de.pauhull.hubgadgets.inventory.gadget.PetInventory;
import de.pauhull.hubgadgets.util.SwearwordFilter;
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
    private PetInventory petInventory;
    private PetRenameInventory petRenameInventory;
    private BootsManager bootsManager;
    private BuyInventory buyInventory;
    private PetManager petManager;
    private List<Gadget> gadgets;
    private Database database;
    private Economy economy;
    private SwearwordFilter swearwordFilter;

    @Override
    public void onEnable() {

        instance = this;

        this.gadgets = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(10);
        this.executorService = Executors.newCachedThreadPool();
        this.configuration = new Configuration(this, "config.yml");
        this.mainInventory = new MainInventory(this);
        this.bootsInventory = new BootsInventory(this);
        this.petInventory = new PetInventory(this);
        this.petRenameInventory = new PetRenameInventory(this);
        this.bootsManager = new BootsManager(this);
        this.petManager = new PetManager(this);
        this.buyInventory = new BuyInventory(this);
        this.economy = new McStatsEconomy(this);
        this.swearwordFilter = new SwearwordFilter(this, "badwords.txt");

        if (configuration.getString("DatabaseType").equalsIgnoreCase("SQLite")) {
            this.database = new HikariSQLiteDatabase(this);
        } else {
            this.database = new HikariMySQLDatabase(this);
        }

        /*
        Bukkit.getScheduler().runTaskTimer(this, () -> {

            for(Player player : Bukkit.getOnlinePlayers()) {
                Vector vector = player.getVelocity();
                if(vector.getY() < 0) {
                    vector.multiply(new Vector(0, 0.5, 0));
                }
                player.setVelocity(vector);
            }

        }, 1, 1);


         */
        new GadgetsCommand(this);

        petManager.removeAllPets();
    }

    @Override
    public void onDisable() {

        this.database.close();
        this.scheduler.shutdown();
        this.executorService.shutdown();
    }
}
