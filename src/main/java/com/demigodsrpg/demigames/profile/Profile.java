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

package com.demigodsrpg.demigames.profile;

import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.kit.Kit;
import com.demigodsrpg.demigames.unlockable.Unlockable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Profile implements Serializable {

    // -- TRANSIENT DATA -- //

    private transient Optional<Player> player;
    private transient Optional<Kit> kit;
    private transient String currentSessionId;
    private transient String previousSessionId;

    // -- DATA -- //

    String mojangUniqueId;
    String lastKnownName;
    List<String> unlockables;
    int tickets;

    // -- CONSTRUCTORS -- //

    public Profile() {
        this.player = Optional.empty();
        this.kit = Optional.empty();
    }

    public Profile(Backend backend, Player player) {
        this.player = Optional.of(player);
        this.mojangUniqueId = player.getUniqueId().toString();
        this.lastKnownName = player.getName();
        this.kit = Optional.empty();
        this.currentSessionId = null;
        this.previousSessionId = null;
        this.unlockables = new ArrayList<>();
        this.tickets = 0;
        backend.getProfileRegistry().put(mojangUniqueId, this);
    }

    public Profile(Backend backend, Player player, Kit kit) {
        this.player = Optional.of(player);
        this.mojangUniqueId = player.getUniqueId().toString();
        this.lastKnownName = player.getName();
        this.kit = Optional.of(kit);
        this.currentSessionId = null;
        this.previousSessionId = null;
        this.unlockables = new ArrayList<>();
        this.tickets = 0;
        backend.getProfileRegistry().put(mojangUniqueId, this);
    }

    // -- GETTERS -- //

    public Optional<Player> getPlayer() {
        if (!player.isPresent()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(mojangUniqueId));
            if (offlinePlayer.isOnline()) {
                player = Optional.of(offlinePlayer.getPlayer());
            }
        }
        return player;
    }

    public String getMojangUniqueId() {
        return mojangUniqueId;
    }

    public Optional<String> getLastKnownName() {
        return Optional.ofNullable(lastKnownName);
    }

    public Optional<Kit> getKit() {
        return kit;
    }

    public Optional<String> getCurrentSessionId() {
        return Optional.ofNullable(currentSessionId);
    }

    public Optional<String> getPreviousSessionId() {
        return Optional.ofNullable(previousSessionId);
    }

    public boolean hasUnlockable(Unlockable unlockable) {
        return unlockables.contains(unlockable.getName());
    }

    public int getTickets() {
        return tickets;
    }

    // -- MUTATORS -- //

    public void setKit(Backend backend, Kit kit) {
        this.kit = Optional.ofNullable(kit);
        backend.getProfileRegistry().put(mojangUniqueId, this);
    }

    public void setCurrentSessionId(Backend backend, String sessionId) {
        this.currentSessionId = sessionId;
        backend.getProfileRegistry().put(mojangUniqueId, this);
    }

    public void setPreviousSessionId(Backend backend, String sessionId) {
        this.previousSessionId = sessionId;
        backend.getProfileRegistry().put(mojangUniqueId, this);
    }

    public void addUnlockable(Unlockable unlockable) {
        unlockables.add(unlockable.getName());
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }
}
