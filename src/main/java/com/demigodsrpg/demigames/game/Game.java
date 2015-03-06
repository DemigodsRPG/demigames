package com.demigodsrpg.demigames.game;

import com.demigodsrpg.demigames.game.type.GameType;
import org.bukkit.event.Listener;

public interface Game extends Listener {
    String getName();

    GameType getType();

    void onServerStart();

    void onServerStop();
}
