package com.game.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.game.assets.SoundAsset.*;
import static com.game.health.HealthVals.MAX_HEALTH;
import static com.game.assets.TextureAsset.MET;
import static com.game.ViewVals.PPM;
import static com.game.entities.enemies.Met.MetBehavior.*;
import static com.game.utils.UtilMethods.setBottomCenterToPoint;
import static com.game.world.BodySense.*;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;
import static java.lang.Math.*;

@Getter
@Setter
public class Met extends AbstractEnemy implements Faceable {

    public enum MetBehavior {
        SHIELDING,
        POP_UP,
        RUNNING,
        PANIC
    }

    private final Map<MetBehavior, Timer> metBehaviorTimers = Map.of(
            SHIELDING, new Timer(1.15f),
            RUNNING, new Timer(.5f),
            POP_UP, new Timer(.5f),
            PANIC, new Timer(1f));

    private MetBehavior metBehavior;
    private Facing facing;

    public Met(GameContext2d gameContext, Supplier<Megaman> megamanSupplier, Vector2 spawn) {
        super(gameContext, megamanSupplier, .05f);
        addComponent(spriteComponent());
        addComponent(updatableComponent());
        addComponent(animationComponent());
        addComponent(bodyComponent(spawn));
        setMetBehavior(SHIELDING);
    }

    protected Map<Class<? extends Damager>, DamageNegotiation> defineDamageNegotiations() {
        return new HashMap<>() {{
            put(Bullet.class, new DamageNegotiation(10));
            put(Fireball.class, new DamageNegotiation(15));
            put(ChargedShot.class, new DamageNegotiation(MAX_HEALTH));
            put(ChargedShotDisintegration.class, new DamageNegotiation(15));
        }};
    }

    public void setMetBehavior(MetBehavior metBehavior) {
        this.metBehavior = metBehavior;
        metBehaviorTimers.values().forEach(Timer::reset);
        // getComponent(BodyComponent.class).setVelocity(0f, 0f);
    }

