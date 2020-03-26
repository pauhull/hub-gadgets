package de.pauhull.hubgadgets.data.mysql;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data.mysql

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.Database;
import de.pauhull.hubgadgets.gadgets.Gadget;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MySQLDatabase implements Database {

    private boolean ssl;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

    private Connection connection;

    private GadgetTable gadgetTable;

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
    }

    private void connect() {

        try {
            String connectionURL = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=%s", host, port, database, Boolean.toString(ssl));
            connection = DriverManager.getConnection(connectionURL, user, password);

        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(HubGadgets.getInstance());
        }
    }

    void close() {

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void update(String sql) {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ResultSet query(String query) {

        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    PreparedStatement prepare(String sql) {

        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void getGadgets(UUID uuid, Consumer<List<Gadget>> consumer) {

        gadgetTable.getGadgets(uuid, consumer);
    }

    @Override
    public void hasGadget(UUID uuid, Gadget gadget, Consumer<Boolean> consumer) {

        gadgetTable.getGadgets(uuid, gadgets -> consumer.accept(gadgets.contains(gadget)));
    }

    @Override
    public void giveGadget(UUID uuid, Gadget gadget) {

        gadgetTable.giveGadget(uuid, gadget);
    }
}
