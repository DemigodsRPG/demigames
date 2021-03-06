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

package com.demigodsrpg.demigames.game.impl.listener;

import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.kit.ImmutableKit;
import com.demigodsrpg.demigames.profile.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class KitListener implements Listener {

    private final Backend INST;

    public KitListener(Backend backend) {
        INST = backend;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getInventory().getHolder() instanceof Player &&
                ((Player) event.getInventory().getHolder()).getGameMode() != GameMode.CREATIVE) {
            Profile profile = INST.getProfileRegistry().fromPlayer(INST, (Player) event.getInventory().getHolder());
            if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
                event.setCancelled(true);
                ((Player) event.getInventory().getHolder()).updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        Profile profile = INST.getProfileRegistry().fromPlayer(INST, (Player) event.getWhoClicked());
        if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE && profile.getKit().isPresent() &&
                profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = INST.getProfileRegistry().fromPlayer(INST, (Player) event.getWhoClicked());
        if (!GameMode.CREATIVE.equals(event.getWhoClicked().getGameMode()) && profile.getKit().isPresent() &&
                profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(BlockPlaceEvent event) {
        Profile profile = INST.getProfileRegistry().fromPlayer(INST, event.getPlayer());
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && profile.getKit().isPresent() &&
                profile.getKit().get() instanceof ImmutableKit) {
            profile.getKit().get().applyItems(event.getPlayer());
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        Profile profile = INST.getProfileRegistry().fromPlayer(INST, event.getPlayer());
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && profile.getKit().isPresent() &&
                profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (Action.RIGHT_CLICK_BLOCK == event.getAction() || Action.RIGHT_CLICK_AIR == event.getAction()) {
            Player player = event.getPlayer();

            if (Material.POTION == player.getItemInHand().getType()) {
                event.setCancelled(true);

                ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
                potion.setItem(player.getItemInHand());

                int slot = player.getInventory().getHeldItemSlot();
                player.getInventory().setItem(slot, player.getItemInHand());
                player.updateInventory();
            }
        }
    }
}
