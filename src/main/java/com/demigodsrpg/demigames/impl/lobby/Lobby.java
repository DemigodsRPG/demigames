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

package com.demigodsrpg.demigames.impl.lobby;

import com.demigodsrpg.demigames.event.PlayerJoinMinigameEvent;
import com.demigodsrpg.demigames.event.PlayerQuitMinigameEvent;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.GameLocation;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.impl.registry.SignRegistry;
import com.demigodsrpg.demigames.kit.ImmutableKit;
import com.demigodsrpg.demigames.kit.Kit;
import com.demigodsrpg.demigames.kit.MutableKit;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.sign.MinigameSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Lobby implements Game {
    public static Lobby LOBBY;
    public static Kit LOBBY_KIT;

    protected World WORLD = Bukkit.getWorld("world"); //Bukkit.getWorlds().get(0);
    private Location SPAWN;

    @Override
    public void onServerStart() {
        GameLocation gameSpawnPoint = getLocation("spawn", WORLD.getSpawnLocation());
        Optional<Location> spawnPoint = gameSpawnPoint.toLocation(WORLD);
        if (spawnPoint.isPresent()) {
            SPAWN = spawnPoint.get();
        } else {
            throw new NullPointerException("No world found for the Lobby.");
        }

        Optional<MutableKit> opKit = Demigames.getKitRegistry().fromKey("lobby");
        if (opKit.isPresent()) {
            LOBBY_KIT = ImmutableKit.of(opKit.get());
        } else {
            LOBBY_KIT = Kit.IMMUTABLE_EMPTY;
        }
    }

    @Override
    public String getName() {
        return "Lobby";
    }

    @Override
    public String getDirectory() {
        return "";
    }

    @Override
    public boolean canPlace() {
        return false;
    }

    @Override
    public boolean canBreak() {
        return false;
    }

    @Override
    public boolean canDrop() {
        return false;
    }

    @Override
    public boolean canLateJoin() {
        return true;
    }

    @Override
    public boolean hasSpectateChat() {
        return false;
    }

    @Override
    public int getMinimumPlayers() {
        return 0;
    }

    @Override
    public int getMaximumPlayers() {
        return 200; // TODO Find a better value for this
    }

    @Override
    public int getNumberOfTeams() {
        return 0;
    }

    @Override
    public int getTotalRounds() {
        return 0;
    }

    @Override
    public List<String> defaultUnlockables() {
        return new ArrayList<>(); // TODO Rainbow armor?
    }

    public Location getSpawn() {
        return SPAWN;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinMinigameEvent event) {
        if (event.getGame().isPresent() && event.getGame().get().equals(this)) {
            Player player = event.getPlayer();
            LOBBY_KIT.apply(player, true);
            player.teleport(SPAWN);
            player.sendMessage(ChatColor.GOLD + "Welcome to the Minigames Lobby.");
            if (event.getPreviusSession().isPresent() && event.getPreviusSession().get().getGame().isPresent()) {
                Game previousGame = event.getPreviusSession().get().getGame().get();
                if (!(previousGame instanceof Lobby)) {
                    player.sendMessage(ChatColor.GREEN + "Did you have fun playing " + previousGame.getName() + "?");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitMinigameEvent event) {
        if (event.getGame().isPresent() && event.getGame().get().equals(this)) {
            event.getPlayer().sendMessage("THE LOBBY MISSES YOU :C");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Optional<Session> opSession = checkPlayer(event.getPlayer());
            if (opSession.isPresent()) {
                if (opSession.get() instanceof LobbySession) {
                    Optional<SignRegistry> opRegistry = Demigames.getSignRegistry(getName());
                    if (opRegistry.isPresent()) {
                        Optional<? extends MinigameSign> opSign = opRegistry.get().fromLocation(
                                event.getClickedBlock().getLocation());
                        if (opSign.isPresent()) {
                            String command = opSign.get().getCommand();
                            event.getPlayer().performCommand(command);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
