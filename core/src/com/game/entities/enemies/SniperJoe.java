package com.game.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.ConstVals;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.cull.CullOnCamTransComponent;
import com.game.cull.CullOutOfCamBoundsComponent;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.decorations.Explosion;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.health.HealthComponent;
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
import com.game.world.FixtureType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;

public class SniperJoe extends AbstractEnemy implements Faceable {

    private static final float BULLET_SPEED = 15f;

    private final Timer shieldedTimer = new Timer(1.75f);
    private final Timer shootingTimer = new Timer(1.5f, new TimeMarkedRunnable(.15f, this::shoot),
            new TimeMarkedRunnable(.75f, this::shoot), new TimeMarkedRunnable(1.35f, this::shoot));

    @Setter
    @Getter
    private Facing facing;
    private boolean isShielded = true;

    public SniperJoe(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier);
        shieldedTimer.setToEnd();
        shootingTimer.setToEnd();
        damageNegotiation.put(Bullet.class, 10);
        addComponent(new CullOutOfCamBoundsComponent(() -> getComponent(BodyComponent.class).getCollisionBox(), 1.5f));
        addComponent(new HealthComponent(30, this::explode));
        addComponent(new CullOnCamTransComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineSpriteComponent());
    }

    private void shoot() {
        Vector2 trajectory = new Vector2(PPM * (isFacing(Facing.F_LEFT) ? -BULLET_SPEED : BULLET_SPEED), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().cpy().add(
                (isFacing(Facing.F_LEFT) ? -5f : 5f), -3.25f);
        gameContext.addEntity(new Bullet(gameContext, this, trajectory, spawn));
        gameContext.getAsset(ENEMY_BULLET_SOUND, Sound.class).play();
    }

    private void explode() {
        gameContext.addEntity(new Explosion(gameContext, getComponent(BodyComponent.class).getCenter()));
        getComponent(SoundComponent.class).requestSound(EXPLOSION_SOUND);
    }

    private void setShielded(boolean isShielded) {
        this.isShielded = isShielded;
        Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
        behaviorTimer.reset();
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            setFacing(Math.round(getMegaman().getComponent(BodyComponent.class).getPosition().x) <
                    Math.round(getComponent(BodyComponent.class).getPosition().x) ? Facing.F_LEFT : Facing.F_RIGHT);
            Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
            behaviorTimer.update(delta);
            if (behaviorTimer.isFinished()) {
                setShielded(!isShielded);
            }
        });
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.25f * PPM, 1.25f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public boolean isFlipX() {
                return isFacing(Facing.F_RIGHT);
            }

        });
    }

    private AnimationComponent defineAnimationComponent() {
        Supplier<String> keySupplier = () -> isShielded ? "Shielded" : "Shooting";
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_TEXTURE_ATLAS, TextureAtlas.class);
        Map<String, TimedAnimation> timedAnimations = Map.of(
            "Shooting", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShooting")),
            "Shielded", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShielded")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setSize(PPM, 1.5f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setGravity(-50f * PPM);
        // hit box
        Fixture hitBox = new Fixture(this, FixtureType.DAMAGEABLE_BOX);
        hitBox.setSize(.75f * PPM, 1.15f * PPM);
        bodyComponent.addFixture(hitBox);
        // damage Box
        Fixture damageBox = new Fixture(this, FixtureType.DAMAGER_BOX);
        damageBox.setCenter(.75f * PPM, 1.25f * PPM);
        bodyComponent.addFixture(damageBox);
        // shield
        Fixture shield = new Fixture(this, FixtureType.SHIELD);
        shield.putUserData("reflectDir", "straight");
        shield.setSize(.15f * PPM, .85f * PPM);
        bodyComponent.addFixture(shield);
        // body pre-process
        bodyComponent.setPreProcess(delta -> {
            if (isShielded) {
                hitBox.setOffset(isFacing(Facing.F_LEFT) ? 3f : -3f, 0f);
                shield.setOffset(isFacing(Facing.F_LEFT) ? -5f : 5f, 0f);
            } else {
                hitBox.setOffset(0f, 0f);
            }
            shield.setActive(isShielded);
        });
        return bodyComponent;
    }

}
