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

package com.demigodsrpg.demigames.game;

import com.demigodsrpg.demigames.game.impl.command.*;
import com.demigodsrpg.demigames.game.impl.listener.GameListener;
import com.demigodsrpg.demigames.game.impl.listener.KitListener;
import com.demigodsrpg.demigames.game.impl.listener.SessionListener;
import com.demigodsrpg.demigames.game.impl.registry.*;
import com.demigodsrpg.demigames.game.impl.util.LibraryHandler;
import com.demigodsrpg.demigames.game.impl.util.TitleUtil;
import com.demigodsrpg.demigames.game.lobby.Lobby;
import com.demigodsrpg.demigames.session.Session;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Backend extends JavaPlugin {
    static Backend INST;

    // -- HANDLERS -- //
    private LibraryHandler LIBRARIES;
    private TitleUtil TITLE_UTIL;

    // -- REGISTRIES -- //
    private GameRegistry GAME_REGISTRY;
    private ProfileRegistry PROFILE_REGISTRY;
    private SessionRegistry SESSION_REGISTRY;
    private KitRegistry KIT_REGISTRY;
    private ConcurrentMap<String, LocationRegistry> LOC_REGISTRIES;
    private ConcurrentMap<String, SignRegistry> SIGN_REGISTRIES;
    private ConcurrentMap<String, ShopRegistry> SHOP_REGISTRIES;

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
        PROFILE_REGISTRY = new ProfileRegistry(this);
        KIT_REGISTRY = new KitRegistry(this);
        SESSION_REGISTRY = new SessionRegistry(this);

        // Define and registry the lobby
        Lobby.LOBBY = new Lobby();
        GAME_REGISTRY.register(Lobby.LOBBY);

        // Handle listeners
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new GameListener(this), this);
        manager.registerEvents(new SessionListener(this), this);
        manager.registerEvents(new KitListener(this), this);

        // Register commands
        getCommand("joingame").setExecutor(new JoinGameCommand(this));
        getCommand("leavegame").setExecutor(new LeaveGameCommand(this));
        getCommand("createkit").setExecutor(new CreateKitCommand(this));
        getCommand("applykit").setExecutor(new ApplyKitCommand(this));
        getCommand("createlocation").setExecutor(new CreateLocationCommand(this));
        getCommand("createsign").setExecutor(new CreateSignCommand(this));
        SessionInfoCommand sessionInfoCommand = new SessionInfoCommand(this);
        getCommand("sessioninfo").setExecutor(sessionInfoCommand);
        getCommand("sessioninfo").setTabCompleter(sessionInfoCommand);

        // Load the components. If there was an error, cancel the plugin from loading
        if (!loadComponents()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register the signs
        SIGN_REGISTRIES = new ConcurrentHashMap<>();
        GAME_REGISTRY.getMinigames().stream().map(SignRegistry::new).forEach(registry -> {
            SIGN_REGISTRIES.put(registry.getGameName(), registry);
        });

        // Register the shops
        SHOP_REGISTRIES = new ConcurrentHashMap<>();
        GAME_REGISTRY.getMinigames().stream().map(ShopRegistry::new).forEach(registry -> {
            SHOP_REGISTRIES.put(registry.getGameName(), registry);
        });

        // Register the locations
        LOC_REGISTRIES = new ConcurrentHashMap<>();
        GAME_REGISTRY.getMinigames().stream().map(LocationRegistry::new).forEach(registry -> {
            LOC_REGISTRIES.put(registry.getGameName(), registry);
        });

        // Handle minigame server start methods
        GAME_REGISTRY.handlePluginStart();
    }

    @Override
    public void onDisable() {
        // Handle minigame server stop methods
        GAME_REGISTRY.handlePluginStop();

        // Unload all sessions
        SESSION_REGISTRY.unloadAllWorlds();

        // Purge the session registry to clean up old sessions
        SESSION_REGISTRY.purge();
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

    public TitleUtil getTitleUtil() {
        return TITLE_UTIL;
    }

    public ProfileRegistry getProfileRegistry() {
        return PROFILE_REGISTRY;
    }

    public KitRegistry getKitRegistry() {
        return KIT_REGISTRY;
    }

    public Optional<SignRegistry> getSignRegistry(String gameName) {
        return Optional.ofNullable(SIGN_REGISTRIES.getOrDefault(gameName, null));
    }

    public Optional<ShopRegistry> getShopRegistry(String gameName) {
        return Optional.ofNullable(SHOP_REGISTRIES.getOrDefault(gameName, null));
    }

    public Optional<LocationRegistry> getLocationRegistry(String gameName) {
        return Optional.ofNullable(LOC_REGISTRIES.getOrDefault(gameName, null));
    }

    public GameRegistry getGameRegistry() {
        return GAME_REGISTRY;
    }

    public SessionRegistry getSessionRegistry() {
        return SESSION_REGISTRY;
    }

    // -- PUBLIC HELPER METHODS -- //

    public void sendTaggedMessage(Player player, String message) {
        player.sendMessage(Setting.TAG + " " + ChatColor.WHITE + message);
    }

    public void sendTaggedMessage(Player player, String... messages) {
        for (String message : messages) {
            player.sendMessage(Setting.TAG + " " + ChatColor.WHITE + message);
        }
    }

    public void broadcastTaggedMessage(Session session, String message) {
        session.getPlayers().forEach(player -> player.sendMessage(Setting.TAG + " " + ChatColor.WHITE + message));
    }

    public void broadcastTaggedMessage(Session session, String... messages) {
        session.getPlayers().forEach(player -> {
            for (String message : messages) {
                player.sendMessage(Setting.TAG + " " + ChatColor.WHITE + message);
            }
        });
    }

    public void sendTaggedMessage(Player player, TextComponent message) {
        TextComponent fullMessage = new TextComponent("");

        for (BaseComponent component : TextComponent.fromLegacyText(Setting.TAG)) {
            fullMessage.addExtra(component);
        }

        TextComponent next = new TextComponent(" ");
        fullMessage.addExtra(next);
        fullMessage.addExtra(message);

        player.spigot().sendMessage(fullMessage);
    }

    public void sendTaggedMessage(Player player, TextComponent... messages) {
        for (TextComponent message : messages) {
            sendTaggedMessage(player, message);
        }
    }

    public void broadcastTaggedMessage(Session session, TextComponent message) {
        session.getPlayers().forEach(player -> sendTaggedMessage(player, message));
    }

    public void broadcastTaggedMessage(Session session, TextComponent... messages) {
        session.getPlayers().forEach(player -> {
            sendTaggedMessage(player, messages);
        });
    }
}
