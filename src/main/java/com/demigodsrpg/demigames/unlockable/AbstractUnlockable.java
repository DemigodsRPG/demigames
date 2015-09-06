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

package com.demigodsrpg.demigames.unlockable;

import com.demigodsrpg.demigames.game.impl.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public abstract class AbstractUnlockable implements Unlockable {
    // -- DATA -- //

    private ItemStack lockedItem;
    private ItemStack activeItem;
    private ItemStack inactiveItem;

    String name;
    int cost;
    String[] lore;
    boolean excludedByDefault;

    public AbstractUnlockable(String name, int cost, boolean excludedByDefault, String... lore) {
        this(name, cost, lore, excludedByDefault);
    }

    public AbstractUnlockable(String name, int cost, String[] lore, boolean excludedByDefault) {
        this.name = name;
        this.cost = cost;
        this.lore = lore;
        this.excludedByDefault = excludedByDefault;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public String[] getLore() {
        return lore;
    }

    @Override
    public boolean isExcludedByDefault() {
        return excludedByDefault;
    }

    @Override
    public ItemStack getLockedItem() {
        return lockedItem;
    }

    @Override
    public ItemStack getUnlockedItem(boolean isActive) {
        return isActive ? activeItem : inactiveItem;
    }

    private void processItems(MaterialData data) {
        lockedItem = new ItemStackBuilder(data).displayName(name).
                lore(ChatColor.LIGHT_PURPLE + "Cost: " + ChatColor.DARK_PURPLE + getCost() + " Tickets").
                appendLore(lore).build();
        activeItem = new ItemStackBuilder(data).displayName(name).
                lore(ChatColor.LIGHT_PURPLE + "This perk is " + ChatColor.DARK_GREEN + "active" +
                        ChatColor.LIGHT_PURPLE + ".").appendLore(lore).build();
        inactiveItem = new ItemStackBuilder(data).displayName(name).lore(ChatColor.LIGHT_PURPLE + "This perk is " +
                ChatColor.DARK_RED + "inactive" + ChatColor.LIGHT_PURPLE + ".").appendLore(lore).build();
    }
}
