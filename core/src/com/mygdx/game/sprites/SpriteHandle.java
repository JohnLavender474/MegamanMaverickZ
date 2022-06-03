package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ConstVals.RenderingGround;
import com.mygdx.game.utils.Drawable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SpriteHandle implements Drawable {

    private float alpha = 1f;
    private boolean hidden = false;
    private boolean isFlipX = false;
    private boolean isFlipY = false;
    private Sprite sprite = new Sprite();
    private SpriteAnimator spriteAnimator;
    private Vector2 offset = new Vector2();
    private RenderingGround renderingGround;

    @Override
    public void draw(SpriteBatch spriteBatch)
            throws IllegalStateException {
        if (sprite == null) {
            throw new IllegalStateException("Sprite cannot be null");
        }
        sprite.draw(spriteBatch);
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
