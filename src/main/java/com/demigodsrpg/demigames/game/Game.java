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
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;

public interface Game extends Listener {
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

    default int getNumberOfTeams() {
        return 0;
    }

    default int getTotalRounds() {
        return 1;
    }

    void setupLocations(Session session);

    default void onServerStart() {
    }

    default void onServerStop() {
    }

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

    default void callWin(Session session, Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerWinMinigameEvent(player, session));
    }

    default void callLose(Session session, Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerLoseMinigameEvent(player, session));
    }

    default void callTie(Session session, Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerTieMinigameEvent(player, session));
    }

    default void join(Player player) {
        SessionRegistry sessions = Demigames.getSessionRegistry();
        Session session = null;
        if (sessions.fromGame(this).size() > 0) {
            Optional<Session> maybe = sessions.fromGame(this).stream().filter(Session::isJoinable).findAny();
            if (maybe.isPresent()) {
                session = maybe.get();
            }
        }
        if (session == null) {
            session = sessions.newSession(this);
            session.updateStage(DefaultStage.SETUP, true);
        }
        Optional<Session> previous;
        Optional<String> previousId = Demigames.getProfileRegistry().fromPlayer(player).getCurrentSessionId();
        if (previousId.isPresent()) {
            previous = sessions.fromKey(previousId.get());
        } else {
            previous = Optional.empty();
        }
        PlayerJoinMinigameEvent event = new PlayerJoinMinigameEvent(player, session, previous);
        Profile profile = Demigames.getProfileRegistry().fromPlayer(event.getPlayer());
        session.addProfile(profile);
        profile.setCurrentSessionId(session.getId());
        Bukkit.getPluginManager().callEvent(event);
    }

    default void quit(Player player, PlayerQuitMinigameEvent.QuitReason reason) {
        SessionRegistry sessions = Demigames.getSessionRegistry();
        Session session = null;
        if (sessions.fromGame(this).size() > 0) {
            Optional<Session> maybe = sessions.fromGame(this).stream().filter(Session::isJoinable).findAny();
            if (maybe.isPresent()) {
                session = maybe.get();
            }
        }
        if (session == null) {
            session = sessions.newSession(this);
        }
        PlayerQuitMinigameEvent event = new PlayerQuitMinigameEvent(player, session, reason);
        session.removeProfile(event.getPlayer());
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onJoin(PlayerJoinMinigameEvent event);

    @EventHandler(priority = EventPriority.MONITOR)
    void onLeave(PlayerQuitMinigameEvent event);
}
