package de.pauhull.hubgadgets.data.sql.mysql;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.mysql

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.sql.SQLDatabase;
import de.pauhull.hubgadgets.data.sql.table.EquippedTable;
import de.pauhull.hubgadgets.data.sql.table.GadgetTable;
import de.pauhull.hubgadgets.gadgets.Gadget;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MySQLDatabase implements SQLDatabase {

    private HikariDataSource dataSource;
    private boolean ssl;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;
    private GadgetTable gadgetTable;
    private EquippedTable equippedTable;

    public MySQLDatabase(HubGadgets hubGadgets) {

        ConfigurationSection section = hubGadgets.getConfig().getConfigurationSection("MySQL");

        this.host = section.getString("Host");
        this.port = section.getString("Port");
        this.database = section.getString("Database");
        this.user = section.getString("User");
        this.password = section.getString("Password");
        this.ssl = section.getBoolean("SSL");
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

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", host, port, database));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("Gadgets");
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("useSSL", ssl);
        hikariConfig.addDataSourceProperty("ssl", ssl);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("characterEncoding", "utf-8");
        hikariConfig.addDataSourceProperty("useUnicode", true);
        hikariConfig.addDataSourceProperty("allowMultiQueries", true);

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
