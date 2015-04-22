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
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.session.SessionProvider;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class SessionRegistry {
    private final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void register(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    // -- GETTERS -- //

    public Optional<Session> getSession(String id) {
        return Optional.ofNullable(SESSIONS.getOrDefault(id, null));
    }

    public Optional<Session> getNewSession(Game game) {
        if (DemigamesPlugin.getGameRegistry().getSessionType(game).isPresent()) {
            Class<? extends Session> sessionType = DemigamesPlugin.getGameRegistry().getSessionType(game).get();
            Optional<Constructor<?>> constructor = Arrays.asList(sessionType.getDeclaredConstructors()).stream().filter(this::isSessionProvider).findFirst();
            if (constructor.isPresent()) {
                try {
                    return Optional.of((Session) constructor.get().newInstance(UUID.randomUUID().toString()));
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    public List<Session> getSessions(Game game) {
        if (game != null) {
            return SESSIONS.values().parallelStream().filter(session -> {
                Optional<Game> foundGame = DemigamesPlugin.getGameRegistry().getSessionGame(session);
                return foundGame.isPresent() && foundGame.get().equals(game);
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    // -- PRIVATE HELPER METHODS -- //

    private boolean isSessionProvider(Constructor provider) {
        return provider != null && provider.isAnnotationPresent(SessionProvider.class) && isValidProvider(provider);
    }

    private boolean isValidProvider(Constructor provider) {
        return provider.getParameters().length == 1 && provider.getParameters()[0].getType().equals(String.class);
    }
}
