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

package com.demigodsrpg.demigames.impl.registry;

import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.util.LocationUtil;
import com.demigodsrpg.demigames.session.Session;
import com.demigodsrpg.demigames.sign.MinigameSign;
import com.demigodsrpg.demigames.sign.MutableMinigameSign;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SignRegistry extends AbstractRegistry<String, MutableMinigameSign> {
    public SignRegistry() {
        super("sign", MutableMinigameSign.class, true);
    }

    public List<MinigameSign> fromGame(Game game) {
        String gameName = game.getName();
        return REGISTERED_DATA.asMap().values().stream().filter(sign -> sign.getGameName().equals(gameName)).
                collect(Collectors.toList());
    }

    public Optional<? extends MinigameSign> fromLocation(Session session, Location location) {
        String strLoc = LocationUtil.stringFromLocation(location, true);
        return REGISTERED_DATA.asMap().values().stream().filter(sign -> sign.getLocationRaw().equals(strLoc)).findAny();
    }
}
