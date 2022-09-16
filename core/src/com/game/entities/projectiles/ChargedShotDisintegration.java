package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.damage.Damager;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;

import static com.game.core.constants.SoundAsset.*;
import static com.game.core.constants.TextureAsset.MEGAMAN_CHARGED_SHOT;
import static com.game.core.constants.TextureAsset.MEGAMAN_HALF_CHARGED_SHOT;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class ChargedShotDisintegration extends Entity implements Damager {

    private static final float FULLY_CHARGED_DURATION = .75f;
    private static final float HALFWAY_CHARGED_DURATION = .15f;

    private final Timer timer;
    private final GameContext2d gameContext;
    private final Timer soundTimer = new Timer(.15f);

    @Getter
    private final boolean fullyCharged;

    public ChargedShotDisintegration(GameContext2d gameContext, Vector2 center,
                                     boolean isLeft, boolean fullyCharged) {
        super(gameContext);
        this.gameContext = gameContext;
        this.fullyCharged = fullyCharged;
        timer = new Timer(fullyCharged ? FULLY_CHARGED_DURATION : HALFWAY_CHARGED_DURATION);
        addComponent(new SoundComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(center));
        addComponent(defineSpriteComponent(center, isLeft));
        addComponent(defineAnimationComponent(gameContext));
        gameContext.addMessageListener(this);
    }

    @Override
    public void onDeath() {
        gameContext.removeMessageListener(this);
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            soundTimer.update(delta);
            if (soundTimer.isJustFinished()) {
                getComponent(SoundComponent.class).requestSound(ENEMY_DAMAGE_SOUND);
                if (fullyCharged) {
                    soundTimer.reset();
                }
            }
            timer.update(delta);
            if (timer.isFinished()) {
                setDead(true);
            }
        });
    }

    private BodyComponent defineBodyComponent(Vector2 center) {
        BodyComponent bodyComponent = new BodyComponent(ABSTRACT);
        bodyComponent.setCenter(center);
        Fixture damagerBox = new Fixture(this, new Rectangle(0f, 0f, PPM, PPM), DAMAGER);
        bodyComponent.addFixture(damagerBox);
        return bodyComponent;
    }

    private SpriteComponent defineSpriteComponent(Vector2 center, boolean isLeft) {
        Sprite sprite = new Sprite();
        if (fullyCharged) {
            sprite.setSize(1.75f * PPM, 1.75f * PPM);
        } else {
            sprite.setSize(1.25f * PPM, 1.25f * PPM);
        }
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean isFlipX() {
                return isLeft;
            }
        });
    }

    private AnimationComponent defineAnimationComponent(GameContext2d gameContext) {
        TextureRegion textureRegion;
        if (fullyCharged) {
            textureRegion = gameContext.getAsset(MEGAMAN_CHARGED_SHOT.getSrc(), TextureAtlas.class)
                    .findRegion("MegamanChargedShotCollision");
        } else {
            textureRegion = gameContext.getAsset(MEGAMAN_HALF_CHARGED_SHOT.getSrc(), TextureAtlas.class)
                    .findRegion("Collide");
        }
        return new AnimationComponent(new TimedAnimation(textureRegion, 3, .05f));
    }

}
