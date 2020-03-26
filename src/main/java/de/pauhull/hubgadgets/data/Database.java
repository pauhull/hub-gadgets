package de.pauhull.hubgadgets.data;

// Project: hub-gadgets
// Class created on 24.03.2020 by Paul
// Package de.pauhull.hubgadgets.data

import de.pauhull.hubgadgets.gadgets.Gadget;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Database {

    void getGadgets(UUID uuid, Consumer<List<Gadget>> consumer);

    void hasGadget(UUID uuid, Gadget gadget, Consumer<Boolean> consumer);

    void giveGadget(UUID uuid, Gadget gadget);

}
