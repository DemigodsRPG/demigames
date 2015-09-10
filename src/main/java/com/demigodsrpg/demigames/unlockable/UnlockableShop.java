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
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.GameLocation;
import com.demigodsrpg.demigames.game.impl.util.ItemStackBuilder;
import com.demigodsrpg.demigames.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public class UnlockableShop implements Serializable {

    // -- TRANSIENT DATA -- //

    transient ItemStack kitShopTab;
    transient ItemStack unlockableShopTab;
    transient ItemStack pane;

    // -- DATA -- //

    String location;
    String name;
    String kitGameName;

    public UnlockableShop() {
        processItems();
    }

    public UnlockableShop(GameLocation location, String name, String kitGameName) {
        this.location = location.toString();
        this.name = name;
        this.kitGameName = kitGameName;
        processItems();
    }

    private Optional<Game> getGame(Backend backend) {
        return backend.getGameRegistry().getMinigame(kitGameName);
    }

    private void processItems() {
        kitShopTab = new ItemStackBuilder(Material.ENDER_CHEST).displayName(ChatColor.GREEN + "Kit Shop").
                lore(ChatColor.LIGHT_PURPLE + "Click this to open", ChatColor.LIGHT_PURPLE + "the kit shop.").
                build();
        unlockableShopTab = new ItemStackBuilder(Material.DIAMOND).displayName(ChatColor.DARK_AQUA + "Unlockables Shop").
                lore(ChatColor.LIGHT_PURPLE + "Click this to open", ChatColor.LIGHT_PURPLE + "the unlockables shop.").
                build();
        pane = new ItemStackBuilder(Material.STAINED_GLASS_PANE).displayName(" ").lore(" ").build();
    }

    public ItemStack getPane(int data) {
        ItemStack stack = pane.clone();
        stack.setDurability((byte) data);
        return stack;
    }

    public void openShop(Backend backend, Player p, boolean message) {
        //Create the inv
        Inventory inv = Bukkit.createInventory(p, 9 * 5, ChatColor.DARK_AQUA + "Minegusta " +
                ChatColor.LIGHT_PURPLE + "Shop");

        //Fill the inv with unlockables that the player does not have already.

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, getPane(i));
        }

        int data = 0;
        for (int i = 36; i < 45; i++) {
            inv.setItem(i, getPane(data));
            data++;
        }

        Profile profile = backend.getProfileRegistry().fromPlayer(backend, p);

        int slot = 9;
        for (Unlockable u : Unlockables.valuesSansKits()) {
            if (profile.hasUnlockable(u)) continue;
            inv.setItem(slot, u.getLockedItem());
            slot++;
        }

        //Set the item for the kit tab.
        inv.setItem(35, kitShopTab);

        //Set the task + open the inv for the player
        p.openInventory(inv);
        if (message)
            backend.sendTaggedMessage(p, ChatColor.LIGHT_PURPLE + "You have " + ChatColor.DARK_PURPLE +
                    profile.getTickets() + ChatColor.LIGHT_PURPLE + " tickets.", ChatColor.GRAY + "Use " +
                    ChatColor.YELLOW + "/Rewards " + ChatColor.GRAY + "to select your active perks.");
        //TODO ShopTask.addInventory(inv);
    }

    public void openKitShop(Backend backend, Player p, boolean message) {
        //Create the inv
        Inventory inv = Bukkit.createInventory(p, 9 * 5, ChatColor.DARK_RED + "DG" + ChatColor.GRAY + "-" +
                ChatColor.DARK_AQUA + "MG " + ChatColor.LIGHT_PURPLE + "Kits");

        Profile profile = backend.getProfileRegistry().fromPlayer(backend, p);

        //Fill the inv with unlockables that the player has

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, getPane(i));
        }

        int data = 0;
        for (int i = 36; i < 45; i++) {
            inv.setItem(i, getPane(data));
            data++;
        }

        int slot = 9;
        if (getGame(backend).isPresent()) {
            for (UnlockableKit kit : Unlockables.kits()) {
                if (profile.hasUnlockable(kit) || getGame(backend).get().getDefaultUnlockables().
                        contains(kit.getName())) {
                    continue;
                }
                inv.setItem(slot, getKitItem(kit.getName(), kit.getCost()));
                slot++;
            }
        }

        //Set the item for the normal tab.
        inv.setItem(35, unlockableShopTab);

        //Set the task + open the inv for the player
        p.openInventory(inv);
        if (message) backend.sendTaggedMessage(p, ChatColor.LIGHT_PURPLE + "You opened the kit menu.");
        //TODO hopTask.addInventory(inv);
    }

    public void update(Inventory i) {
        for (ItemStack item : i.getContents()) {
            if (item != null && item.getType() == Material.STAINED_GLASS_PANE) {
                int data = item.getDurability();
                data++;
                if (data > 15) data = 0;
                item.setDurability((short) data);
            }
        }
    }

    private ItemStack getKitItem(String name, int cost) {
        return new ItemStack(Material.TRAPPED_CHEST, 1) {
            {
                ItemMeta meta = getItemMeta();
                meta.setDisplayName(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + name);
                meta.setLore(Arrays.asList(ChatColor.GREEN + "Click this to", ChatColor.GREEN + "unlock " + name + ".",
                        ChatColor.LIGHT_PURPLE + "Cost: " + ChatColor.DARK_PURPLE + cost + " Tickets"));
                setItemMeta(meta);
            }
        };
    }
}
