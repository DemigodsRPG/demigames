package com.demigodsrpg.demigames.minigame.kit;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface Kit {
    ItemStack[] getContents();

    Optional<ItemStack> getHelmet();

    Optional<ItemStack> getChestplate();

    Optional<ItemStack> getLeggings();

    Optional<ItemStack> getBoots();
}
