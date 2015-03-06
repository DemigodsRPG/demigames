package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.type.GameType;
import com.demigodsrpg.demigames.game.type.GameTypes;
import com.demigodsrpg.demigames.stage.DefaultStage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpleefGame implements Game {
    @StageHandler(stage = DefaultStage.ERROR)
    public void onError(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.WARMUP)
    public void roundWarmup(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.BEGIN)
    public void roundBegin(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.PLAY)
    public void roundPlay(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.END)
    public void roundEnd(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.COOLDOWN)
    public void roundCooldown(SpleefSession session) {

    }

    @StageHandler(stage = DefaultStage.RESET)
    public void roundReset(SpleefSession session) {

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // TODO
    }

    @Override
    public String getName() {
        return "Spleef";
    }

    @Override
    public GameType getType() {
        return GameTypes.ARENA;
    }

    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerStop() {

    }
}
