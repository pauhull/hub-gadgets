package de.pauhull.hubgadgets.data.sql.sqlite;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.sqlite

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.sql.SQLDatabase;
import de.pauhull.hubgadgets.data.sql.table.EquippedTable;
import de.pauhull.hubgadgets.data.sql.table.GadgetTable;
import de.pauhull.hubgadgets.gadgets.Gadget;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class SQLiteDatabase implements SQLDatabase {

    private File databaseFile;
    private HikariDataSource dataSource;
    private GadgetTable gadgetTable;
    private EquippedTable equippedTable;

    public SQLiteDatabase(HubGadgets hubGadgets) {

        ConfigurationSection section = hubGadgets.getConfig().getConfigurationSection("SQLite");

        this.databaseFile = new File(hubGadgets.getDataFolder(), section.getString("File"));

        this.connect();

        this.gadgetTable = new GadgetTable(this, hubGadgets.getExecutorService());
        this.equippedTable = new EquippedTable(this, hubGadgets.getExecutorService());
    }

    @Override
    public void update(String s) {

        try {
            Connection connection = this.dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(s);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PreparedStatement prepare(String s) {

        try {
            PreparedStatement statement = this.dataSource.getConnection().prepareStatement(s);
            return statement;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void close() {

        if (isConnected()) {
            dataSource.close();
        }
    }

    @Override
    public void connect() {

        if (isConnected()) {
            return;
        }

        if (!this.databaseFile.exists()) {

            this.databaseFile.getParentFile().mkdirs();

            try {
                this.databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile.getPath());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("Gadgets");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf-8");
        hikariConfig.addDataSourceProperty("useUnicode", true);
        hikariConfig.addDataSourceProperty("allowMultiQueries", true);
        hikariConfig.addDataSourceProperty("ssl", false);
        hikariConfig.addDataSourceProperty("useSSL", false);
        hikariConfig.setConnectionTestQuery("SELECT 1");

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    private boolean isConnected() {

        return dataSource != null && !dataSource.isClosed();
    }

    @Override
    public void getGadgets(UUID uuid, Consumer<List<Gadget>> consumer) {

        gadgetTable.getGadgets(uuid, consumer);
    }

    @Override
    public void giveGadget(UUID uuid, Gadget gadget) {

        gadgetTable.giveGadget(uuid, gadget);
    }

    @Override
    public void hasGadget(UUID uuid, Gadget gadget, Consumer<Boolean> consumer) {

        gadgetTable.getGadgets(uuid, gadgets -> consumer.accept(gadgets.contains(gadget)));
    }

    @Override
    public void equipGadget(UUID uuid, Gadget gadget) {

        equippedTable.equipGadget(uuid, gadget);
    }

    @Override
    public void unequipGadget(UUID uuid, Gadget gadget) {

        equippedTable.unequipGadget(uuid, gadget);
    }

    @Override
    public void isEquipped(UUID uuid, Gadget gadget, Consumer<Boolean> consumer) {

        equippedTable.getEquipped(uuid, gadgets -> consumer.accept(gadgets.contains(gadget)));
    }

    @Override
    public void getEquipped(UUID uuid, Consumer<List<Gadget>> consumer) {

        equippedTable.getEquipped(uuid, consumer);
    }
}
