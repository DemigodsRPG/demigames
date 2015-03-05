package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.minigame.Session;
import com.demigodsrpg.demigames.minigame.SessionProvider;
import com.demigodsrpg.demigames.minigame.Stage;

public class SpleefSession implements Session {
    private Stage stage = Stage.STARTUP;

    @SessionProvider
    public SpleefSession() {
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public String getGame() {
        return "Spleef";
    }
}
