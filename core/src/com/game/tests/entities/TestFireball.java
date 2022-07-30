package com.game.tests.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.damage.Damager;
import com.game.debugging.DebugRectComponent;
import com.game.entities.contracts.Hitter;
import com.game.entities.enemies.AbstractEnemy;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.UtilMethods;
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

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestFireball extends Entity implements Hitter, Damager {

    private final Timer burnTimer = new Timer(1.5f);

    private IEntity owner;
    private boolean isLanded;
    private boolean wasLanded;

    public TestFireball(IAssetLoader assetLoader, IEntity owner, Vector2 impulse, Vector2 spawn) {
        setOwner(owner);
        addComponent(new SoundComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineUpdatableComponent());
        addComponent(new CullOnCamTransComponent());
        addComponent(defineBodyComponent(spawn, impulse));
        addComponent(defineDebugRectComponent());
        addComponent(defineAnimationComponent(assetLoader));
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), .05f));
    }

    @Override
    public void hit(Fixture fixture) {
        /*
        if (fixture.getEntity().equals(owner) || (owner instanceof AbstractEnemy &&
                fixture.getEntity() instanceof AbstractEnemy)) {
            return;
        }
         */
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

    private AnimationComponent defineAnimationComponent(IAssetLoader assetLoader) {
        TextureAtlas textureAtlas = assetLoader.getAsset(FIRE_TEXTURE_ATLAS, TextureAtlas.class);
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

    private DebugRectComponent defineDebugRectComponent() {
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        getComponent(BodyComponent.class).getFixtures().forEach(fixture ->
            debugRectComponent.addDebugHandle(fixture::getFixtureBox, () -> {
                if (fixture.getFixtureType().equals(FEET)) {
                    return Color.GREEN;
                }
                return Color.BLUE;
            }));
        return debugRectComponent;
    }

}
