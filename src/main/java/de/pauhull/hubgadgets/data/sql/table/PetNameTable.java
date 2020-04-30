package de.pauhull.hubgadgets.data.sql.table;

// Project: hub-gadgets
// Class created on 29.04.2020 by Paul
// Package de.pauhull.hubgadgets.data.sql.table

import de.pauhull.hubgadgets.data.sql.HikariSQLDatabase;
import de.pauhull.hubgadgets.gadgets.pets.Pet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class PetNameTable {

    private static final String TABLE = "gadgets_pet_names";

    private HikariSQLDatabase database;
    private ExecutorService executorService;

    public PetNameTable(HikariSQLDatabase database, ExecutorService executorService) {

        this.database = database;
        this.executorService = executorService;

        database.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `pet` VARCHAR(100), `name` VARCHAR(100), PRIMARY KEY (`id`))");
    }

    public void getPetName(UUID uuid, Pet pet, Consumer<String> consumer) {

        executorService.execute(() -> {

            try {
                PreparedStatement statement = database.prepare("SELECT * FROM `" + TABLE + "` WHERE `uuid`=? AND `pet`=?");
                statement.setString(1, uuid.toString());
                statement.setString(2, pet.getName());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String displayName = resultSet.getString("name");
                    consumer.accept(displayName);
                } else {
                    consumer.accept(pet.getDisplayName());
                }

                statement.close();
                statement.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setPetName(UUID uuid, Pet pet, String displayName) {

        executorService.execute(() -> {

            try {
                PreparedStatement statement = database.prepare("SELECT * FROM `" + TABLE + "` WHERE `uuid`=? AND `pet`=?");
                statement.setString(1, uuid.toString());
                statement.setString(2, pet.getName());
                ResultSet result = statement.executeQuery();
                boolean next = result.next();
                statement.close();
                statement.getConnection().close();

                if (next) {

                    PreparedStatement update = database.prepare("UPDATE `" + TABLE + "` SET `name`=? WHERE `uuid`=? AND `pet`=?");
                    update.setString(1, displayName);
                    update.setString(2, uuid.toString());
                    update.setString(3, pet.getName());
                    update.executeUpdate();
                    update.close();
                    update.getConnection().close();
                } else {

                    PreparedStatement insert = database.prepare("INSERT INTO `" + TABLE + "` VALUES (0, ?, ?, ?)");
                    insert.setString(1, uuid.toString());
                    insert.setString(2, pet.getName());
                    insert.setString(3, displayName);
                    insert.executeUpdate();
                    insert.close();
                    insert.getConnection().close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
