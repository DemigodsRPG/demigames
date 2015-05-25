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
import com.demigodsrpg.demigames.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class SessionRegistry extends AbstractRegistry<String, Session> {

    // -- CONSTRUCTOR -- //

    public SessionRegistry(ConcurrentMap<String, Session> dataMap) {
        super(dataMap);
    }

    // -- GETTERS -- //

    public Session newSession(Game game) {
        String keyId = UUID.randomUUID().toString();
        Session newSession = new Session(keyId, game);
        return REGISTERED_DATA.put(keyId, newSession);
    }

    public Session newSession(Game game, String stage) {
        String keyId = UUID.randomUUID().toString();
        Session newSession = new Session(keyId, game, stage);
        return REGISTERED_DATA.put(keyId, newSession);
    }

    public List<Session> fromGame(Game game) {
        if (game != null) {
            return REGISTERED_DATA.values().parallelStream().filter(session -> {
                Optional<Game> foundGame = session.getGame();
                return foundGame.isPresent() && foundGame.get().equals(game);
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
