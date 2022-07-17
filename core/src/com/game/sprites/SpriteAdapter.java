package com.game.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

public interface SpriteAdapter {

    boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position);

    default float getAlpha() {
        return 1f;
    }

    default float getRotation() {
        return 0f;
    }

    default boolean isHidden() {
        return false;
    }

    default boolean isFlipX() {
        return false;
    }

    default boolean isFlipY() {
        return false;
    }

    default float getOffsetX() {
        return 0f;
    }

    default float getOffsetY() {
        return 0f;
    }

}
