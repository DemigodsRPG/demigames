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

package com.demigodsrpg.demigames.game.mixin;

import com.demigodsrpg.demigames.event.PlayerJoinMinigameEvent;
import com.demigodsrpg.demigames.event.PlayerSpectateMinigameEvent;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;
import java.util.Optional;

public interface SpectateMixin extends Game {
    Location getSpectatorSpawn(Session session);

    List<String> getSpectators(Session session);

    default boolean isSpectator(Session session, Player player) {
        return getSpectators(session).contains(player.getUniqueId().toString());
    }

    default boolean isSpectator(Session session, Profile profile) {
        return getSpectators(session).contains(profile.getMojangUniqueId());
    }

    default void callSpectate(Session session, Player player) {
        try {
            Bukkit.getPluginManager().callEvent(new PlayerSpectateMinigameEvent(player, session));
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    default void onJoinMinigame(PlayerJoinMinigameEvent event) {
        Optional<Session> opSession = event.getSession();
        if (opSession.isPresent() && opSession.get().getGame().isPresent() && opSession.get().getGame().get().
                equals(this)) {
            Session session = opSession.get();
            if (session.getRawProfiles().size() - getSpectators(session).size() >= session.getGame().get().
                    getMaximumPlayers()) {
                callSpectate(session, event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onSpectate(PlayerSpectateMinigameEvent event);
}
