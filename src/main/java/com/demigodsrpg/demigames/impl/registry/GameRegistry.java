package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.session.SessionGameName;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GameRegistry {
    private final ConcurrentMap<String, Game> MINIGAMES = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Class<? extends Session>> SESSION_CLASSES = new ConcurrentHashMap<>();

    public void register(Game game) {
        Bukkit.getPluginManager().registerEvents(game, DemigamesPlugin.getInstance());
        MINIGAMES.put(game.getName(), game);
    }

    public void registerFromJar(JarFile file) {
        for (Enumeration<JarEntry> en = file.entries(); en.hasMoreElements(); ) {
            JarEntry next = en.nextElement();

            // Make sure it's a class
            if (!next.getName().endsWith(".class")) continue;

            Class<?> clazz = null;
            try {
                clazz = Class.forName(formatClassPath(next.getName()), true, getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (isMinigameClass(clazz)) {
                try {
                    register((Game) clazz.newInstance());
                } catch (Exception oops) {
                    oops.printStackTrace();
                }
                continue;
            }

            if (isSessionClass(clazz)) {
                Class<? extends Session> sessionClazz = (Class<? extends Session>) clazz;
                try {
                    Optional<Field> nameField = Arrays.asList(sessionClazz.getDeclaredFields()).stream().filter(field ->
                            field.isAnnotationPresent(SessionGameName.class)).findFirst();
                    if (nameField.isPresent()) {
                        Field field = nameField.get();
                        field.setAccessible(true);
                        field.get(null);
                        SESSION_CLASSES.put(field.get(null).toString(), sessionClazz);
                    }
                } catch (Exception oops) {
                    oops.printStackTrace();
                }
            }
        }
    }

    public void unregister(String minigame) {
        HandlerList.unregisterAll(MINIGAMES.get(minigame));
        MINIGAMES.remove(minigame);
    }

    // -- GETTERS -- //

    public Optional<Game> getMinigame(String name) {
        // ConcurrentHashMap will throw an NPE instead of returning null
        return Optional.ofNullable(MINIGAMES.getOrDefault(name, null));
    }

    public Optional<Class<? extends Session>> getSessionType(Game game) {
        return SESSION_CLASSES.values().stream().filter(type -> getSessionGameName(type).isPresent() &&
                getSessionGameName(type).get().equals(game.getName())).findAny();
    }

    public Optional<Game> getSessionGame(Session session) {
        Class<? extends Session> type = session.getClass();
        Optional<Map.Entry<String, Class<? extends Session>>> found = SESSION_CLASSES.entrySet().stream().
                filter(entry -> entry.getValue().equals(type)).findFirst();
        if (found.isPresent()) {
            return getMinigame(found.get().getKey());
        }
        return Optional.empty();
    }

    // -- PRIVATE HELPER METHODS -- //

    private String formatClassPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }

    private boolean isMinigameClass(Class<?> clazz) {
        return clazz != null && Game.class.isAssignableFrom(clazz);
    }

    private Optional<String> getSessionGameName(Class<? extends Session> clazz) {
        try {
            Optional<Field> nameField = Arrays.asList(clazz.getDeclaredFields()).stream().
                    filter(field -> Arrays.asList(field.getDeclaredAnnotations()).contains(SessionGameName.class)).findFirst();
            if (nameField.isPresent()) {
                Field field = nameField.get();
                field.setAccessible(true);
                return Optional.ofNullable(field.get(null).toString());
            }
        } catch (Exception ignored) {
        }

        return Optional.empty();
    }

    private boolean isSessionClass(Class<?> clazz) {
        return clazz != null && Session.class.isAssignableFrom(clazz);
    }
}
