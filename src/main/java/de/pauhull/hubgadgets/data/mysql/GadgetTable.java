package de.pauhull.hubgadgets.data.mysql;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.data.mysql

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.Gadget;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

class GadgetTable {

    private static final String TABLE = "gadgets";

    private MySQLDatabase mySQL;
    private ExecutorService executorService;

    GadgetTable(MySQLDatabase mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `gadget` VARCHAR(100), PRIMARY KEY (`id`))");
    }

    void giveGadget(UUID uuid, Gadget gadget) {

        executorService.execute(() -> {

            try {
                PreparedStatement statement = mySQL.prepare("INSERT INTO `" + TABLE + "` VALUES(0, ?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, gadget.getClass().getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    void getGadgets(UUID uuid, Consumer<List<Gadget>> consumer) {

        executorService.execute(() -> {

            List<Gadget> gadgets = new ArrayList<>();

            try {
                PreparedStatement statement = mySQL.prepare("SELECT * FROM `" + TABLE + "` WHERE `uuid`=?");
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    String gadgetName = result.getString("gadget");

                    for (Gadget gadget : HubGadgets.getInstance().getGadgets()) {
                        if (gadget.getClass().getName().equals(gadgetName)) {
                            gadgets.add(gadget);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            consumer.accept(gadgets);
        });
    }
}
