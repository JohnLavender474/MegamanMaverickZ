package com.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.utils.Position;
import com.game.utils.Wrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * {@link Component} implementation for handling {@link Sprite}.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpriteComponent implements Component {

    private Sprite sprite = new Sprite();
    private SpriteAdapter spriteAdapter;

    public SpriteComponent(Sprite sprite) {
        this.sprite = sprite;
    }

}
