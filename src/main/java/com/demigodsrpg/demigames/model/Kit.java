package com.demigodsrpg.demigames.model;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Model(name = "kit")
public class Kit {
    // -- DATA -- //

    private ItemStack[] contents;
    private Optional<ItemStack> helmet;
    private Optional<ItemStack> chestplate;
    private Optional<ItemStack> leggings;
    private Optional<ItemStack> boots;


}
