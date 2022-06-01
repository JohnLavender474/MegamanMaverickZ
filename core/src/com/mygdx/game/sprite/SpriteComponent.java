package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the rendering of the {@link Sprite} for the {@link com.mygdx.game.Entity}.
 */
@Getter
@Setter
public class SpriteComponent implements Component {
    private RenderingGround renderingGround = RenderingGround.PLAYGROUND;
    private SpriteSettings spriteSettings = new SpriteSettings() {};
    private Sprite sprite = new Sprite();
}
