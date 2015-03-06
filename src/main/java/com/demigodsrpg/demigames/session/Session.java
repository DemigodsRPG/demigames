package com.demigodsrpg.demigames.session;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.DemigamesPlugin;
import com.demigodsrpg.demigames.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public interface Session {
    String getId();

    String getStage();

    List<Profile> getProfiles();

    @Deprecated
    void setStage(String stage);

    void addProfile(Profile profile);

    void removeProfile(Profile profile);

    void removeProfile(Player player);

    default Optional<Game> getGame() {
        return DemigamesPlugin.getGameRegistry().getSessionGame(this);
    }

    default void updateStage(String stage, boolean process) {
        if (getGame().isPresent()) {
            DemigamesPlugin.getGameRegistry().updateStage(getGame().get(), this, stage, process);
        } else {
            throw new NullPointerException("A session is missing its respective game!");
        }
    }
}
