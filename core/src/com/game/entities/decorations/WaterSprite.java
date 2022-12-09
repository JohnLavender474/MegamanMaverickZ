package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.entities.Entity;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;

import static com.game.ViewVals.PPM;
import static com.game.assets.TextureAsset.WATER;

public class WaterSprite extends Entity {

    public WaterSprite(GameContext2d gameContext, Vector2 pos, boolean isSurface) {
        super(gameContext);
        Rectangle splashBounds = new Rectangle(pos.x, pos.y, PPM, PPM);
        addComponent(spriteComponent(splashBounds));
        addComponent(animationComponent(isSurface));
    }

    protected SpriteComponent spriteComponent(Rectangle bounds) {
        Sprite sprite = new Sprite();
        sprite.setBounds(bounds.x, bounds.y, PPM, PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public int getSpriteRenderPriority() {
                return -1;
            }

            @Override
            public float getAlpha() {
                return .35f;
            }

        });
    }

    protected AnimationComponent animationComponent(boolean isSurface) {
        TextureRegion region = gameContext.getAsset(WATER.getSrc(), TextureAtlas.class)
                .findRegion(isSurface ? "Surface" : "Under");
        TimedAnimation animation = new TimedAnimation(region, 2, .15f);
        return new AnimationComponent(animation);
    }

}
