package com.game.utils.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Position {

    TOP_LEFT(0, 2),
    TOP_CENTER(1, 2),
    TOP_RIGHT(2, 2),
    CENTER_LEFT(0, 1),
    CENTER(1, 1),
    CENTER_RIGHT(2, 1),
    BOTTOM_LEFT(0, 0),
    BOTTOM_CENTER(1, 0),
    BOTTOM_RIGHT(2, 0);

    private final int x;
    private final int y;

    public static Position getByGridIndex(int x, int y) {
        if (x < 0 || x > 2 || y < 0 || y > 2) {
            return null;
        }
        for (Position position : values()) {
            if (position.getX() == x && position.getY() == y) {
                return position;
            }
        }
        throw new IllegalStateException();
    }

}
