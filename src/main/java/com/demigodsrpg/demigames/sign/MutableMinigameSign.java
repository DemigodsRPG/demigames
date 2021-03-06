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

package com.demigodsrpg.demigames.sign;

import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.GameLocation;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class MutableMinigameSign implements MinigameSign, Serializable {

    // -- DATA -- //

    String name;
    String gameName;
    String location;
    String command;
    List<String> lines;

    // -- CONSTRUCTOR -- //

    public MutableMinigameSign(String name, String gameName, GameLocation location, String command, List<String> lines) {
        this.name = name;
        this.gameName = gameName;
        this.location = location.toString();
        this.command = command;
        this.lines = lines;
    }

    // -- GETTERS -- //

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    @Override
    public Optional<Game> getGame(Backend backend) {
        return backend.getGameRegistry().getMinigame(gameName);
    }

    @Override
    public Optional<Location> getLocation(Session session) {
        return new GameLocation(location).toLocation(session.getId());
    }

    @Override
    public GameLocation getGameLocation() {
        return new GameLocation(location);
    }

    public List<String> getLines() {
        return lines;
    }

    public String getCommand() {
        return command;
    }
}
