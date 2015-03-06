package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.session.SessionGame;
import com.demigodsrpg.demigames.session.SessionProvider;
import com.demigodsrpg.demigames.stage.DefaultStage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpleefSession implements Session {
    @SessionGame
    private static final Class<? extends Game> GAME = SpleefGame.class;

    private final String id;
    private transient String stage = DefaultStage.WARMUP;
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
    public String getStage() {
        return stage;
    }

    @Override
    public List<Profile> getProfiles() {
        return profiles;
    }

    @Override
    public void setStage(String stage) {
        this.stage = stage;
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
}
