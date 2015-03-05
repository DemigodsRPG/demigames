package com.demigodsrpg.demigames.game.type;

import java.util.Arrays;
import java.util.Optional;

public final class GameTypes {
    // -- TYPES -- //

    public static final Arena ARENA = new Arena();


    // -- TYPE ARRAY -- //

    private static final GameType[] typeArray = {
            ARENA,
    };


    // -- PRIVATE CONSTRUCTOR -- //

    private GameTypes() {
    }

    // -- ENUM-ESQUE HELPER METHODS -- //

    public static Optional<GameType> valueOf(String name) {
        return Arrays.asList(typeArray).stream().filter(type -> type.getName().equals(name)).findAny();
    }

    public static GameType[] values() {
        return typeArray;
    }
}
