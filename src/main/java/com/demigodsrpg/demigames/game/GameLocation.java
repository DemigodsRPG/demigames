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

package com.demigodsrpg.demigames.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Optional;

public class GameLocation {

    // -- DATA -- //

    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    // -- CONSTRUCTORS -- //

    public GameLocation(String strLocation) {
        if (strLocation == null || "".equals(strLocation)) {
            throw new IllegalArgumentException("Invalid String representation of a GameLocation.");
        }
        String[] part = strLocation.split(";");
        x = Double.parseDouble(part[0]);
        y = Double.parseDouble(part[1]);
        z = Double.parseDouble(part[2]);
        yaw = Float.parseFloat(part[3]);
        pitch = Float.parseFloat(part[4]);
    }

    public GameLocation(Location location, boolean blockLocation) {
        if (blockLocation) {
            x = location.getBlockX();
            y = location.getBlockY();
            z = location.getBlockZ();
        } else {
            x = location.getX();
            y = location.getY();
            z = location.getZ();
        }
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    // -- GETTERS -- //

    public Optional<Location> toLocation(World world) {
        if (world != null) {
            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        }
        return Optional.empty();
    }

    public Optional<Location> toLocation(String sessionId) {
        World world = Bukkit.getWorld(sessionId);
        if (world != null) {
            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        }
        return Optional.empty();
    }

    // -- TO STRING -- //

    @Override
    public String toString() {
        return x + ";" + y + ";" + z + ";" + yaw + ";" + pitch;
    }
}
