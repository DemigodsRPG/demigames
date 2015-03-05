package com.demigodsrpg.demigames.profile;

import com.demigodsrpg.demigames.kit.Kit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Profile {
    // -- DATA -- //

    private final Player player;
    private transient Optional<Kit> kit;
    private transient Optional<String> currentSessionId;

    // -- CONSTRUCTORS -- //

    public Profile(Player player) {
        this.player = player;
        this.kit = Optional.empty();
        this.currentSessionId = Optional.empty();
    }

    public Profile(Player player, Kit kit) {
        this.player = player;
        this.kit = Optional.of(kit);
        this.currentSessionId = Optional.empty();
    }

    // -- GETTERS -- //

    public Player getPlayer() {
        return player;
    }

    public Optional<Kit> getKit() {
        return kit;
    }

    public Optional<String> getCurrentSessionId() {
        return currentSessionId;
    }

    // -- MUTATORS -- //

    public void setKit(Kit kit) {
        this.kit = Optional.ofNullable(kit);
    }

    public void setCurrentSessionId(String sessionId) {
        this.currentSessionId = Optional.of(sessionId);
    }
}
