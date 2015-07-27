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

package com.demigodsrpg.demigames.impl.util;

import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static String stringFromLocation(Location location, boolean blockLocation) {
        if (blockLocation) {
            return location.getBlockX() + ".0" + ";" + location.getBlockY() + ".0" + ";" + location.getBlockZ() + ".0" + ";" + location.getYaw() + ";" + location.getPitch();
        }
        return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static Location locationFromString(Session session, String location) {
        String[] part = location.split(";");
        if (Bukkit.getWorld(session.getId()) != null) {
            return new Location(Bukkit.getWorld(session.getId()), Double.parseDouble(part[0]), Double.parseDouble(part[1]), Double.parseDouble(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
        }
        return null;
    }
}