    private void shoot() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        Vector2 trajectory = new Vector2((isFacing(Facing.F_RIGHT) ? 10f : -10f) * PPM, .5f * PPM);
        Vector2 spawn = bodyComponent.getCenter().cpy().add(isFacing(Facing.F_RIGHT) ? .5f : -.5f, -4f);
        gameContext.addEntity(new Bullet(gameContext, this, trajectory, spawn));
        getComponent(SoundComponent.class).requestSound(ENEMY_BULLET_SOUND);
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(new StandardEnemyUpdater() {
            @Override
            public void update(float delta) {
                super.update(delta);
                if (getMegaman().isDead()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                bodyComponent.getFirstMatchingFixture(SHIELD).ifPresentOrElse(
                        shield -> shield.setActive(metBehavior == SHIELDING),
                        () -> {throw new IllegalStateException();});
                bodyComponent.getFirstMatchingFixture(DAMAGEABLE).ifPresentOrElse(
                        hitBox -> hitBox.setActive(metBehavior != SHIELDING),
                        () -> {throw new IllegalStateException();});
                switch (metBehavior) {
                    case SHIELDING -> {
                        Timer shieldingTimer = metBehaviorTimers.get(SHIELDING);
                        if (!playerIsAttacking()) {
                            shieldingTimer.update(delta);
                        }
                        if (shieldingTimer.isFinished()) {
                            setMetBehavior(POP_UP);
                        }
                    }
                    case POP_UP -> {
                        setFacing((round(megamanSupplier.get().getComponent(BodyComponent.class).getPosition().x) <
                                round(bodyComponent.getPosition().x)) ? Facing.F_LEFT : Facing.F_RIGHT);
                        Timer popUpTimer = metBehaviorTimers.get(POP_UP);
                        if (popUpTimer.isAtBeginning()) {
                            shoot();
                        }
                        popUpTimer.update(delta);
                        if (popUpTimer.isFinished()) {
                            setMetBehavior(RUNNING);
                        }
                    }
                    case RUNNING -> {
                        Timer runningTimer = metBehaviorTimers.get(RUNNING);
                        runningTimer.update(delta);
                        float runVel = 8f * PPM;
                        if (isFacing(Facing.F_LEFT)) {
                            runVel *= -1f;
                        }
                        if (bodyComponent.is(IN_WATER)) {
                            runVel /= 2f;
                        }
                        bodyComponent.setVelocityX(runVel);
                        if (runningTimer.isFinished() ||
                                (isFacing(Facing.F_LEFT) && bodyComponent.is(TOUCHING_HITBOX_LEFT)) ||
                                (isFacing(Facing.F_RIGHT) && bodyComponent.is(TOUCHING_HITBOX_RIGHT))) {
                            setMetBehavior(SHIELDING);
                        }
                    }
                    case PANIC -> {
                        Timer panicTimer = metBehaviorTimers.get(PANIC);
                        metBehaviorTimers.get(PANIC).update(delta);
                        if (panicTimer.isFinished()) {
                            disintegrate();
                            setDead(true);
                        }
                    }
                }
            }
        });
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setSize(.75f * PPM, .75f * PPM);
        setBottomCenterToPoint(bodyComponent.getCollisionBox(), spawn);
        bodyComponent.setGravity(-.5f * PPM);
        Rectangle model1 = new Rectangle(0f, 0f, .75f * PPM, .2f * PPM);
        // feet
        Fixture feet = new Fixture(this, new Rectangle(model1), FEET);
        feet.setOffset(0f, -.375f * PPM);
        bodyComponent.addFixture(feet);
        // water listener
        Fixture waterListener = new Fixture(this, new Rectangle(model1), WATER_LISTENER);
        bodyComponent.addFixture(waterListener);
        // force listener
        Fixture forceListener = new Fixture(this, new Rectangle(model1), FORCE_LISTENER);
        bodyComponent.addFixture(forceListener);
        // side model
        Rectangle sideModel = new Rectangle(0f, 0f, .1f * PPM, .75f * PPM);
        // left
        Fixture left = new Fixture(this, new Rectangle(sideModel), LEFT);
        left.setOffset(-.65f * PPM, 0f);
        bodyComponent.addFixture(left);
        // right
        Fixture right = new Fixture(this, new Rectangle(sideModel), RIGHT);
        right.setOffset(.65f * PPM, 0f);
        bodyComponent.addFixture(right);
        // shield
        Fixture shield = new Fixture(this, new Rectangle(0f, 0f, PPM, 1.5f * PPM), SHIELD);
        shield.putUserData("reflectDir", "up");
        bodyComponent.addFixture(shield);
        // box model
        Rectangle boxModel = new Rectangle(0f, 0f, .75f * PPM, .75f * PPM);
        // hit box
        Fixture hitBox = new Fixture(this, new Rectangle(boxModel), DAMAGEABLE);
        bodyComponent.addFixture(hitBox);
        // damage box
        Fixture damageBox = new Fixture(this, new Rectangle(boxModel), DAMAGER);
        bodyComponent.addFixture(damageBox);
        return bodyComponent;
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.5f * PPM, 1.5f * PPM);
        return new SpriteComponent(sprite, new StandardEnemySpriteProcessor() {
            @Override
            public boolean isFlipX() {
                return isFacing(Facing.F_LEFT);
            }
        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> switch (metBehavior) {
            case RUNNING -> "Run";
            case POP_UP -> "PopUp";
            case PANIC -> "RunNaked";
            case SHIELDING -> "LayDown";
        };
        TextureAtlas textureAtlas = gameContext.getAsset(MET.getSrc(), TextureAtlas.class);
        Map<String, TimedAnimation> timedAnimations = Map.of(
            "Run", new TimedAnimation(textureAtlas.findRegion("Run"), 2, .125f),
            "PopUp", new TimedAnimation(textureAtlas.findRegion("PopUp")),
            "RunNaked", new TimedAnimation(textureAtlas.findRegion("RunNaked"), 2, .1f),
            "LayDown", new TimedAnimation(textureAtlas.findRegion("LayDown")));
        return new AnimationComponent(keySupplier, timedAnimations::get);
    }

}
