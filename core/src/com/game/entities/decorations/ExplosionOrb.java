package com.game.entities.decorations;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.cull.CullOnEventComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import lombok.Getter;

import static com.game.assets.TextureAsset.DECORATIONS;
import static com.game.ViewVals.PPM;
import static com.game.events.EventType.*;

@Getter
public class ExplosionOrb extends Entity {

    private final Timer cullTimer = new Timer(.5f);

    public ExplosionOrb(GameContext2d gameContext, Vector2 spawn, Vector2 trajectory) {
        super(gameContext);
        addComponent(spriteComponent(spawn));
        addComponent(updatableComponent(trajectory));
        addComponent(animationComponent(gameContext));
        addComponent(new CullOutOfCamBoundsComponent(
                () -> getComponent(SpriteComponent.class).getSprite().getBoundingRectangle(), .1f));
        addComponent(new CullOnEventComponent(PLAYER_SPAWN));
    }

    private UpdatableComponent updatableComponent(Vector2 trajectory) {
        return new UpdatableComponent(delta -> getComponent(SpriteComponent.class).getSprite().translate(
                trajectory.x * PPM * delta, trajectory.y * PPM * delta));
    }

    private SpriteComponent spriteComponent(Vector2 spawn) {
        Sprite sprite = new Sprite();
        sprite.setSize(3f * PPM, 3f * PPM);
        sprite.setCenter(spawn.x, spawn.y);
        return new SpriteComponent(sprite);
    }

    private AnimationComponent animationComponent(GameContext2d gameContext) {
        return new AnimationComponent(new TimedAnimation(gameContext.getAsset(
                DECORATIONS.getSrc(), TextureAtlas.class).findRegion("PlayerExplosionOrbs"), 2, .075f));
    }

}
