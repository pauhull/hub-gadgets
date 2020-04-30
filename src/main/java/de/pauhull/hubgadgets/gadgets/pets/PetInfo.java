package de.pauhull.hubgadgets.gadgets.pets;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.pets

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
@AllArgsConstructor
public class PetInfo {

    private Pet petType;
    private Entity entity;

}
