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

import com.censoredsoftware.library.bukkitutil.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class MutableKit implements Kit, Serializable {
    private static final long serialVersionUID = 1L;

    // -- DATA -- //

    private String name;
    private String contents;
    private String armor;

    // -- CONSTRUCTOR -- //

    public MutableKit() {
    }

    public MutableKit(String name, ItemStack[] contents, ItemStack[] armor) {
        this.name = name;
        this.contents = ItemUtil.serializeItemStacks(contents);
        this.armor = ItemUtil.serializeItemStacks(armor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack[] getContents() {
        return ItemUtil.deserializeItemStacks(contents);
    }

    @Override
    public ItemStack[] getArmor() {
        return ItemUtil.deserializeItemStacks(armor);
    }

    // -- STATIC CONSTRUCTOR METHOD -- //

    public static MutableKit of(Kit kit) {
        if (kit instanceof MutableKit) {
            return (MutableKit) kit;
        }
        return new MutableKit(kit.getName(), kit.getContents(), kit.getArmor());
    }
}
