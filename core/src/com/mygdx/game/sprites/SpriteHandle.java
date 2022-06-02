package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SpriteHandle implements Comparable<SpriteHandle> {

    private Integer zpos = 0;
    private float alpha = 1f;
    private boolean hidden = false;
    private boolean isFlipX = false;
    private boolean isFlipY = false;
    private Sprite sprite = new Sprite();
    private SpriteAnimator spriteAnimator;
    private Vector2 offset = new Vector2();
    private RenderingGround renderingGround;

    @Override
    public int compareTo(SpriteHandle o) {
        return zpos.compareTo(o.getZpos());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SpriteHandle spriteHandle &&
                sprite.equals(spriteHandle.getSprite());
    }

    @Override
    public int hashCode() {
        return sprite.hashCode();
    }

}
