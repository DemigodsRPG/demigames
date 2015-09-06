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

package com.demigodsrpg.demigames.session;

import com.demigodsrpg.demigames.event.PlayerQuitMinigameEvent;
import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.Setting;
import com.demigodsrpg.demigames.game.impl.registry.ProfileRegistry;
import com.demigodsrpg.demigames.game.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.game.lobby.Lobby;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Session implements Serializable {

    // -- TRANSIENT DATA -- //

    protected transient Backend backend;
    protected transient Optional<Game> game;
    protected transient boolean done = false;

    // -- DATA -- //

    protected Set<String> profiles = new HashSet<>();
    protected String id;
    protected String stage;
    protected boolean joinable;
    protected int currentRound;
    protected Map<String, Serializable> data;

    // -- CONSTRUCTORS -- //

    public Session(String id, Game game) {
        this.id = id;
        this.backend = game.getBackend();
        this.game = Optional.ofNullable(game);
        this.stage = DefaultStage.SETUP;
        this.data = new HashMap<>();
        this.joinable = true;
    }

    public Session(String id, Game game, String stage) {
        this.id = id;
        this.backend = game.getBackend();
        this.game = Optional.of(game);
        this.stage = stage;
        this.data = new HashMap<>();
        this.joinable = true;
    }

    // -- GETTERS -- //

    public String getId() {
        return id;
    }

    public String getStage() {
        return stage;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public List<Profile> getProfiles() {
        ProfileRegistry registry = backend.getProfileRegistry();
        return profiles.stream().map(registry::fromKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public Set<String> getRawProfiles() {
        return profiles;
    }

    @Deprecated
    public List<Player> getPlayers() {
        return getProfiles().stream().filter(profile -> profile.getPlayer().isPresent()).map(profile ->
                profile.getPlayer().get()).collect(Collectors.toList());
    }

    public Optional<Game> getGame() {
        return game;
    }

    public Backend getBackend() {
        return backend;
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(id));
    }

    public Map<String, Serializable> getData() {
        return data;
    }

    public boolean isJoinable() {
        return joinable && !isDone();
    }

    public boolean isDone() {
        if (!done) {
            Optional<Session> found = backend.getSessionRegistry().fromKey(id);
            if (found.isPresent()) {
                return found.get().done;
            }
        }
        return done;
    }

    // -- MUTATORS -- //

    public Optional<World> setupWorld() {
        return backend.getSessionRegistry().setupWorld(this);
    }

    public void setDone() {
        if (!done) {
            this.done = true;
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void addProfile(Profile profile) {
        profile.setCurrentSessionId(backend, id);
        profiles.add(profile.getMojangUniqueId());
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void addProfiles(List<Profile> profiles) {
        profiles.forEach(profile -> profile.setCurrentSessionId(backend, id));
        this.profiles.addAll(profiles.stream().map(Profile::getMojangUniqueId).collect(Collectors.toList()));
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void setRawProfiles(Set<String> rawProfiles) {
        this.profiles = rawProfiles;
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getMojangUniqueId());
        if (profile.getCurrentSessionId().isPresent() && profile.getCurrentSessionId().get().equals(id)) {
            profile.setCurrentSessionId(backend, null);
            profile.setPreviousSessionId(backend, id);
        }
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void removeProfile(Player player) {
        profiles = profiles.parallelStream().filter(profile -> !profile.equals(player.getUniqueId().toString())).
                collect(Collectors.toSet());
        Profile profile = backend.getProfileRegistry().fromPlayer(backend, player);
        if (profile.getCurrentSessionId().isPresent() && profile.getCurrentSessionId().get().equals(id)) {
            profile.setCurrentSessionId(backend, null);
            profile.setPreviousSessionId(backend, id);
        }
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void setStage(String stage) {
        this.stage = stage;
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void updateStage(String stage, boolean process) {
        if (getGame().isPresent()) {
            backend.getGameRegistry().updateStage(getGame().get(), this, stage, process);
        } else {
            throw new NullPointerException("A session is missing its respective game!");
        }
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
        if (!isDone()) {
            backend.getSessionRegistry().put(id, this);
        }
    }

    public void endSession() {
        SessionRegistry registry = backend.getSessionRegistry();

        // Make sure this isn't a duplicate
        if (!done) {
            // Set this to done to make sure nothing is overwritten
            setDone();

            // Make all players quit the game
            getPlayers().forEach(player -> {
                if (getGame().isPresent()) {
                    getGame().get().quit(player, this, PlayerQuitMinigameEvent.QuitReason.SESSION_END);
                } else {
                    Profile profile = backend.getProfileRegistry().fromPlayer(backend, player);
                    profile.setCurrentSessionId(backend, null);
                    profile.setPreviousSessionId(backend, id);
                }
            });

            // Party mode
            if ("party".equals(Setting.MODE)) {
                Optional<Game> opGame = backend.getGameRegistry().randomGame();
                if (opGame.isPresent()) {
                    Session newSession = registry.newSession(opGame.get());
                    newSession.setRawProfiles(profiles);
                    newSession.updateStage(DefaultStage.SETUP, true);
                    getPlayers().forEach(opGame.get()::join);
                } else {
                    getPlayers().forEach(Lobby.LOBBY::join);
                }
            }

            // Remove the file and unload the world
            backend.getLogger().info("Unloading session " + id + ".");
            registry.remove(id);
            registry.unloadWorld(this);
        }
    }
}
