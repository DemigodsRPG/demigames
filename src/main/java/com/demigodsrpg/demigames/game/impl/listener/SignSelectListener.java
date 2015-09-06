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
import com.demigodsrpg.demigames.game.GameLocation;
import com.demigodsrpg.demigames.game.impl.registry.SignRegistry;
import com.demigodsrpg.demigames.sign.MutableMinigameSign;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Optional;

public class SignSelectListener implements Listener {

    // -- DATA -- //

    private Backend backend;
    private String name;
    private String gameName;
    private String command;
    private Player player;

    // -- CONSTRUCTOR -- //

    public SignSelectListener(Backend backend, String name, String gameName, String command, Player player) {
        this.backend = backend;
        this.name = name;
        this.gameName = gameName;
        this.command = command;
        this.player = player;
    }

    // -- EVENT LISTENERS -- //

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        // Unregister this listener when the player disconnects
        if (event.getPlayer().equals(player)) {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSelect(PlayerInteractEvent event) {
        if (event.getClickedBlock().getState() instanceof Sign && event.getPlayer().equals(player) &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<SignRegistry> opRegistry = backend.getSignRegistry(gameName);
            if (opRegistry.isPresent()) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                GameLocation location = new GameLocation(sign.getLocation(), true);
                opRegistry.get().put(location.toString(), new MutableMinigameSign(name, gameName,
                        location, command,
                        Arrays.asList(sign.getLines())));
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Sign " + name + " has been created!");
            } else {
                event.getPlayer().sendMessage(ChatColor.RED +
                        "There was an error getting the sign registry for this game.");
            }
            HandlerList.unregisterAll(this);
        }
    }
}
