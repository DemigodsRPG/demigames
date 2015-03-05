package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.minigame.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MinigameRegistry {
    private ConcurrentMap<String, Minigame> MINIGAMES = new ConcurrentHashMap<>();

    public void register(Minigame minigame) {
        Bukkit.getPluginManager().registerEvents(minigame, DemigamesPlugin.getInstance());
        MINIGAMES.put(minigame.getName(), minigame);
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
                DemigamesPlugin.getMinigameRegistry().register((Minigame) clazz.newInstance());
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    public void unregister(String minigame) {
        HandlerList.unregisterAll(MINIGAMES.get(minigame));
        MINIGAMES.remove(minigame);
    }

    // -- PRIVATE HELPER METHODS -- //

    private String formatClassPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }


    private boolean isMinigameClass(Class<?> clazz) {
        return clazz != null && Minigame.class.isAssignableFrom(clazz);
    }
}
