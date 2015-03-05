package com.demigodsrpg.demigames.kit;

import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class MutableKit implements Kit {

    // -- DATA -- //

    private Map<Integer, ItemStack> contents;
    private Optional<ItemStack> helmet;
    private Optional<ItemStack> chestplate;
    private Optional<ItemStack> leggings;
    private Optional<ItemStack> boots;

    // -- CONSTRUCTOR -- //

    public MutableKit(Map<Integer, ItemStack> contents, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.contents = contents;
        this.helmet = Optional.ofNullable(helmet);
        this.chestplate = Optional.ofNullable(chestplate);
        this.leggings = Optional.ofNullable(leggings);
        this.boots = Optional.ofNullable(boots);
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[0]; // TODO
    }

    @Override
    public Optional<ItemStack> getHelmet() {
        return helmet;
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return chestplate;
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return leggings;
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return boots;
    }
}
