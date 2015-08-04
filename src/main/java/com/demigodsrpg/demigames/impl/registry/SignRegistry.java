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
import com.demigodsrpg.demigames.sign.MinigameSign;
import com.demigodsrpg.demigames.sign.MutableMinigameSign;
import org.bukkit.Location;

import java.util.Optional;

public class SignRegistry extends AbstractRegistry<String, MutableMinigameSign> {
    private String gameName;

    public SignRegistry(Game game) {
        super("sign", MutableMinigameSign.class, true, game.getName());
        this.gameName = game.getName();
    }

    public Optional<? extends MinigameSign> fromLocation(Location location) {
        String strLoc = LocationUtil.stringFromLocation(location, true);
        return fromKey(strLoc);
    }

    public String getGameName() {
        return gameName;
    }
}
