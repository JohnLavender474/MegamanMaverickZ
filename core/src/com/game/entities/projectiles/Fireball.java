package com.game.entities.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.Supplier;

import static com.game.constants.SoundAsset.ATOMIC_FIRE_SOUND;
import static com.game.constants.TextureAsset.FIRE;
import static com.game.constants.ViewVals.PPM;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class Fireball extends AbstractProjectile {

    private final Timer burnTimer = new Timer(1.5f);

    private boolean isLanded;
    private boolean wasLanded;

    public Fireball(GameContext2d gameContext, Entity owner, Vector2 impulse, Vector2 spawn) {
        super(gameContext, owner, .25f);
        addComponent(spriteComponent());
        addComponent(animationComponent());
        addComponent(updatableComponent());
        addComponent(bodyComponent(spawn, impulse));
    }

    @Override
    public void hit(Fixture fixture) {
        if (fixture.isAnyFixtureType(BLOCK, DAMAGEABLE, SHIELD)) {
            setLanded(true);
        }
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {
            if (!wasLanded && isLanded) {
                getComponent(SoundComponent.class).requestSound(ATOMIC_FIRE_SOUND);
            }
            wasLanded = isLanded;
            if (isLanded) {
                burnTimer.update(delta);
            }
            setDead(burnTimer.isFinished());
        });
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.25f * PPM, 1.25f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            private float rotation = 0f;

            @Override
            public void update(Sprite sprite1, float delta) {
                if (isLanded()) {
                    return;
                }
                rotation += delta * 2000f;
            }

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public float getRotation() {
                if (isLanded()) {
                    return 0f;
                }
                return rotation;
            }

        });
    }

    private AnimationComponent animationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(FIRE.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> isLanded() ? "Flame" : "Fireball";
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Flame", new TimedAnimation(textureAtlas.findRegion("Flame"), 4, .1f),
                "Fireball", new TimedAnimation(textureAtlas.findRegion("Fireball")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent bodyComponent(Vector2 spawn, Vector2 impulse) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> {
            if (isLanded()) {
                bodyComponent.setVelocityX(0f);
            }
        });
        bodyComponent.applyImpulse(impulse);
        bodyComponent.setGravity(-PPM * .35f);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, new Rectangle(0f, 0f, .85f * PPM, .85f * PPM), HITTER);
        projectile.setOffset(0f, -.15f * PPM);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, new Rectangle(0f, 0f, PPM, PPM), DAMAGER);
        bodyComponent.addFixture(damageBox);
        Fixture feet = new Fixture(this, new Rectangle(0f, 0f, PPM / 2f, 1f), FEET);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        return bodyComponent;
    }

}
