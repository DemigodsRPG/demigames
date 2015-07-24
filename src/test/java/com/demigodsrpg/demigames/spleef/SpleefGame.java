/*
 * Copyright (c) 2015 Demigods RPG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.demigodsrpg.demigames.spleef;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.stage.DefaultStage;
import com.demigodsrpg.demigames.stage.StageHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class SpleefGame implements Game {
    // -- SETTINGS -- //

    @Override
    public boolean canPlace() {
        return false;
    }

    @Override
    public boolean canBreak() {
        return true;
    }

    @Override
    public boolean canDrop() {
        return false;
    }

    @Override
    public boolean hasSpectateChat() {
        return false;
    }

    @Override
    public int getMinimumPlayers() {
        return 3;
    }

    @Override
    public int getNumberOfTeams() {
        return 0;
    }

    @Override
    public int getTotalRounds() {
        return 3;
    }

    // -- LOCATIONS -- //

    private Location warmupSpawn;

    private void setupLocations(Session session) {
        // Get the world
        World world = session.getWorld().get();

        // TODO Config for locations

        // Get the warmup spawn
        warmupSpawn = world.getSpawnLocation();
    }

    // -- STAGES -- //

    @StageHandler(stage = DefaultStage.ERROR)
    public void onError(Session session) {

    }

    @StageHandler(stage = DefaultStage.SETUP)
    public void roundSetup(Session session) {
        // Setup the world
        Optional<World> opWorld = Demigames.getSessionRegistry().setupWorld(session);

        if (opWorld.isPresent()) {
            // Setup the locations
            setupLocations(session);

            // Iterate the round
            session.setCurrentRound(session.getCurrentRound() + 1);

            // Update the stage
            session.updateStage(DefaultStage.WARMUP, true);
        } else {
            // Update the stage
            session.updateStage(DefaultStage.ERROR, true);
        }
    }

    @StageHandler(stage = DefaultStage.WARMUP)
    public void roundWarmup(Session session) {
        for (Player player : session.getPlayers()) {
            player.teleport(warmupSpawn);
        }

        for (int i = 0; i <= 8; i++) {
            final int k = i;
            Bukkit.getScheduler().scheduleSyncDelayedTask(Demigames.getInstance(), () -> {
                Demigames.getTitleUtil().broadcastTitle(session, 2, 16, 2, ChatColor.GOLD + "Spleef!", "In " + k + " seconds!");
                if (k == 8) {
                    // Update the stage
                    session.updateStage(DefaultStage.BEGIN, true);
                    session.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
                    });
                } else {
                    session.getPlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5f, 0.5f);
                    });
                }
            }, i * 20);
        }
    }

    @StageHandler(stage = DefaultStage.BEGIN)
    public void roundBegin(Session session) {

        // Update the stage
        session.updateStage(DefaultStage.PLAY, true);
    }

    @StageHandler(stage = DefaultStage.PLAY)
    public void roundPlay(Session session) {

        // Update the stage
        session.updateStage(DefaultStage.END, true);
    }

    @StageHandler(stage = DefaultStage.END)
    public void roundEnd(Session session) {

        // Update the stage
        session.updateStage(DefaultStage.COOLDOWN, true);
    }

    @StageHandler(stage = DefaultStage.COOLDOWN)
    public void roundCooldown(Session session) {

        // Update the stage
        if (session.getCurrentRound() == getTotalRounds()) {
            session.endSession(true);
        } else {
            session.updateStage(DefaultStage.RESET, true);
        }
    }

    @StageHandler(stage = DefaultStage.RESET)
    public void roundReset(Session session) {

        // Update the stage
        session.updateStage(DefaultStage.SETUP, true);
    }

    // -- LISTENERS -- //

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // TODO
    }

    // -- META DATA -- //

    @Override
    public String getName() {
        return "Spleef";
    }

    @Override
    public String getDirectory() {
        return "spleef";
    }

    // -- WIN/LOSE/TIE CONDITIONS -- //

    @Override
    public void onWin(Player player) {

    }

    @Override
    public void onLose(Player player) {

    }

    @Override
    public void onTie(Player player) {

    }

    // -- PLAYER JOIN/QUIT -- //

    @Override
    public void playerJoins(Player player) {

    }

    @Override
    public void playerQuits(Player player) {

    }

    // -- START & STOP -- //

    @Override
    public void onServerStart() {

    }

    @Override
    public void onServerStop() {
        for (Session session : Demigames.getSessionRegistry().fromGame(this)) {
            session.endSession(false);
        }
    }
}
