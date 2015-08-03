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

package com.demigodsrpg.demigames.impl.listener;

import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.kit.ImmutableKit;
import com.demigodsrpg.demigames.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class KitListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Profile profile = Demigames.getProfileRegistry().fromPlayer((Player) event.getInventory().getHolder());
            if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
                event.setCancelled(true);
                ((Player) event.getInventory().getHolder()).updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        Profile profile = Demigames.getProfileRegistry().fromPlayer((Player) event.getWhoClicked());
        if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Profile profile = Demigames.getProfileRegistry().fromPlayer((Player) event.getWhoClicked());
        if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(BlockPlaceEvent event) {
        Profile profile = Demigames.getProfileRegistry().fromPlayer(event.getPlayer());
        if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
            profile.getKit().get().applyItems(event.getPlayer());
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent event) {
        Profile profile = Demigames.getProfileRegistry().fromPlayer(event.getPlayer());
        if (profile.getKit().isPresent() && profile.getKit().get() instanceof ImmutableKit) {
            event.setCancelled(true);
        }
    }
}
