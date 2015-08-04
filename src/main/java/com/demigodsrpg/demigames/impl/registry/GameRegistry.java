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

import com.censoredsoftware.library.util.RandomUtil;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.impl.Setting;
import com.demigodsrpg.demigames.impl.util.ClassPathHack;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.StageHandler;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class GameRegistry {
    private final ConcurrentMap<String, Game> MINIGAMES = new ConcurrentHashMap<>();
    private final boolean partyMode = "party".equalsIgnoreCase(Setting.MODE);

    public Collection<Game> getMinigames() {
        return MINIGAMES.values();
    }

    public void register(Game game) {
        Bukkit.getPluginManager().registerEvents(game, Demigames.getInstance());
        MINIGAMES.put(game.getName(), game);
        Demigames.getInstance().getLogger().info("The \"" + game.getName() + "\" minigame has been registered.");
    }

    public void registerFromJar(File file) throws IOException {
        ClassPathHack.addFile(file, (URLClassLoader) Demigames.class.getClassLoader());
        JarFile jar = new JarFile(file);
        for (Enumeration<JarEntry> en = jar.entries(); en.hasMoreElements(); ) {
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

    public Optional<Game> randomGame() {
        if (!MINIGAMES.isEmpty()) {
            int index = RandomUtil.generateIntRange(partyMode ? 0 : 1, MINIGAMES.size() - 1);
            return Optional.of(Lists.newArrayList(MINIGAMES.values()).get(index));
        }
        return Optional.empty();
    }

    // -- MUTATORS -- //

    public void updateStage(Game game, Session session, String stage, boolean process) {
        session.setStage(stage);
        if (process) {
            processSession(game, session);
        }
    }

    public void processSession(Game game, Session session) {
        for (Method handler : getStageHandlers(game, session.getStage())) {
            try {
                handler.invoke(game, session);
            } catch (Exception ignored) {
            }
        }
    }

    // -- PUBLIC HANDLER METHODS -- //

    public void handlePluginStart() {
        MINIGAMES.values().forEach(Game::onServerStart);
    }

    public void handlePluginStop() {
        MINIGAMES.values().forEach(Game::onServerStop);
    }

    // -- PRIVATE HELPER METHODS -- //

    private String formatClassPath(String path) {
        if (path.length() < 6) return path;
        return path.substring(0, path.length() - 6).replaceAll("/", ".");
    }

    private boolean isMinigameClass(Class<?> clazz) {
        return clazz != null && Game.class.isAssignableFrom(clazz);
    }

    private Optional<Game> getMinigame(Class<? extends Game> clazz) {
        return MINIGAMES.values().stream().filter(game -> game.getClass().equals(clazz)).findFirst();
    }

    private List<Method> getStageHandlers(Game game, String stage) {
        return Arrays.asList(game.getClass().getMethods()).stream().filter(method -> method.isAnnotationPresent(StageHandler.class)
                && stage.equals(method.getAnnotation(StageHandler.class).stage()) && method.getParameters().length == 1
                && method.getParameters()[0].getType().isAssignableFrom(Session.class)).collect(Collectors.toList());
    }
}
