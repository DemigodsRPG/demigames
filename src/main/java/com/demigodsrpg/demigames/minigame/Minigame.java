package com.demigodsrpg.demigames.minigame;

import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getName();

    MinigameType getType();
}
