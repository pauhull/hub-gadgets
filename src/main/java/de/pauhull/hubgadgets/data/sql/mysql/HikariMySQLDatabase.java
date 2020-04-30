package de.pauhull.hubgadgets.data.sql.mysql;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.mysql

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.sql.HikariSQLDatabase;
import org.bukkit.configuration.ConfigurationSection;

public class HikariMySQLDatabase extends HikariSQLDatabase {

    public HikariMySQLDatabase(HubGadgets hubGadgets) {
        super(hubGadgets);
    }

    @Override
    public HikariDataSource createDataSource() {

        if (isConnected()) {
            return super.dataSource;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        ConfigurationSection section = HubGadgets.getInstance().getConfig().getConfigurationSection("MySQL");

        String host = section.getString("Host");
        String port = section.getString("Port");
        String database = section.getString("Database");
        String user = section.getString("User");
        String password = section.getString("Password");
        boolean ssl = section.getBoolean("SSL");

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

        return new HikariDataSource(hikariConfig);
    }
}
