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

import com.demigodsrpg.demigames.event.PlayerSpectateMinigameEvent;
import com.demigodsrpg.demigames.kit.Kit;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.GameMode;

import java.util.Optional;

public interface ConfinedSpectateMixin extends SpectateMixin {
    @Override
    default void onSpectate(PlayerSpectateMinigameEvent event) {
        if (event.getGame().isPresent() && event.getGame().get().equals(this)) {
            Optional<Session> opSession = checkPlayer(event.getPlayer());

            if (opSession.isPresent()) {
                // Teleport tot the spectator box
                event.getPlayer().teleport(getSpectatorSpawn(opSession.get()));

                // Add the empty kit to the player
                Kit.EMPTY.apply(event.getPlayer(), true);

                // Set them to adventure so they can't break the box
                event.getPlayer().setGameMode(GameMode.ADVENTURE);
            }
        }
    }
}
