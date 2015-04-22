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
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.impl.registry.ProfileRegistry;
import com.demigodsrpg.demigames.profile.Profile;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Session implements Serializable {

    // -- DATA -- //

    protected List<String> profiles = new ArrayList<>();
    protected String id;
    protected String stage;

    // -- CONSTRUCTOR -- //

    public Session() {
    }

    // -- GETTERS -- //

    public String getId() {
        return id;
    }

    public String getStage() {
        return stage;
    }

    public List<Profile> getProfiles() {
        ProfileRegistry registry = DemigamesPlugin.getProfileRegistry();
        return profiles.stream().map(registry::fromId).collect(Collectors.toList());
    }

    public Optional<Game> getGame() {
        return DemigamesPlugin.getGameRegistry().getSessionGame(this);
    }

    // -- MUTATORS -- //

    public void addProfile(Profile profile) {
        profiles.add(profile.getMojangUniqueId());
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getMojangUniqueId());
    }

    public void removeProfile(Player player) {
        profiles = profiles.parallelStream().filter(profile -> !profile.equals(player.getUniqueId().toString())).collect(Collectors.toList());
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void updateStage(String stage, boolean process) {
        if (getGame().isPresent()) {
            DemigamesPlugin.getGameRegistry().updateStage(getGame().get(), this, stage, process);
        } else {
            throw new NullPointerException("A session is missing its respective game!");
        }
    }
}
