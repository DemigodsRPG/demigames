package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.type.GameType;
import com.demigodsrpg.demigames.game.type.GameTypes;
import com.demigodsrpg.demigames.stage.Stage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpleefGame implements Game {
    SpleefSession session;

    @StageHandler(stage = Stage.STARTUP)
    public void serverStart() {

    }

    @StageHandler(stage = Stage.SHUTDOWN)
    public void serverShutdown() {

    }

    @StageHandler(stage = Stage.WARMUP)
    public void roundWarmup(SpleefSession session) {

    }

    @StageHandler(stage = Stage.BEGIN)
    public void roundBegin(SpleefSession session) {

    }

    @StageHandler(stage = Stage.PLAY)
    public void roundPlay(SpleefSession session) {

    }

    @StageHandler(stage = Stage.END)
    public void roundEnd(SpleefSession session) {

    }

    @StageHandler(stage = Stage.COOLDOWN)
    public void roundCooldown(SpleefSession session) {

    }

    @StageHandler(stage = Stage.RESET)
    public void roundReset(SpleefSession session) {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

    }

    @Override
    public String getName() {
        return "Spleef";
    }

    @Override
    public GameType getType() {
        return GameTypes.ARENA;
    }
}
