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
    private final SpriteProcessor spriteProcessor;

    public SpriteComponent() {
        this(new Sprite());
    }

    public SpriteComponent(Sprite sprite) {
        this(sprite, new SpriteProcessor() {});
    }

    public SpriteComponent(SpriteProcessor spriteProcessor) {
        this(new Sprite(), spriteProcessor);
    }

    public SpriteComponent(Sprite sprite, SpriteProcessor spriteProcessor) {
        if (sprite == null || spriteProcessor == null) {
            throw new IllegalArgumentException("Sprite and sprite adapter cannot be null");
        }
        this.sprite = sprite;
        this.spriteProcessor = spriteProcessor;
    }

}
