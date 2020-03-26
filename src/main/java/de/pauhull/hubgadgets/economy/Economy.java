package de.pauhull.hubgadgets.economy;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.economy

import java.util.UUID;
import java.util.function.Consumer;

public interface Economy {

    void getCoins(UUID uuid, Consumer<Double> consumer);

    void getCredits(UUID uuid, Consumer<Double> consumer);

    void setCoins(UUID uuid, double coins);

    void setCredits(UUID uuid, double credits);

}
