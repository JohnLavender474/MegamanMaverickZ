package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the rendering of the {@link Sprite} for the {@link Entity}.
 */
@Getter
@Setter
public class SpriteComponent implements Component {
    private SpriteHandle spriteHandle = new SpriteHandle();
    private SpriteSettings spriteSettings = new SpriteSettings() {};
}
