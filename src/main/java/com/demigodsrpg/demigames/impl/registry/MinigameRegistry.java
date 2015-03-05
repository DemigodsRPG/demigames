package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MinigameRegistry {
    private ConcurrentMap<String, Game> MINIGAMES = new ConcurrentHashMap<>();

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

            if (!isMinigameClass(clazz)) continue;

            try {
                DemigamesPlugin.getMinigameRegistry().register((Game) clazz.newInstance());
            } catch (Exception oops) {
                oops.printStackTrace();
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

    // -- PRIVATE HELPER METHODS -- //

    private String formatClassPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }


    private boolean isMinigameClass(Class<?> clazz) {
        return clazz != null && Game.class.isAssignableFrom(clazz);
    }
}
