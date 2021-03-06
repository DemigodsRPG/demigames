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
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Optional;

public class GameListener implements Listener {

    private final Backend INST;

    public GameListener(Backend backend) {
        INST = backend;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            Player player = (Player) event.getInventory().getHolder();
            Optional<Session> opSession = INST.getSessionRegistry().getSession(player);
            if (opSession.isPresent()) {
                Session session = opSession.get();
                if (session.getGame().isPresent() && !session.getGame().get().canDrop()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        Optional<Session> opSession = INST.getSessionRegistry().getSession(event.getPlayer());
        if (opSession.isPresent()) {
            Session session = opSession.get();
            if (session.getGame().isPresent() && !session.getGame().get().canDrop()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Optional<Session> opSession = INST.getSessionRegistry().getSession(event.getPlayer());
        if (opSession.isPresent()) {
            Session session = opSession.get();
            if (session.getGame().isPresent() && !session.getGame().get().canPlace()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Optional<Session> opSession = INST.getSessionRegistry().getSession(event.getPlayer());
        if (opSession.isPresent()) {
            Session session = opSession.get();
            if (session.getGame().isPresent() && !session.getGame().get().canPlace()) {
                event.setCancelled(true);
            }
        }
    }
}
