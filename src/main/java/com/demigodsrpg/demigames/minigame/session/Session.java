package com.demigodsrpg.demigames.minigame.session;

import com.demigodsrpg.demigames.minigame.stage.Stage;
import com.demigodsrpg.demigames.model.Profile;
import org.bukkit.entity.Player;

import java.util.List;

public interface Session {
    Stage getStage();

    String getGame();

    void addProfile(Profile profile);

    void removeProfile(Profile profile);

    void removeProfile(Player player);

    List<Profile> getProfiles();
}
