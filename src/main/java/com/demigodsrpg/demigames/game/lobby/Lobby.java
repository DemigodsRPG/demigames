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

package com.demigodsrpg.demigames.game.lobby;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.entity.Player;

public class Lobby implements Game {
    @Override
    public String getName() {
        return "Lobby";
    }

    @Override
    public String getDirectory() {
        return "lobby";
    }

    @Override
    public boolean canPlace() {
        return false;
    }

    @Override
    public boolean canBreak() {
        return false;
    }

    @Override
    public boolean canDrop() {
        return false;
    }

    @Override
    public boolean canLateJoin() {
        return true;
    }

    @Override
    public boolean hasSpectateChat() {
        return false;
    }

    @Override
    public int getMinimumPlayers() {
        return 0;
    }

    @Override
    public int getNumberOfTeams() {
        return 0;
    }

    @Override
    public int getTotalRounds() {
        return 0;
    }

    @Override
    public void onWin(Session session, Player player) {
    }

    @Override
    public void onLose(Session session, Player player) {
    }

    @Override
    public void onTie(Session session, Player player) {
    }

    @Override
    public void onPlayerJoin(Session session, Player player) {
        player.sendMessage("Welcome!");
    }

    @Override
    public void onPlayerQuit(Session session, Player player) {
    }

    @Override
    public void setupLocations(Session session) {
    }
}
