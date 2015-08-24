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

package com.demigodsrpg.demigames.unlockable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Unlockables {

    // -- UNLOCKABLE LIST -- //

    private static final Unlockable[] unlockableList = new Unlockable[]{
    };

    // -- PRIVATE CONSTRUCTOR -- //

    private Unlockables() {
    }

    // -- HELPER METHODS -- //

    public static Unlockable[] values() {
        return unlockableList;
    }

    public static List<Unlockable> valuesSansKits() {
        return Arrays.asList(unlockableList).stream().filter(perk -> !(perk instanceof UnlockableKit)).
                collect(Collectors.toList());
    }

    public static Unlockable valueOf(final String name) {
        if (name != null) {
            for (Unlockable perk : unlockableList) {
                if (perk.getName().equalsIgnoreCase(name)) {
                    return perk;
                }
            }
        }
        return null;
    }

    public static List<UnlockableKit> kits() {
        return Arrays.asList(unlockableList).stream().filter(UnlockableKit.class::isInstance).
                map(UnlockableKit.class::cast).collect(Collectors.toList());
    }

    public static Optional<UnlockableKit> kitValueOf(final String name) {
        if (name != null) {
            for (UnlockableKit kit : kits()) {
                if (kit.getName().equalsIgnoreCase(name)) {
                    return Optional.of(kit);
                }
            }
        }
        return Optional.empty();
    }
}
