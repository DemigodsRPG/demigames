package com.demigodsrpg.demigames.kit;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;

public class ImmutableKit implements Kit {

    // -- DATA -- //

    private final ItemStack[] contents;
    private final Optional<ItemStack> helmet;
    private final Optional<ItemStack> chestplate;
    private final Optional<ItemStack> leggings;
    private final Optional<ItemStack> boots;

    // -- PRIVATE CONSTRUCTOR -- //

    private ImmutableKit(ItemStack[] contents, Optional<ItemStack> helmet, Optional<ItemStack> chestplate, Optional<ItemStack> leggings, Optional<ItemStack> boots) {
        this.contents = contents;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    // -- GETTERS -- //

    @Override
    public ItemStack[] getContents() {
        return Arrays.copyOf(contents, contents.length);
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

    // -- STATIC CONSTRUCTOR METHOD -- //

    public static ImmutableKit of(Kit kit) {
        return new ImmutableKit(kit.getContents(), kit.getHelmet(), kit.getChestplate(), kit.getLeggings(), kit.getBoots());
    }
}
