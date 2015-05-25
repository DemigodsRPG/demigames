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

package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.stage.DefaultStage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpleefGame implements Game {
    @StageHandler(stage = DefaultStage.ERROR)
    public void onError(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.WARMUP)
    public void roundWarmup(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.BEGIN)
    public void roundBegin(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.PLAY)
    public void roundPlay(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.END)
    public void roundEnd(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.COOLDOWN)
    public void roundCooldown(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.RESET)
    public void roundReset(SpleefSession session) {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // TODO
    }

    @Override
    public String getName() {
        return "Spleef";
    }

    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerStop() {

    }
}
