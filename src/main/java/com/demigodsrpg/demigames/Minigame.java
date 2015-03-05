package com.demigodsrpg.demigames;

import com.demigodsrpg.demigames.type.MinigameType;
import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getName();

    MinigameType getType();
}
