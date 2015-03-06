package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.session.SessionGameName;
import com.demigodsrpg.demigames.session.SessionProvider;
import com.demigodsrpg.demigames.stage.Stage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpleefSession implements Session {
    @SessionGameName
    public static final String NAME = new SpleefGame().getName();

    private final String id;
    private transient Stage stage = Stage.STARTUP;
    private transient List<Profile> profiles = new ArrayList<>();

    @SessionProvider
    public SpleefSession(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Stage getStage() {
        return stage;
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
