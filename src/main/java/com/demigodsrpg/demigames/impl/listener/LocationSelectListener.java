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
import com.demigodsrpg.demigames.impl.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LocationSelectListener implements Listener {

    // -- DATA -- //

    private String name;
    private String gameName;
    private Player player;

    // -- CONSTRUCTOR -- //

    public LocationSelectListener(String name, String gameName, Player player) {
        this.name = name;
        this.gameName = gameName;
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
        if (event.getPlayer().equals(player) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Demigames.getInstance().getConfig().set(gameName + ".loc." + name,
                    LocationUtil.stringFromLocation(player.getLocation(), true));
            Demigames.getInstance().saveConfig();
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Location " + name + " has been created!");
            HandlerList.unregisterAll(this);
        }
    }
}
