package com.game.sprites;

import com.badlogic.gdx.math.Rectangle;
import com.game.utils.Position;

public interface SpriteAdapter {

    default Rectangle getBoundingBox() {
        return null;
    }

    default Position getPosition() {
        return Position.BOTTOM_CENTER;
    }

    default float getAlpha() {
        return 1f;
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
