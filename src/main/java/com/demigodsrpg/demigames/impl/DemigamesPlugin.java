package com.demigodsrpg.demigames.impl;

import com.demigodsrpg.demigames.impl.registry.GameRegistry;
import com.demigodsrpg.demigames.impl.registry.SessionRegistry;
import com.demigodsrpg.demigames.impl.util.ClassPathHack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.jar.JarFile;

public class DemigamesPlugin extends JavaPlugin {
    private static DemigamesPlugin INST;

    // -- REGISTRIES -- //
    private static GameRegistry GAME_REGISTRY;
    private static SessionRegistry SESSION_REGISTRY;

    public DemigamesPlugin() {
        super();
        INST = this;
    }

    @Override
    public void onEnable() {
        // Create the registries
        GAME_REGISTRY = new GameRegistry();
        SESSION_REGISTRY = new SessionRegistry();

        // Load libraries
        loadLibraries();

        // Load the components, if there was an error, cancel the plugin from loading
        if (!loadComponents()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load the config
        getConfig().options().copyDefaults(true);
        saveConfig();

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
        // Get the file
        File libraryDirectory = new File(getDataFolder().getPath() + "/lib/");

        // If it exists and isn't a directory, throw an error
        if (libraryDirectory.exists() && !libraryDirectory.isDirectory()) {
            getLogger().severe("The library directory isn't a directory!");
            return;
        }
        // Otherwise, make the directory
        else if (!libraryDirectory.exists()) {
            libraryDirectory.mkdirs();
        }

        // Look for jar files
        for (File file : libraryDirectory.listFiles((dir, name) -> name.endsWith(".jar"))) {
            try {
                ClassPathHack.addFile(file);
            } catch (Exception oops) {
                oops.printStackTrace();
            }
        }
    }

    // -- STATIC GETTERS -- //

    public static DemigamesPlugin getInstance() {
        return INST;
    }

    public static GameRegistry getGameRegistry() {
        return GAME_REGISTRY;
    }

    public static SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }
}
