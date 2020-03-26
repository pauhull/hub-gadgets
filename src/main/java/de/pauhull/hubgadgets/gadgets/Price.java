package de.pauhull.hubgadgets.gadgets;

// Project: hub-gadgets
// Class created on 25.03.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Price {

    private Type type;
    private double value;

    @Override
    public String toString() {

        return String.format(value % 1.0 != 0 ? "%s" : "%.0f", value) + " " + (type == Price.Type.COINS ? "Coins" : "Credits");
    }

    public enum Type {

        COINS, CREDITS
    }

}
