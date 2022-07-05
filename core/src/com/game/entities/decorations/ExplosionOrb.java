package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.screens.levels.CullOnOutOfGameCamBounds;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Timer;
import lombok.Getter;

import java.util.Map;

import static com.game.ConstVals.TextureAssets.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

@Getter
public class ExplosionOrb extends Entity implements CullOnOutOfGameCamBounds {

    private final Timer cullTimer = new Timer(.5f);

    public ExplosionOrb(GameContext2d gameContext, Vector2 spawn, Vector2 trajectory) {
        addComponent(defineUpdatableComponent(trajectory));
        addComponent(defineAnimationComponent(gameContext));
        addComponent(defineSpriteComponent(spawn));
    }

    private UpdatableComponent defineUpdatableComponent(Vector2 trajectory) {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        updatableComponent.setUpdatable(delta ->
                getComponent(SpriteComponent.class).getSprite().translate(
                        trajectory.x * PPM * delta, trajectory.y * PPM * delta));
        return updatableComponent;
    }

    private SpriteComponent defineSpriteComponent(Vector2 spawn) {
        Sprite sprite = new Sprite();
        sprite.setSize(3f * PPM, 3f * PPM);
        sprite.setCenter(spawn.x, spawn.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent defineAnimationComponent(GameContext2d gameContext) {
        TextureRegion explosionOrb = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class)
                .findRegion("PlayerExplosionOrbs");
        Animator animator = new Animator(() -> "ExplosionOrb", Map.of("ExplosionOrb",
                new TimedAnimation(explosionOrb, 2, .075f)));
        return new AnimationComponent(animator);
    }

    @Override
    public Rectangle getCullBoundingBox() {
        return getComponent(SpriteComponent.class).getSprite().getBoundingRectangle();
    }


}
