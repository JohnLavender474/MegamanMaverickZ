package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.sprites.SpriteComponent;
import com.game.sprites.SpriteProcessor;
import com.game.updatables.UpdatableComponent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static com.game.assets.TextureAsset.DECORATIONS;
import static com.game.ViewVals.PPM;

public class Explosion extends Entity {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TimedAnimation timedAnimation;

    public Explosion(GameContext2d gameContext, Vector2 center) {
        super(gameContext);
        addComponent(animationComponent(gameContext));
        addComponent(spriteComponent(center));
        addComponent(updatableComponent());
    }

    private SpriteComponent spriteComponent(Vector2 center) {
        Sprite sprite = new Sprite();
        sprite.setSize(2.5f * PPM, 2.5f * PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite, new SpriteProcessor() {
            @Override
            public int getSpriteRenderPriority() {
                return 4;
            }
        });
    }

    private AnimationComponent animationComponent(GameContext2d gameContext) {
        timedAnimation = new TimedAnimation(gameContext.getAsset(
                DECORATIONS.getSrc(), TextureAtlas.class).findRegion("Explosion"), 11, .025f);
        timedAnimation.setLoop(false);
        return new AnimationComponent(timedAnimation);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {
            if (timedAnimation.isFinished()) {
                setDead(true);
            }
        });
    }

}
