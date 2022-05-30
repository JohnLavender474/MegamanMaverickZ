package com.mygdx.game.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Component;
import com.mygdx.game.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpriteComponent implements Component {
    private SpriteSettings spriteSettings;
    private final Sprite sprite = new Sprite();
}
