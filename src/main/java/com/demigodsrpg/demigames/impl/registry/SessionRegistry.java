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

package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class SessionRegistry extends AbstractRegistry<String, Session> {

    // -- CONSTRUCTOR -- //

    public SessionRegistry() {
        super("session", Session.class);
    }

    // -- GETTERS -- //

    public Session newSession(Game game) {
        String keyId = UUID.randomUUID().toString();
        Session newSession = new Session(keyId, game);
        return put(keyId, newSession);
    }

    public Session newSession(Game game, String stage) {
        String keyId = UUID.randomUUID().toString();
        Session newSession = new Session(keyId, game, stage);
        return put(keyId, newSession);
    }

    public List<Session> fromGame(Game game) {
        if (game != null) {
            return REGISTERED_DATA.asMap().values().parallelStream().filter(session -> {
                Optional<Game> foundGame = session.getGame();
                return foundGame.isPresent() && foundGame.get().equals(game);
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public Optional<Session> getSession(Player player) {
        Optional<Profile> opProfile = Demigames.getProfileRegistry().fromKey(player.getUniqueId().toString());
        if (opProfile.isPresent()) {
            Optional<String> opId = opProfile.get().getCurrentSessionId();
            if (opId.isPresent()) {
                return fromKey(opId.get());
            }
        }
        return Optional.empty();
    }

    // -- WORLD STUFF -- //

    public Optional<World> setupWorld(Session session) {
        if (session.getGame().isPresent()) {
            Game game = session.getGame().get();

            // Copy world from file
            File file = new File(Demigames.getInstance().getDataFolder().getPath() + "/worlds/" + game.getDirectory() + "/");
            try {
                FileUtils.copyDirectory(file, new File(session.getId()), true);
            } catch (Exception oops) {
                oops.printStackTrace();
            }

            // Load new world
            return Optional.ofNullable(new WorldCreator(session.getId()).createWorld());
        }
        return Optional.empty();
    }

    public void unloadWorld(Session session) {
        // Unregister old worlds
        if (Bukkit.getWorld(session.getId()) != null) {
            Bukkit.unloadWorld(session.getId(), false);
        }

        // Delete the old world
        Bukkit.getScheduler().scheduleSyncDelayedTask(Demigames.getInstance(), () -> {
            try {
                FileUtils.deleteDirectory(new File(session.getId()));
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }, 60);
    }

    public void unloadAllWorlds() {
        REGISTERED_DATA.asMap().values().forEach(this::unloadWorld);
    }
}
