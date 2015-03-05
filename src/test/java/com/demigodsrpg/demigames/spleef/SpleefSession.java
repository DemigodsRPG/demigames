package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.minigame.session.Session;
import com.demigodsrpg.demigames.minigame.session.SessionProvider;
import com.demigodsrpg.demigames.minigame.stage.Stage;
import com.demigodsrpg.demigames.model.Profile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpleefSession implements Session {
    private Stage stage = Stage.STARTUP;
    private List<Profile> profiles = new ArrayList<>();

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

    @Override
    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    @Override
    public void removeProfile(Profile profile) {
        profiles.remove(profile);
    }

    @Override
    public void removeProfile(Player player) {
        profiles = profiles.parallelStream().filter(profile -> !profile.getPlayer().equals(player)).collect(Collectors.toList());
    }

    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }
}
