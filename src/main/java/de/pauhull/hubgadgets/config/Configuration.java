package de.pauhull.hubgadgets.config;

// Project: hub-gadgets
// Class created on 23.03.2020 by Paul
// Package de.pauhull.hubgadgets.config

import de.pauhull.hubgadgets.HubGadgets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Configuration {

    private File file;
    private HubGadgets hubGadgets;
    private FileConfiguration config;

    public Configuration(HubGadgets hubGadgets, String fileName) {

        this.hubGadgets = hubGadgets;
        this.file = new File(hubGadgets.getDataFolder(), fileName);
        this.copyFile();
        this.load();
    }

    public void copyFile() {

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(hubGadgets.getResource(file.getName()), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getString(String path) {

        return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }

    public String getStringWithPrefix(String path) {

        return getString("Prefix") + getString(path);
    }

    public double getDouble(String path) {

        return config.getDouble(path);
    }

    public ConfigurationSection getSection(String path) {

        return config.getConfigurationSection(path);
    }

}
