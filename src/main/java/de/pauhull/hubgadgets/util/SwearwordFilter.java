package de.pauhull.hubgadgets.util;

// Project: hub-gadgets
// Class created on 30.04.2020 by Paul
// Package de.pauhull.hubgadgets.util

import de.pauhull.hubgadgets.HubGadgets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SwearwordFilter {

    private File file;
    private HubGadgets hubGadgets;
    private List<String> swearwordsRegex;

    public SwearwordFilter(HubGadgets hubGadgets, String fileName) {

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

    private void load() {

        this.swearwordsRegex = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {

                // produces this format:
                // (?mix)f+\s*u+\s*c+\s*k+
                StringBuilder builder = new StringBuilder("(?mix)");
                for (char c : line.toCharArray()) {
                    if (builder.length() != 0) {
                        builder.append("\\s*");
                    }
                    builder.append(c).append('+');
                }
                swearwordsRegex.add(builder.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsSwearword(String s) {

        for (String regex : swearwordsRegex) {

            if (Pattern.compile(regex).matcher(s).find()) {
                return true;
            }
        }

        return false;
    }

}
