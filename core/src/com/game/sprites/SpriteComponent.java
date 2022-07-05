package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SpriteComponent implements Component {
    private final Sprite sprite;
    private SpriteAdapter spriteAdapter;
}
