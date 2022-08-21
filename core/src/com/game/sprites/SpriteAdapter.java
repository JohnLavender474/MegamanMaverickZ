package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.enums.Position;
import com.game.utils.objects.Wrapper;

public interface SpriteAdapter {

    default int getSpriteRenderPriority() {
        return 0;
    }

    default void update(Sprite sprite, float delta) {}

    default boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
        return false;
    }

    default Vector2 getSizeTrans() {
        return Vector2.Zero;
    }

    default Vector2 getOrigin(Sprite sprite1) {
        return new Vector2(sprite1.getWidth() / 2f, sprite1.getHeight() / 2f);
    }

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
