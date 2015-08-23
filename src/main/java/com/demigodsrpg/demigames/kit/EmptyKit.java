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
import org.bukkit.potion.PotionEffect;

public class EmptyKit implements Kit {

    // -- CONSTRUCTOR -- //

    EmptyKit() {
    }

    // -- GETTERS -- //

    @Override
    public String getName() {
        return "Empty";
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[0];
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[4];
    }

    @Override
    public PotionEffect[] getPotionEffects() {
        return new PotionEffect[0];
    }

    @Override
    public double getHealthScale() {
        return 20.0D;
    }

    @Override
    public double getMaxHealth() {
        return 20.0D;
    }

    @Override
    public double getHealth() {
        return 20.0D;
    }

    @Override
    public int getMaximumAir() {
        return 20;
    }

    @Override
    public int getRemainingAir() {
        return 20;
    }

    @Override
    public int getFoodLevel() {
        return 20;
    }

    @Override
    public float getExhaustion() {
        return 0F;
    }

    @Override
    public float getSaturation() {
        return 20F;
    }

    @Override
    public int getFireTicks() {
        return 0;
    }

    @Override
    public int getTotalExperience() {
        return 0;
    }
}
