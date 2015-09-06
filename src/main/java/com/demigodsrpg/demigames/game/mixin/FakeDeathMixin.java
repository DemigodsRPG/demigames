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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.Optional;

public interface FakeDeathMixin extends Game {
    // -- DAMAGE LISTENER -- //

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    default void onFakeDeathDamage(EntityDamageEvent event) {
        // Only do anything if this is a player
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Optional<Session> opSession = checkPlayer(player);
            if (opSession.isPresent()) {
                // If they should be dead, cancel the event and call the fake death method
                if (player.getHealth() - event.getDamage() <= 0.0) {
                    event.setCancelled(true);
                    Bukkit.getPluginManager().callEvent(new Event(player, opSession.get(), event));
                }
            }
        }
    }

    // -- FAKE DEATH -- //

    class Event extends PlayerEvent {
        // -- HANDLER LIST -- //

        private static final HandlerList handlers = new HandlerList();

        // -- DATA -- //

        Optional<Game> game;
        String sessionId;
        EntityDamageEvent damageEvent;

        // -- CONSTRUCTOR -- //

        public Event(Player player, Session session, EntityDamageEvent damageEvent) {
            super(player);
            this.game = session.getGame();
            this.sessionId = session.getId();
            this.damageEvent = damageEvent;
        }

        // -- GETTERS -- //

        public Optional<Game> getGame() {
            return game;
        }

        public Optional<Session> getSession() {
            if (getGame().isPresent()) {
                return getGame().get().getBackend().getSessionRegistry().fromKey(sessionId);
            }
            return Optional.empty();
        }

        public EntityDamageEvent getDamageEvent() {
            return damageEvent;
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
}
