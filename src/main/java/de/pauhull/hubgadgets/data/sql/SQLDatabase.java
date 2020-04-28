package de.pauhull.hubgadgets.data.sql;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data

import de.pauhull.hubgadgets.data.Database;

import java.sql.PreparedStatement;

public interface SQLDatabase extends Database {

    void connect();

    void update(String s);

    PreparedStatement prepare(String s);

}
