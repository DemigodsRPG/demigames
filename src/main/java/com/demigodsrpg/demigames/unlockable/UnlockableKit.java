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

import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.impl.util.ItemStackBuilder;
import com.demigodsrpg.demigames.kit.ImmutableKit;
import com.demigodsrpg.demigames.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;


public class UnlockableKit implements Unlockable {

    // -- DATA -- //

    private Kit kit;
    private ItemStack lockedItem;
    private ItemStack unlockedItem;

    String kitName;
    int cost;
    String[] lore;
    boolean immutable;
    boolean excludedByDefault;

    public UnlockableKit(Backend backend, String kitName, int cost, String[] lore, boolean immutable,
                         boolean excludedByDefault, Material type, byte data) {
        this.kitName = kitName;
        this.cost = cost;
        this.lore = lore;
        this.immutable = immutable;
        this.excludedByDefault = excludedByDefault;

        processKit(backend);
        processItems(new MaterialData(type, data));
    }

    @Override
    public String getName() {
        return kitName;
    }

    @Override
    public String[] getLore() {
        return lore;
    }

    @Override
    public ItemStack getLockedItem() {
        return lockedItem;
    }

    @Override
    public ItemStack getUnlockedItem(boolean ignored) {
        return unlockedItem;
    }

    @Override
    public int getCost() {
        return cost;
    }

    public Kit getKit() {
        return kit;
    }

    public boolean isImmutable() {
        return immutable;
    }

    @Override
    public boolean isExcludedByDefault() {
        return excludedByDefault;
    }

    // -- PRIVATE HELPER METHODS -- //

    private void processKit(Backend backend) {
        Kit kit = backend.getKitRegistry().fromKeyOrEmpty(kitName);
        if (immutable) {
            this.kit = ImmutableKit.of(kit);
        } else {
            this.kit = kit;
        }
    }

    private void processItems(MaterialData data) {
        lockedItem = new ItemStackBuilder(data).displayName(kitName).
                lore(ChatColor.LIGHT_PURPLE + "Cost: " + ChatColor.DARK_PURPLE + getCost() + " Tickets").
                appendLore(lore).build();
        unlockedItem = new ItemStackBuilder(data).displayName(kitName).
                lore(ChatColor.LIGHT_PURPLE + "You have unlocked this kit.").appendLore(lore).build();
    }
}
