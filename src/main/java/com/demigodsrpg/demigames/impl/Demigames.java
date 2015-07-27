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

import com.demigodsrpg.demigames.impl.command.KitCommand;
import com.demigodsrpg.demigames.impl.listener.DefaultSessionListener;
import com.demigodsrpg.demigames.impl.registry.GameRegistry;
import com.demigodsrpg.demigames.impl.registry.KitRegistry;
import com.demigodsrpg.demigames.impl.registry.ProfileRegistry;
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.impl.util.LibraryHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Demigames extends JavaPlugin {
    private static Demigames INST;

    // -- HANDLERS -- //
    private static LibraryHandler LIBRARIES;
    private static TitleUtil TITLE_UTIL;

    // -- REGISTRIES -- //
    private static GameRegistry GAME_REGISTRY;
    private static ProfileRegistry PROFILE_REGISTRY;
    private static KitRegistry KIT_REGISTRY;
    private static SessionRegistry SESSION_REGISTRY;

    @Override
    public void onEnable() {
        INST = this;

        // Create the handlers
        LIBRARIES = new LibraryHandler(this);

        // Setup title util
        try {
            TITLE_UTIL = new TitleUtil();
        } catch (Exception oops) {
            oops.printStackTrace();
        }

        // Load the config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load libraries
        loadLibraries();

        // Create the registries
        GAME_REGISTRY = new GameRegistry();
        PROFILE_REGISTRY = new ProfileRegistry();
        KIT_REGISTRY = new KitRegistry();
        SESSION_REGISTRY = new SessionRegistry();

        // Handle listeners
        getServer().getPluginManager().registerEvents(new DefaultSessionListener(), this);

        // Register commands
        getCommand("demikit").setExecutor(new KitCommand());

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

        // Unload all sessions
        SESSION_REGISTRY.unloadAllWorlds();
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
                GAME_REGISTRY.registerFromJar(file);
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }

        return true;
    }

    private void loadLibraries() {
        // Censored Lib
        LIBRARIES.addMavenLibrary("http://repo.ii.dg-mg.club/", "com.censoredsoftware.library", "util", "1.0.2");
        LIBRARIES.addMavenLibrary("http://repo.ii.dg-mg.club/", "com.censoredsoftware.library", "bukkit-util", "1.0.2");
        LIBRARIES.addMavenLibrary("http://repo.ii.dg-mg.club/", "com.censoredsoftware.library", "command", "1.0.2");
    }

    // -- STATIC GETTERS -- //

    public static Demigames getInstance() {
        return INST;
    }

    public static TitleUtil getTitleUtil() {
        return TITLE_UTIL;
    }

    public static ProfileRegistry getProfileRegistry() {
        return PROFILE_REGISTRY;
    }

    public static KitRegistry getKitRegistry() {
        return KIT_REGISTRY;
    }

    public static GameRegistry getGameRegistry() {
        return GAME_REGISTRY;
    }

    public static SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }
}
