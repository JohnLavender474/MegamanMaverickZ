package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.core.Component;
import com.game.updatables.Updatable;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
public class SpriteComponent implements Component {
    @Setter private Updatable spriteUpdater;
    private final Sprite sprite = new Sprite();
}
