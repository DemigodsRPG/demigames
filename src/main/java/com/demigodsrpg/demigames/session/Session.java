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

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.impl.Setting;
import com.demigodsrpg.demigames.impl.registry.ProfileRegistry;
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Session implements Serializable {

    // -- DATA -- //

    protected transient Optional<Game> game;
    protected List<String> profiles = new ArrayList<>();
    protected Map<String, Object> data;
    protected String id;
    protected String stage;
    protected int currentRound;

    // -- CONSTRUCTOR -- //

    public Session(String id, Game game) {
        this.id = id;
        this.game = Optional.of(game);
        this.stage = DefaultStage.SETUP;
        this.data = new HashMap<>();
    }

    public Session(String id, Game game, String stage) {
        this.id = id;
        this.game = Optional.of(game);
        this.stage = stage;
        this.data = new HashMap<>();
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
        ProfileRegistry registry = Demigames.getProfileRegistry();
        return profiles.stream().map(registry::fromKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Deprecated
    public List<Player> getPlayers() {
        return getProfiles().stream().filter(profile -> profile.getPlayer().isPresent()).map(profile ->
                profile.getPlayer().get()).collect(Collectors.toList());
    }

    public Optional<Game> getGame() {
        return game;
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(id));
    }

    public Map<String, Object> getData() {
        return data;
    }

    // -- MUTATORS -- //

    public void addProfile(Profile profile) {
        profile.setCurrentSessionId(id);
        profiles.add(profile.getMojangUniqueId());
        Demigames.getSessionRegistry().put(id, this);
    }

    public void addProfiles(List<Profile> profiles) {
        profiles.forEach(profile -> profile.setCurrentSessionId(id));
        this.profiles.addAll(profiles.stream().map(Profile::getMojangUniqueId).collect(Collectors.toList()));
        Demigames.getSessionRegistry().put(id, this);
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getMojangUniqueId());
        Demigames.getSessionRegistry().put(id, this);
    }

    public void removeProfile(Player player) {
        profiles = profiles.parallelStream().filter(profile -> !profile.equals(player.getUniqueId().toString())).collect(Collectors.toList());
        Demigames.getSessionRegistry().put(id, this);
    }

    public void setStage(String stage) {
        this.stage = stage;
        Demigames.getSessionRegistry().put(id, this);
    }

    public void updateStage(String stage, boolean process) {
        if (getGame().isPresent()) {
            Demigames.getGameRegistry().updateStage(getGame().get(), this, stage, process);
        } else {
            throw new NullPointerException("A session is missing its respective game!");
        }
        Demigames.getSessionRegistry().put(id, this);
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
        Demigames.getSessionRegistry().put(id, this);
    }

    public void endSession(boolean nextGame) {
        SessionRegistry registry = Demigames.getSessionRegistry();
        registry.removeIfPresent(id);

        // Party mode
        if ("party".equals(Setting.MODE) && nextGame) {
            Optional<Game> opGame = Demigames.getGameRegistry().randomGame();
            if (opGame.isPresent()) {
                Session newSession = registry.newSession(opGame.get());
                newSession.profiles.addAll(profiles);
                newSession.updateStage(DefaultStage.SETUP, true);
            }
        }
        // Lobby mode, or empty party
        else {
            getPlayers().forEach(player -> player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation())); // TODO
        }

        // Unload the world
        registry.unloadWorld(this);
    }
}
