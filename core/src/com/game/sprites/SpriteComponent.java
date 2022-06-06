package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
@RequiredArgsConstructor
public class SpriteComponent implements Component {
    private final Sprite sprite = new Sprite();
    private Consumer<Sprite> spriteConsumer;
}
