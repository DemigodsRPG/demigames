package com.demigodsrpg.demigames.minigame.type;

import java.util.Arrays;
import java.util.Optional;

public final class MinigameTypes {
    // -- TYPES -- //

    public static final Arena ARENA = new Arena();


    // -- TYPE ARRAY -- //

    private static final MinigameType[] typeArray = {
            ARENA,
    };


    // -- PRIVATE CONSTRUCTOR -- //

    private MinigameTypes() {
    }

    // -- ENUM-ESQUE HELPER METHODS -- //

    public static Optional<MinigameType> valueOf(String name) {
        return Arrays.asList(typeArray).stream().filter(type -> type.getName().equals(name)).findAny();
    }

    public static MinigameType[] values() {
        return typeArray;
    }
}
