package com.game.utils.enums;

/** The four directions left, right, up, and down. */
public enum Direction {

    DIR_UP, DIR_DOWN, DIR_LEFT, DIR_RIGHT;

    /**
     * The provided String is converted to all lowercase and a Direction value is returned based on the following cases:
     *     1. lowercase(direction) == "l" or "left": return {@link #DIR_LEFT}
     *     2. lowercase(direction) == "r" or "right": return {@link #DIR_RIGHT}
     *     3. lowercase(direction) == "d" or "down": return {@link #DIR_DOWN}
     *     4. lowercase(direction) == "u" or "up": return {@link #DIR_UP}
     *     5. return null
     *
     * @param direction the direction string
     * @return the matching direction enum value
     */
    public static Direction get(String direction) {
        switch (direction.toLowerCase()) {
            case "l", "left" -> {
                return DIR_LEFT;
            }
            case "r", "right" -> {
                return DIR_RIGHT;
            }
            case "u", "up" -> {
                return DIR_UP;
            }
            case "d", "down" -> {
                return DIR_DOWN;
            }
        }
        return null;
    }

}
