package de.pauhull.hubgadgets.data.sql.sqlite;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.sqlite

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.sql.HikariSQLDatabase;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

public class HikariSQLiteDatabase extends HikariSQLDatabase {

    public HikariSQLiteDatabase(HubGadgets hubGadgets) {
        super(hubGadgets);
    }

    @Override
    protected HikariDataSource createDataSource() {

        ConfigurationSection section = HubGadgets.getInstance().getConfig().getConfigurationSection("SQLite");
        File databaseFile = new File(HubGadgets.getInstance().getDataFolder(), section.getString("File"));

        if (isConnected()) {
            return super.dataSource;
        }

        if (!databaseFile.exists()) {

            databaseFile.getParentFile().mkdirs();

            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
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

        return new HikariDataSource(hikariConfig);
    }
}
