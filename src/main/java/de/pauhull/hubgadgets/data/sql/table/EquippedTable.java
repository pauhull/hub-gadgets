package de.pauhull.hubgadgets.data.sql.table;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.table

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.sql.HikariSQLDatabase;
import de.pauhull.hubgadgets.gadgets.Gadget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class EquippedTable {

    private static final String TABLE = "gadgets_equipped";

    private HikariSQLDatabase database;
    private ExecutorService executorService;

    public EquippedTable(HikariSQLDatabase database, ExecutorService executorService) {

        this.database = database;
        this.executorService = executorService;

        database.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `gadget` VARCHAR(100), PRIMARY KEY (`id`))");
    }

    public void equipGadget(UUID uuid, Gadget gadget) {

        executorService.execute(() -> {

            database.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + gadget.getName() + "')");
        });
    }

    public void unequipGadget(UUID uuid, Gadget gadget) {

        executorService.execute(() -> {

            database.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `gadget`='" + gadget.getName() + "'");
        });
    }

    public void getEquipped(UUID uuid, Consumer<List<Gadget>> consumer) {

        executorService.execute(() -> {

            List<Gadget> gadgets = new ArrayList<>();

            try {
                PreparedStatement statement = database.prepare("SELECT * FROM `" + TABLE + "` WHERE `uuid`=?");
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String gadgetName = resultSet.getString("gadget");
                    for (Gadget gadget : HubGadgets.getInstance().getGadgets()) {
                        if (gadget.getName().equalsIgnoreCase(gadgetName)) {
                            gadgets.add(gadget);
                        }
                    }
                }

                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            consumer.accept(gadgets);
        });
    }
}