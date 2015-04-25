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

import com.demigodsrpg.demigames.kit.Kit;
import com.demigodsrpg.demigames.kit.MutableKit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Profile implements Serializable {
    // -- DATA -- //

    private transient Optional<Player> player;
    private String mojangUniqueId;
    private MutableKit kit;
    private String currentSessionId;

    // -- CONSTRUCTORS -- //

    public Profile() {
        this.player = Optional.empty();
    }

    public Profile(Player player) {
        this.player = Optional.of(player);
        this.mojangUniqueId = player.getUniqueId().toString();
        this.kit = null;
        this.currentSessionId = null;
    }

    public Profile(Player player, Kit kit) {
        this.player = Optional.of(player);
        this.mojangUniqueId = player.getUniqueId().toString();
        this.kit = MutableKit.of(kit);
        this.currentSessionId = null;
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

    public Optional<Kit> getKit() {
        return Optional.ofNullable(kit);
    }

    public Optional<String> getCurrentSessionId() {
        return Optional.ofNullable(currentSessionId);
    }

    // -- MUTATORS -- //

    public void setKit(Kit kit) {
        this.kit = MutableKit.of(kit);
    }

    public void setCurrentSessionId(String sessionId) {
        this.currentSessionId = sessionId;
    }
}
