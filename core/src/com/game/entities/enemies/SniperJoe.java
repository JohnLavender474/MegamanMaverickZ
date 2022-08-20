package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.megaman.Megaman;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.ChargedShot;
import com.game.entities.projectiles.ChargedShotDisintegration;
import com.game.entities.projectiles.Fireball;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.objects.Timer;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.Supplier;

import static com.game.core.ConstVals.TextureAsset.ENEMIES_TEXTURE_ATLAS;
import static com.game.core.ConstVals.ViewVals.PPM;
import static com.game.core.ConstVals.SoundAsset.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.lang.Math.*;

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
        super(gameContext, megamanSupplier, .1f);
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineBodyComponent(spawn));
        shieldedTimer.setToEnd();
        shootingTimer.setToEnd();
    }

    @Override
    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return Map.of(
                Bullet.class, new DamageNegotiation(10),
                Fireball.class, new DamageNegotiation(15),
                ChargedShot.class, new DamageNegotiation(30, this::explode),
                ChargedShotDisintegration.class, new DamageNegotiation(15, this::explode));
    }

    private void shoot() {
        Vector2 trajectory = new Vector2(PPM * (isFacing(F_LEFT) ? -BULLET_SPEED : BULLET_SPEED), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().cpy().add(
                (isFacing(F_LEFT) ? -5f : 5f), -3.25f);
        gameContext.addEntity(new Bullet(gameContext, this, trajectory, spawn));
        getComponent(SoundComponent.class).requestSound(ENEMY_BULLET_SOUND);
    }

    private void setShielded(boolean isShielded) {
        this.isShielded = isShielded;
        (isShielded ? shieldedTimer : shootingTimer).reset();
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                setFacing(round(getMegaman().getComponent(BodyComponent.class).getPosition().x) <
                        round(getComponent(BodyComponent.class).getPosition().x) ? F_LEFT : F_RIGHT);
                Timer behaviorTimer = isShielded ? shieldedTimer : shootingTimer;
                behaviorTimer.update(delta);
                if (behaviorTimer.isFinished()) {
                    setShielded(!isShielded);
                }
            }
        });
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.25f * PPM, 1.25f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteAdapter() {
            @Override
            public boolean isFlipX() {
                return isFacing(F_RIGHT);
            }
        });
    }

    private AnimationComponent defineAnimationComponent() {
        Supplier<String> keySupplier = () -> isShielded ? "Shielded" : "Shooting";
        TextureAtlas textureAtlas = gameContext.getAsset(ENEMIES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> timedAnimations = Map.of(
            "Shooting", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShooting")),
            "Shielded", new TimedAnimation(textureAtlas.findRegion("SniperJoe/SniperJoeShielded")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setGravity(-50f * PPM);
        bodyComponent.setSize(PPM, 1.5f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        // hit box
        Fixture hitBox = new Fixture(this, DAMAGEABLE_BOX);
        hitBox.setSize(.75f * PPM, 1.15f * PPM);
        bodyComponent.addFixture(hitBox);
        // damage Box
        Fixture damageBox = new Fixture(this, DAMAGER_BOX);
        damageBox.setCenter(.75f * PPM, 1.25f * PPM);
        bodyComponent.addFixture(damageBox);
        // shield
        Fixture shield = new Fixture(this, SHIELD);
        shield.putUserData("reflectDir", "straight");
        shield.setSize(.35f * PPM, .85f * PPM);
        bodyComponent.addFixture(shield);
        // body pre-process
        bodyComponent.setPreProcess(delta -> {
            if (isShielded) {
                hitBox.setOffset(isFacing(F_LEFT) ? 3f : -3f, 0f);
                shield.setOffset(isFacing(F_LEFT) ? -5f : 5f, 0f);
            } else {
                hitBox.setOffset(0f, 0f);
            }
            shield.setActive(isShielded);
        });
        return bodyComponent;
    }

}
