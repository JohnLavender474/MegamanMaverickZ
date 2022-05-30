package com.mygdx.game.sprite;

public interface SpriteSettings {

    default boolean hidden() {
        return false;
    }

    default boolean flipX() {
        return false;
    }

    default boolean flipY() {
        return false;
    }

    default float offsetX() {
        return 0.0f;
    }

    default float offsetY() {
        return 0.0f;
    }

    default float alpha() {
        return 1.0f;
    }

}
