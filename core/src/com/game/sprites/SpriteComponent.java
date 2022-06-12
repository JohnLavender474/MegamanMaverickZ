package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.utils.Updatable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
public class SpriteComponent implements Component {
    @Setter private Updatable spriteUpdater;
    private final Sprite sprite = new Sprite();
}
