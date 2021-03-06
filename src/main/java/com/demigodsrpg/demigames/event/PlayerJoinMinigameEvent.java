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

package com.demigodsrpg.demigames.event;

import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public class PlayerJoinMinigameEvent extends PlayerEvent {

    // -- HANDLER LIST -- //

    private static final HandlerList handlers = new HandlerList();

    // -- DATA -- //

    Backend backend;
    Optional<Game> game;
    Optional<Session> previusSession;
    String sessionId;

    // -- CONSTRUCTOR -- //

    public PlayerJoinMinigameEvent(Player player, Session session, Optional<Session> previousSession) {
        super(player);
        this.backend = session.getBackend();
        this.game = session.getGame();
        this.sessionId = session.getId();
        this.previusSession = previousSession;
    }

    // -- GETTERS -- //

    public Optional<Game> getGame() {
        return game;
    }

    public Optional<Session> getSession() {
        return backend.getSessionRegistry().fromKey(sessionId);
    }

    public Optional<Session> getPreviusSession() {
        return previusSession;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
