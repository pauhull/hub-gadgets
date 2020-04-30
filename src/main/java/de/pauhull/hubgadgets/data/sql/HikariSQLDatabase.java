package de.pauhull.hubgadgets.data.sql;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data

import com.zaxxer.hikari.HikariDataSource;
import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.data.Database;
import de.pauhull.hubgadgets.data.sql.table.EquippedTable;
import de.pauhull.hubgadgets.data.sql.table.GadgetTable;
import de.pauhull.hubgadgets.data.sql.table.PetNameTable;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.pets.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class HikariSQLDatabase implements Database {

    protected HikariDataSource dataSource;
    private GadgetTable gadgetTable;
    private EquippedTable equippedTable;
    private PetNameTable petNameTable;

    public HikariSQLDatabase(HubGadgets hubGadgets) {

        this.dataSource = createDataSource();

        this.gadgetTable = new GadgetTable(this, hubGadgets.getExecutorService());
        this.equippedTable = new EquippedTable(this, hubGadgets.getExecutorService());
        this.petNameTable = new PetNameTable(this, hubGadgets.getExecutorService());
    }

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

    public PreparedStatement prepare(String s) {

        try {
            return this.dataSource.getConnection().prepareStatement(s);
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

    protected abstract HikariDataSource createDataSource();

    protected boolean isConnected() {

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

    @Override
    public void getPetName(UUID uuid, Pet pet, Consumer<String> consumer) {

        petNameTable.getPetName(uuid, pet, consumer);
    }

    @Override
    public void setPetName(UUID uuid, Pet pet, String name) {

        petNameTable.setPetName(uuid, pet, name);
    }

}
