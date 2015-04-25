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

package com.demigodsrpg.demigames.impl;

import com.demigodsrpg.demigames.impl.registry.GameRegistry;
import com.demigodsrpg.demigames.impl.registry.ProfileRegistry;
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.impl.util.LibraryHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.Config;
import org.redisson.Redisson;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

public class DemigamesPlugin extends JavaPlugin {
    private static DemigamesPlugin INST;

    // -- HANDLERS -- //
    private static LibraryHandler LIBRARIES;
    private Redisson REDIS;

    // -- REGISTRIES -- //
    private static GameRegistry GAME_REGISTRY;
    private static ProfileRegistry PROFILE_REGISTRY;
    private static SessionRegistry SESSION_REGISTRY;

    @Override
    public void onEnable() {
        INST = this;

        // Create the handlers
        LIBRARIES = new LibraryHandler(this);

        // Load the config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load libraries
        loadLibraries();

        if (Setting.USE_REDIS) {
            Config config = new Config();
            config.useSingleServer().setAddress(Setting.REDIS_CONNECTION);
            REDIS = Redisson.create(config);

            // Test redis connection
            try {
                REDIS.getQueue(Setting.REDIS_SERVER_ID + "$TEST").peek();
                getLogger().info("Redis connection was successful.");
            } catch (Exception oops) {
                getLogger().severe("Redis connection was unsuccessful!");
                getLogger().severe("Disabling all Redis features.");
                Setting.USE_REDIS = false;
            }
        }

        // Create the registries
        GAME_REGISTRY = new GameRegistry();

        if (Setting.USE_REDIS) {
            PROFILE_REGISTRY = new ProfileRegistry(REDIS.getMap(Setting.REDIS_CHANNEL + "$PROFILES"));
            SESSION_REGISTRY = new SessionRegistry(REDIS.getMap(Setting.REDIS_CHANNEL + "$SESSIONS"));
        } else {
            PROFILE_REGISTRY = new ProfileRegistry(new ConcurrentHashMap<>());
            SESSION_REGISTRY = new SessionRegistry(new ConcurrentHashMap<>());
        }

        // Load the components. If there was an error, cancel the plugin from loading
        if (!loadComponents()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Handle minigame server start methods
        GAME_REGISTRY.handlePluginStart();
    }

    @Override
    public void onDisable() {
        // Handle minigame server stop methods
        GAME_REGISTRY.handlePluginStop();
    }

    // -- HELPER METHODS -- //

    private boolean loadComponents() {
        // Get the file
        File componentDirectory = new File(getDataFolder().getPath() + "/games/");

        // If it exists and isn't a directory, throw an error
        if (componentDirectory.exists() && !componentDirectory.isDirectory()) {
            return false;
        }
        // Otherwise, make the directory
        else if (!componentDirectory.exists()) {
            componentDirectory.mkdirs();
        }

        // Look for jar files
        for (File file : componentDirectory.listFiles((dir, name) -> name.endsWith(".jar"))) {
            try {
                JarFile jar = new JarFile(file);
                GAME_REGISTRY.registerFromJar(jar);
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }

        return true;
    }

    private void loadLibraries() {
        // Add the redis related libraries
        if (Setting.USE_REDIS) {
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "org.redisson", "redisson", "1.2.0");
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "org.slf4j", "slf4j-api", "1.7.10");
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "com.esotericsoftware", "kryo", "3.0.0");
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "com.fasterxml.jackson.core", "jackson-core", "2.4.4");
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "com.fasterxml.jackson.core", "jackson-annotations", "2.4.4");
            LIBRARIES.addMavenLibrary(LibraryHandler.MAVEN_CENTRAL, "com.fasterxml.jackson.core", "jackson-databind", "2.4.4");
        }

        // Censored Lib
        LIBRARIES.addMavenLibrary("http://repo.ii.dg-mg.club/", "com.censoredsoftware.library", "util", "1.0.2");
        LIBRARIES.addMavenLibrary("http://repo.ii.dg-mg.club/", "com.censoredsoftware.library", "bukkit-util", "1.0.2");
    }

    // -- STATIC GETTERS -- //

    public static DemigamesPlugin getInstance() {
        return INST;
    }

    public static ProfileRegistry getProfileRegistry() {
        return PROFILE_REGISTRY;
    }

    public static GameRegistry getGameRegistry() {
        return GAME_REGISTRY;
    }

    public static SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }
}
