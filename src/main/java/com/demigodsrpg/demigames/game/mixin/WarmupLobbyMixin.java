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

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface WarmupLobbyMixin extends Game {

    // -- GETTER -- //

    Location getWarmupSpawn(Session session);

    // -- WARMUP -- //

    @StageHandler(stage = DefaultStage.WARMUP)
    default void roundWarmup(Session session) {
        // Teleport all players to the warmup point
        for (Player player : session.getPlayers()) {
            player.teleport(getWarmupSpawn(session));
        }

        // Iterate the round
        session.setCurrentRound(session.getCurrentRound() + 1);

        for (int i = 0; i <= 10; i++) {
            final int k = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(getBackend(), () -> {
                Optional<Session> current = getBackend().getSessionRegistry().fromKey(session.getId());
                if (current.isPresent()) {
                    if (k == 10) {
                        // Update the stage
                        current.get().getPlayers().forEach(player -> {
                            getBackend().getTitleUtil().broadcastTitle(session, 0, 18, 2, ChatColor.GREEN +
                                    "GO!", "Have fun!");
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
                        });
                        current.get().updateStage(DefaultStage.BEGIN, true);
                    } else {
                        getBackend().getTitleUtil().broadcastTitle(session, 2, 30, 0, ChatColor.GOLD + getName() + "!",
                                "In " + (10 - k) + " seconds!");
                        current.get().getPlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 0.5f);
                        });
                    }
                }
            }, i * 20);
        }
    }
}
