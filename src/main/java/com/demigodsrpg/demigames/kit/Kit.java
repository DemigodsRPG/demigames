/*
 * Copyright (c) 2015 Demigods RPG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.demigodsrpg.demigames.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.Optional;

public interface Kit {
    Kit EMPTY = new EmptyKit();

    String getName();

    ItemStack[] getContents();

    ItemStack[] getArmor();

    PotionEffect[] getPotionEffects();

    default Optional<ItemStack> getItemStack(int index) {
        return Optional.ofNullable(getContents()[index]);
    }

    default Optional<ItemStack> getHelmet() {
        return Optional.ofNullable(getArmor()[3]);
    }

    default Optional<ItemStack> getChestplate() {
        return Optional.ofNullable(getArmor()[2]);
    }

    default Optional<ItemStack> getLeggings() {
        return Optional.ofNullable(getArmor()[1]);
    }

    default Optional<ItemStack> getBoots() {
        return Optional.ofNullable(getArmor()[0]);
    }

    default void apply(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setContents(getContents());
        inventory.setArmorContents(getArmor());
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffects(Arrays.asList(getPotionEffects()));
    }
}
