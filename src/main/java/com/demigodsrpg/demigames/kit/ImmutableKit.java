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

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ImmutableKit implements Kit {

    // -- DATA -- //

    private final String name;
    private final ItemStack[] contents;
    private final ItemStack[] armor;

    // -- PRIVATE CONSTRUCTOR -- //

    private ImmutableKit(String name, ItemStack[] contents, ItemStack[] armor) {
        this.name = name;
        this.contents = contents;
        this.armor = armor;
    }

    // -- GETTERS -- //

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack[] getContents() {
        return Arrays.copyOf(contents, contents.length);
    }

    @Override
    public ItemStack[] getArmor() {
        return Arrays.copyOf(armor, armor.length);
    }

    // -- STATIC CONSTRUCTOR METHOD -- //

    public static ImmutableKit of(Kit kit) {
        return new ImmutableKit(kit.getName(), kit.getContents(), kit.getArmor());
    }
}
