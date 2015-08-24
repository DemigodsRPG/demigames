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

package com.demigodsrpg.demigames.impl.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class ItemStackBuilder {

    // -- DATA -- //

    private ItemStack item;
    private ItemMeta meta;

    // -- CONSTRUCTORS -- //

    public ItemStackBuilder(Material type) {
        item = new ItemStack(type);
        meta = item.getItemMeta();
    }

    public ItemStackBuilder(MaterialData data) {
        item = new ItemStack(data.getItemType());
        item.setData(data);
        meta = item.getItemMeta();
    }

    // -- SETTERS -- //

    public ItemStackBuilder type(Material type) {
        item.setType(type);
        return this;
    }

    public ItemStackBuilder data(MaterialData data) {
        item.setData(data);
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStackBuilder durability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchant, int level) {
        meta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemStackBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemStackBuilder displayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemStackBuilder appendLore(String[] additionalLore) {
        List<String> lore = meta.getLore();
        lore.addAll(Arrays.asList(additionalLore));
        meta.setLore(lore);
        return this;
    }

    // -- BUILD -- //

    public ItemStack build() {
        ItemStack item = this.item.clone();
        item.setItemMeta(meta);
        return item;
    }
}
