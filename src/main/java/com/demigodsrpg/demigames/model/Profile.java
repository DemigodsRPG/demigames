package com.demigodsrpg.demigames.model;

import com.demigodsrpg.demigames.minigame.kit.Kit;
import com.demigodsrpg.demigames.minigame.kit.MutableKit;
import org.bukkit.entity.Player;

import java.util.Optional;

@Model(name = "profile")
public class Profile {
    // -- DATA -- //

    private final Player player;
    private transient Optional<Kit> kit;

    // -- CONSTRUCTORS -- //

    public Profile(Player player) {
        this.player = player;
        this.kit = Optional.empty();
    }

    public Profile(Player player, MutableKit kit) {
        this.player = player;
        this.kit = Optional.of(kit);
    }

    // -- GETTERS -- //

    public Player getPlayer() {
        return player;
    }

    public Optional<Kit> getKit() {
        return kit;
    }

    // -- MUTATORS -- //

    public void setKit(Kit kit) {
        this.kit = Optional.ofNullable(kit);
    }
}
