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

import com.demigodsrpg.demigames.impl.util.LocationUtil;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public abstract class StaticSign implements DemiSign {

    // -- DATA -- //

    String name;
    String location;
    List<String> lines;

    // -- GETTERS -- //

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<Location> getLocation(Session session) {
        return Optional.ofNullable(LocationUtil.locationFromString(session.getId(), location));
    }

    public List<String> getLines() {
        return lines;
    }
}
