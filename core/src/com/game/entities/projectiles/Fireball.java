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
import com.game.sprites.SpriteAdapter;
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

import static com.game.core.ConstVals.SoundAsset.ATOMIC_FIRE_SOUND;
import static com.game.core.ConstVals.TextureAsset.FIRE_TEXTURE_ATLAS;
import static com.game.core.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.equalsAny;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class Fireball extends AbstractProjectile {

    private final Timer burnTimer = new Timer(1.5f);

    private boolean isLanded;
    private boolean wasLanded;

    public Fireball(GameContext2d gameContext, Entity owner, Vector2 impulse, Vector2 spawn) {
        super(gameContext, owner, .25f);
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn, impulse));
    }

    @Override
    public void hit(Fixture fixture) {
        if (equalsAny(fixture.getFixtureType(), BLOCK, DAMAGEABLE_BOX, SHIELD)) {
            setLanded(true);
        }
    }

    private UpdatableComponent defineUpdatableComponent() {
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

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.25f * PPM, 1.25f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            private float rotation = 0f;

            @Override
            public void update(float delta) {
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

    private AnimationComponent defineAnimationComponent() {
        TextureAtlas textureAtlas = gameContext.getAsset(FIRE_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        Supplier<String> keySupplier = () -> isLanded() ? "Flame" : "Fireball";
        Map<String, TimedAnimation> timedAnimations = Map.of(
                "Flame", new TimedAnimation(textureAtlas.findRegion("Flame"), 4, .1f),
                "Fireball", new TimedAnimation(textureAtlas.findRegion("Fireball")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn, Vector2 impulse) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPreProcess(delta -> {
            if (isLanded()) {
                bodyComponent.setVelocityX(0f);
            }
        });
        bodyComponent.applyImpulse(impulse);
        bodyComponent.setGravity(-50f * PPM);
        bodyComponent.setSize(PPM, PPM);
        bodyComponent.setCenter(spawn.x, spawn.y);
        Fixture projectile = new Fixture(this, HITTER_BOX);
        projectile.setSize(.85f * PPM, .85f * PPM);
        projectile.setOffset(0f, -.15f * PPM);
        bodyComponent.addFixture(projectile);
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.setSize(PPM, PPM);
        bodyComponent.addFixture(damageBox);
        Fixture feet = new Fixture(this, FEET);
        feet.setSize(PPM / 2f, 1f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        return bodyComponent;
    }

}
