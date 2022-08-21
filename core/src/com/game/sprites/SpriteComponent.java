package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.core.Component;
import lombok.Getter;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
public class SpriteComponent extends Component {

    private final Sprite sprite;
    private final SpriteAdapter spriteAdapter;

    public SpriteComponent() {
        this(new Sprite());
    }

    public SpriteComponent(Sprite sprite) {
        this(sprite, new SpriteAdapter() {});
    }

    public SpriteComponent(SpriteAdapter spriteAdapter) {
        this(new Sprite(), spriteAdapter);
    }

    public SpriteComponent(Sprite sprite, SpriteAdapter spriteAdapter) {
        if (sprite == null || spriteAdapter == null) {
            throw new IllegalArgumentException("Sprite and sprite adapter cannot be null");
        }
        this.sprite = sprite;
        this.spriteAdapter = spriteAdapter;
    }

}
