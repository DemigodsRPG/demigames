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
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.impl.Setting;
import com.demigodsrpg.demigames.impl.lobby.Lobby;
import com.demigodsrpg.demigames.impl.registry.LocationRegistry;
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public interface Game extends Listener {

    // -- SETTINGS -- //

    String getName();

    String getDirectory();

    boolean canPlace();

    boolean canBreak();

    boolean canDrop();

    boolean canLateJoin();

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

    // -- SERVER START/STOP -- //

    default void onServerStart() {
    }

    default void onServerStop() {
    }

    // -- HELPER METHODS -- //

    default ConfigurationSection getConfig() {
        ConfigurationSection parent = Demigames.getInstance().getConfig();
        if (parent.getKeys(false).contains(getName())) {
            return parent.getConfigurationSection(getName());
        }
        return parent.createSection(getName());
    }

    default Optional<Session> checkPlayer(Player player) {
        Optional<Session> opSession = Demigames.getSessionRegistry().getSession(player);
        if (opSession.isPresent() && opSession.get().getGame().isPresent()) {
            if (opSession.get().getGame().get().equals(this)) {
                return opSession;
            }
        }
        return Optional.empty();
    }

    default Optional<GameLocation> getLocation(String name) {
        Optional<LocationRegistry> opReg = Demigames.getLocationRegistry(getName());
        if (opReg.isPresent()) {
            LocationRegistry reg = opReg.get();
            return reg.fromKey(name);
        }
        Demigames.getInstance().getLogger().warning(getName() + " is missing a Location Registry.");
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
        Optional<LocationRegistry> opReg = Demigames.getLocationRegistry(getName());
        if (opReg.isPresent()) {
            LocationRegistry reg = opReg.get();
            reg.put(name, location);
        } else {
            Demigames.getInstance().getLogger().warning(getName() + " is missing a Location Registry.");
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
        SessionRegistry sessions = Demigames.getSessionRegistry();
        Session session = null;
        if (sessions.fromGame(this).size() > 0) {
            // TODO The game finding algorithm needs to take into account the distribution of players
            Optional<Session> maybe = sessions.fromGame(this).stream().filter(Session::isJoinable).findAny();
            if (maybe.isPresent()) {
                session = maybe.get();
            }
        }
        if (session == null) {
            session = sessions.newSession(this);
            session.setupWorld();
            session.updateStage(DefaultStage.SETUP, true);
        }
        Optional<Session> previous;
        Optional<String> previousId = Demigames.getProfileRegistry().fromPlayer(player).getPreviousSessionId();
        if (previousId.isPresent()) {
            previous = sessions.fromKey(previousId.get());
        } else {
            previous = Optional.empty();
        }
        PlayerJoinMinigameEvent event = new PlayerJoinMinigameEvent(player, session, previous);
        Profile profile = Demigames.getProfileRegistry().fromPlayer(event.getPlayer());
        session.addProfile(profile);
        try {
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    default void quit(Player player, Session session, PlayerQuitMinigameEvent.QuitReason reason) {
        // Remove from profile
        Profile profile = Demigames.getProfileRegistry().fromPlayer(player);
        session.removeProfile(profile);

        // Lobby mode
        if ("lobby".equalsIgnoreCase(Setting.MODE)) {
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
