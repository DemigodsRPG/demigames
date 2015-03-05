package com.demigodsrpg.demigames.impl;

import com.demigodsrpg.demigames.impl.registry.GameRegistry;
import com.demigodsrpg.demigames.impl.util.ClassPathHack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.jar.JarFile;

public class DemigamesPlugin extends JavaPlugin {
    private static DemigamesPlugin INST;

    // -- REGISTRIES -- //
    private static GameRegistry GAME_REGISTRY;

    public DemigamesPlugin() {
        super();
        INST = this;
    }

    @Override
    public void onEnable() {
        // Create the registries
        GAME_REGISTRY = new GameRegistry();

        // Load libraries
        loadLibraries();

        // Load the components
        loadComponents();

        // Load the config
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {

    }

    // -- HELPER METHODS -- //

    private void loadComponents() {
        // Get the file
        File componentDirectory = new File(getDataFolder().getPath() + "/games/");

        // If it exists and isn't a directory, throw an error
        if (componentDirectory.exists() && !componentDirectory.isDirectory()) {
            // ERROR
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
    }

    private void loadLibraries() {
        // Get the file
        File libraryDirectory = new File(getDataFolder().getPath() + "/lib/");

        // If it exists and isn't a directory, throw an error
        if (libraryDirectory.exists() && !libraryDirectory.isDirectory()) {
            // ERROR
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
}
