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
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damager;
import com.game.messages.MessageListener;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;

import static com.game.core.constants.Events.LEVEL_PAUSED;
import static com.game.core.constants.Events.LEVEL_UNPAUSED;
import static com.game.core.constants.SoundAsset.*;
import static com.game.core.constants.TextureAsset.MEGAMAN_CHARGED_SHOT;
import static com.game.core.constants.ViewVals.PPM;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

public class ChargedShotDisintegration extends Entity implements MessageListener, Damager {

    private final GameContext2d gameContext;
    private final Timer timer = new Timer(.75f);
    private final Timer soundTimer = new Timer(.15f);

    public ChargedShotDisintegration(GameContext2d gameContext, Vector2 center, boolean isLeft) {
        super(gameContext);
        this.gameContext = gameContext;
        addComponent(new SoundComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(center));
        addComponent(defineSpriteComponent(center, isLeft));
        addComponent(defineAnimationComponent(gameContext));
        gameContext.addMessageListener(this);
    }

    @Override
    public void listenToMessage(Object owner, Object message, float delta) {
        if (message.equals(LEVEL_PAUSED)) {
            getComponents().values().forEach(component -> {
                if (component instanceof CullOutOfCamBoundsComponent || component instanceof CullOnCamTransComponent) {
                    return;
                }
                component.setOn(false);
            });
        } else if (message.equals(LEVEL_UNPAUSED)) {
            getComponents().values().forEach(component -> {
                if (component instanceof CullOutOfCamBoundsComponent || component instanceof CullOnCamTransComponent) {
                    return;
                }
                component.setOn(true);
            });
        }
    }

    @Override
    public void onDeath() {
        gameContext.removeMessageListener(this);
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            soundTimer.update(delta);
            if (soundTimer.isFinished()) {
                getComponent(SoundComponent.class).requestSound(ENEMY_DAMAGE_SOUND);
                soundTimer.reset();
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
        sprite.setSize(1.75f * PPM, 1.75f * PPM);
        sprite.setCenter(center.x, center.y);
        return new SpriteComponent(sprite, new SpriteAdapter() {
            @Override
            public boolean isFlipX() {
                return isLeft;
            }
        });
    }

    private AnimationComponent defineAnimationComponent(GameContext2d gameContext) {
        TextureRegion textureRegion = gameContext.getAsset(
                        MEGAMAN_CHARGED_SHOT.getSrc(), TextureAtlas.class)
                .findRegion("MegamanChargedShotCollision");
        return new AnimationComponent(new TimedAnimation(textureRegion, 3, .05f));
    }

}
