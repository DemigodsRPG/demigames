package com.demigodsrpg.demigames.session;

import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.stage.Stage;
import org.bukkit.entity.Player;

import java.util.List;

public interface Session {
    String getId();

    Stage getStage();

    String getGame();

    void addProfile(Profile profile);

    void removeProfile(Profile profile);

    void removeProfile(Player player);

    List<Profile> getProfiles();
}
