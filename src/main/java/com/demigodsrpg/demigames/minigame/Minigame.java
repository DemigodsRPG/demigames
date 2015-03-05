package com.demigodsrpg.demigames.minigame;

import com.demigodsrpg.demigames.minigame.type.MinigameType;
import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getName();

    MinigameType getType();
}
