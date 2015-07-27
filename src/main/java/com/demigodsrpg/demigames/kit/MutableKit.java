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
import com.demigodsrpg.demigames.impl.Demigames;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MutableKit implements Kit, Serializable {
    private static final long serialVersionUID = 1L;

    // -- DATA -- //

    String name;
    String contents;
    String armor;
    List<Map<String, Object>> effects;

    // -- CONSTRUCTOR -- //

    public MutableKit() {
    }

    public MutableKit(String name, ItemStack[] contents, ItemStack[] armor, PotionEffect[] effects) {
        this.name = name;
        this.contents = ItemUtil.serializeItemStacks(contents);
        this.armor = ItemUtil.serializeItemStacks(armor);
        this.effects = Arrays.asList(effects).stream().map(PotionEffect::serialize).collect(Collectors.toList());
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

    @Override
    public PotionEffect[] getPotionEffects() {
        PotionEffect[] effects = new PotionEffect[this.effects.size()];
        int i = 0;
        for (Map<String, Object> effect : this.effects) {
            effects[i] = new PotionEffect(fixEffect(effect));
            i++;
        }
        return effects;
    }

    // -- STATIC CONSTRUCTOR METHODS -- //

    public static MutableKit of(Kit kit) {
        if (kit instanceof MutableKit) {
            return (MutableKit) kit;
        }
        return new MutableKit(kit.getName(), kit.getContents(), kit.getArmor(), kit.getPotionEffects());
    }

    public static MutableKit of(String name, Player player) {
        MutableKit kit = new MutableKit();
        kit.name = name;
        kit.contents = ItemUtil.serializeItemStacks(player.getInventory().getContents());
        kit.armor = ItemUtil.serializeItemStacks(player.getInventory().getArmorContents());
        kit.effects = player.getActivePotionEffects().stream().map(PotionEffect::serialize).collect(Collectors.toList());
        return kit;
    }

    // -- PERSISTENCE METHOD -- //

    public void register() {
        Demigames.getKitRegistry().put(name, this);
    }

    // -- PRIVATE HELPER METHODS -- //

    private Map<String, Object> fixEffect(Map<String, Object> map) {
        int effect = (int) (double) map.get("effect");
        int duration = (int) (double) map.get("duration");
        int amplifier = (int) (double) map.get("amplifier");
        map.put("effect", effect);
        map.put("duration", duration);
        map.put("amplifier", amplifier);
        return map;
    }
}
