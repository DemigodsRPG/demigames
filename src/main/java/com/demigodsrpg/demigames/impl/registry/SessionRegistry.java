package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.session.SessionProvider;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
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
            return SESSIONS.values().parallelStream().filter(new Predicate<Session>() {
                @Override
                public boolean test(Session session) {
                    Optional<Game> foundGame = DemigamesPlugin.getGameRegistry().getSessionGame(session);
                    return foundGame.isPresent() && foundGame.get().equals(game);
                }
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
