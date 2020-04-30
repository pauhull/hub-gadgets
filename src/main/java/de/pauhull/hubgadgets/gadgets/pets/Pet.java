package de.pauhull.hubgadgets.gadgets.pets;

// Project: hub-gadgets
// Class created on 28.04.2020 by Paul
// Package de.pauhull.hubgadgets.gadgets.pets

import de.pauhull.hubgadgets.HubGadgets;
import de.pauhull.hubgadgets.gadgets.Gadget;
import de.pauhull.hubgadgets.gadgets.Price;
import de.pauhull.hubgadgets.inventory.gadget.GadgetInventory;
import de.pauhull.hubgadgets.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Pet implements Gadget {

    @Getter
    private static List<Pet> pets = new ArrayList<>();

    private GadgetInventory gadgetInventory;
    private EntityType entityType;
    private String name;
    private String displayName;
    private Price price;
    private boolean baby;
    private String permission;
    private ItemStack item, selectedItem, boughtItem, unboughtItem;

    public Pet(EntityType entityType, String name, String displayName, Price price, boolean baby, String permission) {

        pets.add(this);
        HubGadgets.getInstance().getGadgets().add(this);

        this.gadgetInventory = HubGadgets.getInstance().getPetInventory();
        this.entityType = entityType;
        this.name = name;
        this.displayName = displayName;
        this.price = price;
        this.baby = baby;
        this.permission = permission;

        this.item = new ItemBuilder(new ItemStack(Material.MONSTER_EGG, 1, entityType.getTypeId()))
                .displayName(displayName)
                .build();

        this.selectedItem = new ItemBuilder(getItem())
                .enchantment(Enchantment.DURABILITY, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .lore(HubGadgets.getInstance().getConfiguration().getString("ClickToUnselect").split("\n"))
                .build();

        String status;
        if (!permission.equals("")) {
            status = HubGadgets.getInstance().getConfiguration().getString("Premium");
        } else {
            if (price.getValue() == 0) {
                status = HubGadgets.getInstance().getConfiguration().getString("Free");
            } else {
                status = HubGadgets.getInstance().getConfiguration().getString("Bought");
            }
        }
        this.boughtItem = new ItemBuilder(getItem())
                .lore(HubGadgets.getInstance().getConfiguration()
                        .getString("ClickToSelect")
                        .replace("%STATUS%", status)
                        .split("\n"))
                .build();

        if (price.getValue() == 0) {
            this.unboughtItem = boughtItem;
        } else {
            this.unboughtItem = new ItemBuilder(getItem())
                    .lore(HubGadgets.getInstance().getConfiguration().getString("ClickToBuy")
                            .replace("%PRICE%", price.toString())
                            .split("\n"))
                    .build();
        }
    }

    public static void despawn(Player player) {

        PetManager petManager = HubGadgets.getInstance().getPetManager();
        PetInfo petInfo = Pet.getPetByPlayer(player);

        if (petInfo != null) {
            petInfo.getEntity().remove();
            petManager.getSpawnedPets().remove(player.getUniqueId());
        }
    }

    public static Pet getPetByItem(ItemStack stack) {

        for (Pet pet : pets) {
            if (pet.isItem(stack)) {
                return pet;
            }
        }

        return null;
    }

    public static PetInfo getPetByPlayer(Player player) {

        PetManager petManager = HubGadgets.getInstance().getPetManager();
        return petManager.getSpawnedPets().get(player.getUniqueId());
    }

    public PetInfo spawn(Player player) {

        despawn(player);

        PetManager petManager = HubGadgets.getInstance().getPetManager();

        Location location = player.getLocation().clone();
        location.setPitch(0);

        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(location, entityType);
        entity.setMetadata("pet", new FixedMetadataValue(HubGadgets.getInstance(), null));

        if (entity instanceof Ageable && baby) {
            Ageable ageable = (Ageable) entity;
            ageable.setBaby();
            ageable.setAgeLock(true);
        }

        if (entity instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) entity;
            ocelot.setTamed(true);
            ocelot.setOwner(player);
            ocelot.setCatType(Ocelot.Type.values()[Math.abs((int) (player.getUniqueId().getLeastSignificantBits() % Ocelot.Type.values().length))]);
        } else if (entity instanceof Wolf) {
            Wolf wolf = (Wolf) entity;
            wolf.setTamed(true);
            wolf.setHealth(20);
            wolf.setOwner(player);
            wolf.setCollarColor(DyeColor.values()[Math.abs((int) (player.getUniqueId().getMostSignificantBits() % DyeColor.values().length))]);
        }

        HubGadgets.getInstance().getDatabase().getPetName(player.getUniqueId(), this, name -> {
            Bukkit.getScheduler().runTask(HubGadgets.getInstance(), () -> {
                entity.setCustomName(name);
                entity.setCustomNameVisible(true);
            });
        });

        PetInfo petInfo = new PetInfo(this, entity);
        petManager.getSpawnedPets().put(player.getUniqueId(), petInfo);

        return petInfo;
    }

    private boolean isItem(ItemStack stack) {

        return isItem(stack, item)
                || isSelectedItem(stack)
                || isBoughtItem(stack)
                || isUnboughtItem(stack);
    }

    public boolean isBoughtItem(ItemStack stack) {

        return isItem(boughtItem, stack);
    }

    public boolean isUnboughtItem(ItemStack stack) {

        return isItem(unboughtItem, stack);
    }

    public boolean isSelectedItem(ItemStack stack) {

        return isItem(selectedItem, stack);
    }

    private boolean isItem(ItemStack stack1, ItemStack stack2) {

        if (stack2.getItemMeta() == null) {
            return false;
        }

        return stack2.getType() == stack1.getType()
                && stack2.getItemMeta().getDisplayName().equals(stack1.getItemMeta().getDisplayName())
                && compareLore(stack2.getItemMeta().getLore(), stack1.getItemMeta().getLore());
    }

    private boolean compareLore(List<String> lore1, List<String> lore2) {

        if (lore1 == null && lore2 == null) {
            return true;
        }

        if (lore1 == null || lore2 == null) {
            return false;
        }

        if (lore1.size() != lore2.size()) {
            return false;
        }

        for (int i = 0; i < lore1.size(); i++) {
            if (!lore1.get(i).equals(lore2.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isPremium() {
        return !permission.equals("");
    }
}
