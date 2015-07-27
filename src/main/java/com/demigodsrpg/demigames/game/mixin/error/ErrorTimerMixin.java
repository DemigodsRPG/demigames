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

package com.demigodsrpg.demigames.game.mixin.error;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public interface ErrorTimerMixin extends Game {
    @StageHandler(stage = DefaultStage.ERROR)
    default void roundWarmup(Session session) {
        for (int i = 0; i <= 5; i++) {
            final int k = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Demigames.getInstance(), () -> {
                if (k == 10) {
                    // Update the stage
                    session.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
                    });
                    session.endSession(false);
                } else {
                    Demigames.getTitleUtil().broadcastTitle(session, 2, 30, 0, ChatColor.RED + "ERROR!", "Ending in " + (5 - k) + " seconds.");
                    session.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 0.5f);
                    });
                }
            }, i * 20);
        }
    }
}
