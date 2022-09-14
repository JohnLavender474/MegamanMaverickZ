package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class SpriteUtils {

    public static void setSpriteToBounds(Sprite sprite, Rectangle bounds) {
        sprite.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

}
