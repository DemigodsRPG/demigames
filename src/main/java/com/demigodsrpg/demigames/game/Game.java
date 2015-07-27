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

import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Optional;

public interface Game extends Listener {
    String getName();

    String getDirectory();

    boolean canPlace();

    boolean canBreak();

    boolean canDrop();

    boolean canLateJoin();

    boolean hasSpectateChat();

    int getMinimumPlayers();

    int getNumberOfTeams();

    int getTotalRounds();

    void onWin(Session session, Player player);

    void onLose(Session session, Player player);

    void onTie(Session session, Player player);

    void onPlayerJoin(Player player);

    void onPlayerQuit(Player player);

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
}
