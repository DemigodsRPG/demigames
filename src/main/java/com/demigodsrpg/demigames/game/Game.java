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

package com.demigodsrpg.demigames.game;

import com.demigodsrpg.demigames.event.*;
import com.demigodsrpg.demigames.game.impl.registry.LocationRegistry;
import com.demigodsrpg.demigames.game.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.game.lobby.Lobby;
import com.demigodsrpg.demigames.game.lobby.LobbySession;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface Game extends Listener {

    // -- SETTINGS -- //

    String getName();

    String getDirectory();

    GameMode getDefaultGamemode();

    boolean canPlace();

    boolean canBreak();

    boolean canDrop();

    default boolean hasSpectateChat() {
        return false;
    }

    int getMinimumPlayers();

    int getMaximumPlayers();

    default int getNumberOfTeams() {
        return 0;
    }

    default int getTotalRounds() {
        return 1;
    }

    default List<String> getJoinableStages() {
        return Arrays.asList(DefaultStage.SETUP, DefaultStage.WARMUP);
    }

    List<String> getDefaultUnlockables();

    default List<String> getExcludedUnlockables() {
        return new ArrayList<>();
    }

    // -- SERVER START/STOP -- //

    default void onServerStart() {
    }

    default void onServerStop() {
    }

    // -- HELPER METHODS -- //

    default Backend getBackend() {
        return Backend.INST;
    }

    default ConfigurationSection getConfig() {
        ConfigurationSection parent = Backend.INST.getConfig();
        if (parent.getKeys(false).contains(getName())) {
            return parent.getConfigurationSection(getName());
        }
        return parent.createSection(getName());
    }

    default Optional<Session> checkPlayer(Player player) {
        Optional<Session> opSession = getBackend().getSessionRegistry().getSession(player);
        if (opSession.isPresent() && opSession.get().getGame().isPresent()) {
            if (opSession.get().getGame().get().equals(this)) {
                return opSession;
            }
        }
        return Optional.empty();
    }

    default Optional<GameLocation> getLocation(String name) {
        Optional<LocationRegistry> opReg = getBackend().getLocationRegistry(getName());
        if (opReg.isPresent()) {
            LocationRegistry reg = opReg.get();
            return reg.fromKey(name);
        }
        Backend.INST.getLogger().warning(getName() + " is missing a Location Registry.");
        return Optional.empty();
    }

    default GameLocation getLocation(String name, Location fallback) {
        Optional<GameLocation> opLoc = getLocation(name);
        if (opLoc.isPresent()) {
            return opLoc.get();
        }
        return new GameLocation(fallback, false);
    }

    default void setLocation(String name, GameLocation location) {
        Optional<LocationRegistry> opReg = getBackend().getLocationRegistry(getName());
        if (opReg.isPresent()) {
            LocationRegistry reg = opReg.get();
            reg.put(name, location);
        } else {
            Backend.INST.getLogger().warning(getName() + " is missing a Location Registry.");
        }
    }

    // -- DEFAULT BEHAVIOR -- //

    default void callWin(Session session, Player player) {
        try {
            Bukkit.getPluginManager().callEvent(new PlayerWinMinigameEvent(player, session));
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    default void callLose(Session session, Player player) {
        try {
            Bukkit.getPluginManager().callEvent(new PlayerLoseMinigameEvent(player, session));
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    default void callTie(Session session, Player player) {
        try {
            Bukkit.getPluginManager().callEvent(new PlayerTieMinigameEvent(player, session));
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    default void join(Player player) {
        SessionRegistry sessions = getBackend().getSessionRegistry();
        Session session = null;
        if (sessions.fromGame(this).size() > 0) {
            Optional<Session> maybe = sessions.fromGame(this).stream().filter(Session::isJoinable).findAny();
            if (maybe.isPresent()) {
                if (getJoinableStages().isEmpty() || getJoinableStages().contains(maybe.get().getStage())) {
                    session = maybe.get();
                }
            }
        }
        if (session == null) {
            session = sessions.newSession(this);
            session.setupWorld();
            session.updateStage(DefaultStage.SETUP, true);
        }
        Optional<Session> previous;
        Optional<String> previousId = getBackend().getProfileRegistry().fromPlayer(getBackend(), player).
                getPreviousSessionId();
        if (previousId.isPresent()) {
            previous = sessions.fromKey(previousId.get());
        } else {
            previous = Optional.empty();
        }
        PlayerJoinMinigameEvent event = new PlayerJoinMinigameEvent(player, session, previous);
        Profile profile = getBackend().getProfileRegistry().fromPlayer(getBackend(), event.getPlayer());
        session.addProfile(profile);
        player.setGameMode(getDefaultGamemode());
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    default void quit(Player player, Session session, PlayerQuitMinigameEvent.QuitReason reason) {
        // Remove from profile
        Profile profile = getBackend().getProfileRegistry().fromPlayer(getBackend(), player);
        session.removeProfile(profile);

        // Lobby mode
        if ("lobby".equalsIgnoreCase(Setting.MODE) && !(session instanceof LobbySession)) {
            Lobby.LOBBY.join(player);
        }

        // Create and call the event
        PlayerQuitMinigameEvent event = new PlayerQuitMinigameEvent(player, session, reason);
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    // -- REQUIRED EVENT LISTENERS -- //

    @EventHandler(priority = EventPriority.LOW)
    void onJoin(PlayerJoinMinigameEvent event);

    @EventHandler(priority = EventPriority.LOW)
    void onLeave(PlayerQuitMinigameEvent event);
}
