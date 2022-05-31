package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the rendering of the sprite for the {@link com.mygdx.game.Entity}.
 */
@Getter
@Setter
public class SpriteComponent implements Component {
    private SpriteSettings spriteSettings = new SpriteSettings() {};
    private Sprite sprite = new Sprite();
}
