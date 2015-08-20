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

package com.demigodsrpg.demigames.impl.lobby;

import com.demigodsrpg.demigames.session.Session;


public class LobbySession extends Session {

    // -- DEFAULT INSTANCE -- //

    public static final LobbySession INST = new LobbySession();

    // -- CONSTRUCTOR -- //

    private LobbySession() {
        super(Lobby.LOBBY.WORLD.getName(), Lobby.LOBBY);
    }

    // -- IGNORE THE FOLLOWING METHODS -- //

    public void setStage(String stage) {
    }

    public void updateStage(String stage, boolean process) {
    }

    public void setCurrentRound(int currentRound) {
    }

    public void endSession(boolean nextGame) {
    }
}
