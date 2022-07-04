package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.Component;
import com.game.updatables.Updatable;
import lombok.*;

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
