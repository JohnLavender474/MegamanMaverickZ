package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.core.Component;
import com.mygdx.game.core.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Defines the rendering of the {@link Sprite} for the {@link Entity}.
 */
@Getter
@Setter
public class SpriteComponent implements Component {
    private final Map<String, SpriteHandle> spriteHandles = new HashMap<>();
}
