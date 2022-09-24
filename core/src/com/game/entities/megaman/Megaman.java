package com.game.entities.megaman;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.entities.hazards.LaserBeamer;
import com.game.events.Event;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.controllers.ControllerAdapter;
import com.game.controllers.ControllerComponent;
import com.game.damage.DamageNegotiation;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.entities.decorations.ExplosionOrb;
import com.game.entities.enemies.*;
import com.game.entities.projectiles.Bullet;
import com.game.entities.projectiles.Fireball;
import com.game.health.HealthComponent;
import com.game.levels.CameraFocusable;
import com.game.shapes.ShapeComponent;
import com.game.shapes.ShapeHandle;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteProcessor;
import com.game.sprites.SpriteComponent;
import com.game.updatables.Debugger;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Percentage;
import com.game.utils.objects.TimeMarkedRunnable;
import com.game.utils.objects.Timer;
import com.game.utils.objects.Wrapper;
import com.game.weapons.WeaponDef;
import com.game.world.BodyComponent;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.events.EventType.*;
import static com.game.levels.LevelStatus.*;
import static com.game.entities.megaman.MegamanVals.*;
import static com.game.GlobalKeys.COLLECTION;
import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.MEGAMAN_FIRE;
import static com.game.assets.TextureAsset.MEGAMAN;
import static com.game.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;
import static com.game.controllers.ControllerButton.*;
import static com.game.entities.AbstractBounds.ABSTRACT_BOUNDS;
import static com.game.entities.contracts.Facing.*;
import static com.game.entities.megaman.Megaman.AButtonTask.*;
import static com.game.entities.megaman.Megaman.AButtonTask._AIR_DASH;
import static com.game.entities.megaman.MegamanSpecialAbility.*;
import static com.game.entities.megaman.MegamanWeapon.*;
import static com.game.utils.UtilMethods.*;
import static com.game.world.BodySense.*;
import static com.game.world.BodySense.FEET_ON_GROUND;
import static com.game.world.BodyType.*;
import static com.game.world.FixtureType.*;

/** Megaman, dah bloo bombah! */
@Getter
@Setter
public class Megaman extends Entity implements Damageable, Faceable, CameraFocusable {

    public enum AButtonTask {
        _JUMP, _AIR_DASH
    }

    private static final Debugger debugger = new Debugger(true);

    private static final float CLAMP_X = 20f;
    private static final float CLAMP_Y = 35f;

    private static final float RUN_SPEED = 4f;
    private static final float WATER_RUN_SPEED = 2f;

    private static final float JUMP_VEL = 18f;
    private static final float WATER_JUMP_VEL = 25f;

    private static final float WALL_JUMP_VEL = 32f;
    private static final float WALL_JUMP_HORIZ = 15f;
    private static final float WALL_JUMP_IMPETUS_TIME = .2f;

    private static final float AIR_DASH_VEL = 12f;
    private static final float WATER_AIR_DASH_VEL = 6f;
    private static final float MAX_AIR_DASH_TIME = .25f;

    private static final float GROUND_SLIDE_VEL = 12f;
    private static final float WATER_GROUND_SLIDE_VEL = 6f;
    private static final float MAX_GROUND_SLIDE_TIME = .35f;

    private static final float GROUNDED_GRAVITY = -.125f;
    private static final float UNGROUNDED_GRAVITY = -.5f;
    private static final float WATER_UNGROUNDED_GRAVITY = -.25f;

    private static final float SHOOT_ANIM_TIME = .5f;

    private static final float DAMAGE_DURATION = .75f;
    private static final float DAMAGE_RECOVERY_TIME = 1.5f;
    private static final float DAMAGE_RECOVERY_FLASH_DURATION = .05f;

    private static final float TIME_TO_HALFWAY_CHARGED = .5f;
    private static final float TIME_TO_FULLY_CHARGED = 1.25f;

    private static final float EXPLOSION_ORB_SPEED = 3.5f;

    private static final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations = new HashMap<>() {{
        put(Bat.class, new DamageNegotiation(5));
        put(Met.class, new DamageNegotiation(5));
        put(MagFly.class, new DamageNegotiation(5));
        put(Bullet.class, new DamageNegotiation(10));
        put(Fireball.class, new DamageNegotiation(5));
        put(Dragonfly.class, new DamageNegotiation(5));
        put(Matasaburo.class, new DamageNegotiation(5));
        put(SniperJoe.class, new DamageNegotiation(10));
        put(SpringHead.class, new DamageNegotiation(5));
        put(FloatingCan.class, new DamageNegotiation(10));
        put(LaserBeamer.class, new DamageNegotiation(10));
        put(SuctionRoller.class, new DamageNegotiation(10));
    }};

    private final Percentage[] healthTanks;
    private final Supplier<Boolean> canChargeWeapons;
    private final Set<MegamanWeapon> megamanWeaponsAttained;
    private final Set<MegamanSpecialAbility> megamanSpecialAbilities;

    private final Timer damageTimer = new Timer(DAMAGE_DURATION);
    private final Timer airDashTimer = new Timer(MAX_AIR_DASH_TIME);
    private final Timer shootAnimationTimer = new Timer(SHOOT_ANIM_TIME);
    private final Timer groundSlideTimer = new Timer(MAX_GROUND_SLIDE_TIME);
    private final Timer damageRecoveryTimer = new Timer(DAMAGE_RECOVERY_TIME);
    private final Timer wallJumpImpetusTimer = new Timer(WALL_JUMP_IMPETUS_TIME);
    private final Timer damageRecoveryBlinkTimer = new Timer(DAMAGE_RECOVERY_FLASH_DURATION);
    private final Timer chargingTimer = new Timer(TIME_TO_FULLY_CHARGED, new TimeMarkedRunnable(TIME_TO_HALFWAY_CHARGED,
            () -> getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_CHARGING_SOUND, true)));

    private static MegamanWeaponDefs megamanWeaponDefs;

    private boolean recoveryBlink;
    private Facing facing = F_RIGHT;
    private MegamanWeapon currentWeapon;
    private AButtonTask aButtonTask = _JUMP;

    public Megaman(GameContext2d gameContext, Vector2 spawn) {
        super(gameContext);
        if (megamanWeaponDefs == null) {
            megamanWeaponDefs = new MegamanWeaponDefs(gameContext);
        }
        megamanWeaponDefs.setMegaman(this);
        MegamanGameInfo megamanGameInfo = gameContext.getBlackboardObject(MEGAMAN_INFO, MegamanGameInfo.class);
        healthTanks = megamanGameInfo.getHealthTanks();
        canChargeWeapons = megamanGameInfo.canChargeWeaponsSupplier();
        megamanWeaponsAttained = megamanGameInfo.getMegamanWeaponsAttained();
        megamanSpecialAbilities = megamanGameInfo.getMegamanSpecialAbilities();

        // TODO: temporarily adding special abilities at onset

        megamanSpecialAbilities.add(GROUND_SLIDE);
        megamanSpecialAbilities.add(WALL_JUMP);
        megamanSpecialAbilities.add(AIR_DASH);

        // TODO: Remove three above lines of code

        setCurrentWeapon(MEGA_BUSTER);
        addComponent(healthComponent(MEGAMAN_MAX_HEALTH));
        addComponent(controllerComponent());
        addComponent(updatableComponent());
        addComponent(bodyComponent(spawn));
        addComponent(animationComponent());
        addComponent(behaviorComponent());
        addComponent(spriteComponent());
        addComponent(new SoundComponent());
        // addComponent(shapeComponent());
        damageTimer.setToEnd();
        shootAnimationTimer.setToEnd();
        damageRecoveryTimer.setToEnd();
        wallJumpImpetusTimer.setToEnd();
    }

    private ShapeComponent shapeComponent() {
        List<ShapeHandle> shapeHandles = new ArrayList<>();
        getComponent(BodyComponent.class).getFixturesOfType(BOUNCEABLE).forEach(f -> {
            ShapeHandle shapeHandle = new ShapeHandle();
            shapeHandle.setShapeSupplier(f::getFixtureShape);
            shapeHandles.add(shapeHandle);
        });
        return new ShapeComponent(shapeHandles);
    }

    @Override
    public void onDeath() {
        gameContext.removeEventListener(this);
    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return damageNegotiations.keySet();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        DamageNegotiation damageNegotiation = damageNegotiations.get(damager.getClass());
        damageTimer.reset();
        damageNegotiation.runOnDamage();
        getComponent(HealthComponent.class).sub(damageNegotiation.getDamage(damager));
        getComponent(SoundComponent.class).requestSound(MEGAMAN_DAMAGE_SOUND);
    }

    @Override
    public Vector2 getFocus() {
        return bottomCenterPoint(getComponent(BodyComponent.class).getCollisionBox());
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished() || !damageRecoveryTimer.isFinished();
    }

    /**
     * Is damaged.
     *
     * @return if damaged
     */
    public boolean isDamaged() {
        return !damageTimer.isFinished();
    }

    /**
     * Is shooting.
     *
     * @return if shooting
     */
    public boolean isShooting() {
        return !shootAnimationTimer.isFinished();
    }

    /**
     * Returns if Megaman can charge weapons.
     *
     * @return if Megaman can charge weapons
     */
    public boolean canChargeWeapons() {
        return canChargeWeapons.get();
    }

    /**
     * Return if charging.
     *
     * @return if charging
     */
    public boolean isCharging() {
        return canChargeWeapons() && chargingTimer.getTime() >= TIME_TO_HALFWAY_CHARGED;
    }

    /**
     * Return if charging fully.
     *
     * @return if charging fully
     */
    public boolean isChargingFully() {
        return canChargeWeapons() && chargingTimer.isFinished();
    }

    /**
     * Get weapon def.
     *
     * @param megamanWeapon the weapon
     * @return the weapon def
     */
    public WeaponDef getWeaponDef(MegamanWeapon megamanWeapon) {
        return megamanWeaponDefs.get(megamanWeapon);
    }

    private void stopCharging() {
        chargingTimer.reset();
        getComponent(SoundComponent.class).stopLoopingSound(MEGA_BUSTER_CHARGING_SOUND);
    }

    private void shoot() {
        WeaponDef weaponDef = getWeaponDef(currentWeapon);
        if (!megamanWeaponsAttained.contains(currentWeapon)) {
            throw new IllegalStateException();
        }
        if (weaponDef == null || isDamaged() || getComponent(BehaviorComponent.class).is(GROUND_SLIDING, AIR_DASHING) ||
                weaponDef.isDepleted() || !weaponDef.isCooldownTimerFinished()) {
            return;
        }
        weaponDef.getWeaponsInstances().forEach(gameContext::addEntity);
        weaponDef.resetCooldownTimer();
        shootAnimationTimer.reset();
        weaponDef.runOnShoot();
    }

    private HealthComponent healthComponent(int maxHealth) {
        return new HealthComponent(maxHealth, () -> {
            List<Vector2> trajectories = new ArrayList<>() {{
                add(new Vector2(-EXPLOSION_ORB_SPEED, 0f));
                add(new Vector2(-EXPLOSION_ORB_SPEED, EXPLOSION_ORB_SPEED));
                add(new Vector2(0f, EXPLOSION_ORB_SPEED));
                add(new Vector2(EXPLOSION_ORB_SPEED, EXPLOSION_ORB_SPEED));
                add(new Vector2(EXPLOSION_ORB_SPEED, 0f));
                add(new Vector2(EXPLOSION_ORB_SPEED, -EXPLOSION_ORB_SPEED));
                add(new Vector2(0f, -EXPLOSION_ORB_SPEED));
                add(new Vector2(-EXPLOSION_ORB_SPEED, -EXPLOSION_ORB_SPEED));
            }};
            trajectories.forEach(trajectory -> gameContext.addEntity(new ExplosionOrb(
                    gameContext, getComponent(BodyComponent.class).getCenter(), trajectory)));
            gameContext.addEvent(new Event(PLAYER_DEAD));
        });
    }

    private UpdatableComponent updatableComponent() {
        return new UpdatableComponent(delta -> {
            // charging timer
            if (!canChargeWeapons()) {
                chargingTimer.reset();
            }
            // damage timer
            damageTimer.update(delta);
            if (isDamaged()) {
                chargingTimer.reset();
                getComponent(SoundComponent.class).stopLoopingSound(MEGA_BUSTER_CHARGING_SOUND);
                getComponent(BodyComponent.class).applyImpulse((isFacing(F_LEFT) ? .15f : -.15f) * PPM, 0f);
            }
            if (damageTimer.isJustFinished()) {
                damageRecoveryTimer.reset();
            }
            if (damageTimer.isFinished() && !damageRecoveryTimer.isFinished()) {
                damageRecoveryTimer.update(delta);
                damageRecoveryBlinkTimer.update(delta);
                if (damageRecoveryBlinkTimer.isFinished()) {
                    recoveryBlink = !recoveryBlink;
                    damageRecoveryBlinkTimer.reset();
                }
            }
            if (damageRecoveryTimer.isJustFinished()) {
                setRecoveryBlink(false);
            }
            // anim and wall jump impetus timers
            shootAnimationTimer.update(delta);
            wallJumpImpetusTimer.update(delta);
            // update weapon cool down timer
            megamanWeaponDefs.get(currentWeapon).updateCooldownTimer(delta);
        });
    }

    private ControllerComponent controllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.addControllerAdapter(DPAD_LEFT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                setFacing(behaviorComponent.is(WALL_SLIDING) ? F_RIGHT : F_LEFT);
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x > -RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(-PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased() {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
            }

            @Override
            public void onReleaseContinued() {
                if (!gameContext.isControllerButtonPressed(DPAD_RIGHT)) {
                    getComponent(BehaviorComponent.class).setIsNot(RUNNING);
                }
            }

        });
        controllerComponent.addControllerAdapter(DPAD_RIGHT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                setFacing(behaviorComponent.is(WALL_SLIDING) ? F_LEFT : F_RIGHT);
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x < RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased() {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
            }

            @Override
            public void onReleaseContinued() {
                if (!gameContext.isControllerButtonPressed(DPAD_LEFT)) {
                    getComponent(BehaviorComponent.class).setIsNot(RUNNING);
                }
            }

        });
        controllerComponent.addControllerAdapter(X, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (!canChargeWeapons()) {
                    return;
                }
                chargingTimer.update(delta);
                if (isDamaged()) {
                    stopCharging();
                }
            }

            @Override
            public void onJustReleased() {
                shoot();
                stopCharging();
            }

        });
        return controllerComponent;
    }

    private BehaviorComponent behaviorComponent() {
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        // Wall slide
        Behavior wallSlide = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (isDamaged() || !megamanSpecialAbilities.contains(WALL_JUMP)) {
                    return false;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                return wallJumpImpetusTimer.isFinished() && !bodyComponent.is(FEET_ON_GROUND) &&
                        ((bodyComponent.is(TOUCHING_WALL_SLIDE_LEFT) && gameContext.isControllerButtonPressed(DPAD_LEFT)) ||
                                (bodyComponent.is(TOUCHING_WALL_SLIDE_RIGHT) && gameContext.isControllerButtonPressed(DPAD_RIGHT)));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(WALL_SLIDING);
                setAButtonTask(_JUMP);
            }

            @Override
            protected void act(float delta) {
                getComponent(BodyComponent.class).applyResistanceY(1.25f);
            }

            @Override
            protected void end() {
                behaviorComponent.setIsNot(WALL_SLIDING);
                setAButtonTask(_AIR_DASH);
            }

        };
        behaviorComponent.addBehavior(wallSlide);
        // Jump
        Behavior jump = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (isDamaged() || gameContext.isControllerButtonPressed(DPAD_DOWN) || bodyComponent.is(HEAD_TOUCHING_BLOCK)) {
                    return false;
                }
                return behaviorComponent.is(JUMPING) ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f && gameContext.isControllerButtonPressed(A) :
                        // case 2
                        aButtonTask == _JUMP && gameContext.isControllerButtonJustPressed(A) &&
                                (bodyComponent.is(FEET_ON_GROUND) || behaviorComponent.is(WALL_SLIDING));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(JUMPING);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (behaviorComponent.is(WALL_SLIDING)) {
                    bodyComponent.applyImpulse((isFacing(F_LEFT) ? -1f : 1f) * WALL_JUMP_HORIZ * PPM,
                            WALL_JUMP_VEL * PPM);
                    wallJumpImpetusTimer.reset();
                } else {
                    bodyComponent.setVelocityY(JUMP_VEL * PPM);
                }
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                behaviorComponent.setIsNot(JUMPING);
                getComponent(BodyComponent.class).setVelocityY(0f);
            }

        };
        behaviorComponent.addBehavior(jump);
        // Air dash
        Behavior airDash = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (isDamaged() || !megamanSpecialAbilities.contains(AIR_DASH) || behaviorComponent.is(WALL_SLIDING) ||
                        getComponent(BodyComponent.class).is(FEET_ON_GROUND) ||
                        airDashTimer.isFinished()) {
                    return false;
                }
                return behaviorComponent.is(AIR_DASHING) ? gameContext.isControllerButtonPressed(A) :
                        gameContext.isControllerButtonJustPressed(A) && getAButtonTask() == _AIR_DASH;
            }

            @Override
            protected void init() {
                getComponent(SoundComponent.class).requestSound(WHOOSH_SOUND);
                getComponent(BodyComponent.class).setGravityOn(false);
                behaviorComponent.setIs(AIR_DASHING);
                setAButtonTask(_JUMP);
            }

            @Override
            protected void act(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                airDashTimer.update(delta);
                bodyComponent.setVelocityY(0f);
                if ((isFacing(F_LEFT) && bodyComponent.is(TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(F_RIGHT) && bodyComponent.is(TOUCHING_BLOCK_RIGHT))) {
                    return;
                }
                float x = AIR_DASH_VEL * PPM;
                if (isFacing(F_LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                airDashTimer.reset();
                bodyComponent.setGravityOn(true);
                behaviorComponent.setIsNot(AIR_DASHING);
                if (isFacing(F_LEFT)) {
                    bodyComponent.applyImpulse(-5f * PPM, 0f);
                } else {
                    bodyComponent.applyImpulse(5f * PPM, 0f);
                }
            }
        };
        behaviorComponent.addBehavior(airDash);
        // Ground slide
        Behavior groundSlide = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (!megamanSpecialAbilities.contains(GROUND_SLIDE)) {
                    return false;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (behaviorComponent.is(GROUND_SLIDING) && bodyComponent.is(HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                if (isDamaged() || !bodyComponent.is(FEET_ON_GROUND) || groundSlideTimer.isFinished()) {
                    return false;
                }
                if (!behaviorComponent.is(GROUND_SLIDING)) {
                    return gameContext.isControllerButtonPressed(DPAD_DOWN) && gameContext.isControllerButtonJustPressed(A);
                } else {
                    return gameContext.isControllerButtonPressed(DPAD_DOWN) && gameContext.isControllerButtonPressed(A);
                }
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(GROUND_SLIDING);
            }

            @Override
            protected void act(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                groundSlideTimer.update(delta);
                if (isDamaged() || (isFacing(F_LEFT) && bodyComponent.is(TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(F_RIGHT) && bodyComponent.is(TOUCHING_BLOCK_RIGHT))) {
                    return;
                }
                float x = GROUND_SLIDE_VEL * PPM;
                if (isFacing(F_LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                groundSlideTimer.reset();
                behaviorComponent.setIsNot(GROUND_SLIDING);
                if (isFacing(F_LEFT)) {
                    bodyComponent.applyImpulse(-5f * PPM, 0f);
                } else {
                    bodyComponent.applyImpulse(5f * PPM, 0f);
                }
            }
        };
        behaviorComponent.addBehavior(groundSlide);
        return behaviorComponent;
    }

    private BodyComponent bodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(DYNAMIC);
        bodyComponent.setClamp(CLAMP_X * PPM, CLAMP_Y * PPM);
        bodyComponent.maskForCustomCollisions(ABSTRACT_BOUNDS);
        bodyComponent.setPosition(spawn);
        bodyComponent.setWidth(.8f * PPM);
        Rectangle model1 = new Rectangle(0f, 0f, .625f * PPM, PPM / 16f);
        // feet and bounceable
        Fixture feet = new Fixture(this, new Rectangle(model1), FEET);
        bodyComponent.addFixture(feet);
        Fixture feetBounceable = new Fixture(this, new Rectangle(model1), BOUNCEABLE);
        bodyComponent.addFixture(feetBounceable);
        // head
        Fixture head = new Fixture(this, new Rectangle(model1), HEAD);
        head.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(head);
        Fixture headBounceable = new Fixture(this, new Rectangle(model1), BOUNCEABLE);
        headBounceable.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(headBounceable);
        Rectangle model2 = new Rectangle(0f, 0f, PPM / 16f, PPM / 16f);
        // left
        Fixture left = new Fixture(this, new Rectangle(model2), LEFT);
        left.setOffset(-.45f * PPM, .15f * PPM);
        bodyComponent.addFixture(left);
        Fixture leftBounceable = new Fixture(this, new Rectangle(model2), BOUNCEABLE);
        leftBounceable.setOffset(-.25f * PPM, .15f * PPM);
        bodyComponent.addFixture(leftBounceable);
        // right
        Fixture right = new Fixture(this, new Rectangle(model2), RIGHT);
        right.setOffset(.45f * PPM, .15f * PPM);
        bodyComponent.addFixture(right);
        Fixture rightBounceable = new Fixture(this, new Rectangle(model2), BOUNCEABLE);
        rightBounceable.setOffset(.25f * PPM, .15f * PPM);
        bodyComponent.addFixture(rightBounceable);
        // hitbox
        Fixture hitBox = new Fixture(this, new Rectangle(0f, 0f, .8f * PPM, .5f * PPM), DAMAGEABLE);
        bodyComponent.addFixture(hitBox);
        // force listener
        Fixture forceListener = new Fixture(this, bodyComponent.getCollisionBox(), FORCE_LISTENER);
        forceListener.putUserData(COLLECTION, new HashSet<>() {{
            add(MagFly.class);
            add(Matasaburo.class);
        }});
        bodyComponent.addFixture(forceListener);
        // pre-process
        bodyComponent.setPreProcess(delta -> {
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (behaviorComponent.is(GROUND_SLIDING)) {
                bodyComponent.setHeight(.45f * PPM);
                feet.setOffset(0f, -PPM / 4f);
                feetBounceable.setOffset(0f, -PPM / 5f);
                ((Rectangle) right.getFixtureShape()).setHeight(.15f * PPM);
                ((Rectangle) left.getFixtureShape()).setHeight(.15f * PPM);
            } else {
                bodyComponent.setHeight(.95f * PPM);
                feet.setOffset(0f, -PPM / 2f);
                feetBounceable.setOffset(0f, -PPM / 4f);
                ((Rectangle) right.getFixtureShape()).setHeight(.35f * PPM);
                ((Rectangle) left.getFixtureShape()).setHeight(.35f * PPM);
            }
            if (bodyComponent.getVelocity().y < 0f && !bodyComponent.is(FEET_ON_GROUND)) {
                bodyComponent.setGravity(UNGROUNDED_GRAVITY * PPM);
            } else {
                bodyComponent.setGravity(GROUNDED_GRAVITY * PPM);
            }
        });
        return bodyComponent;
    }

    private SpriteComponent spriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.65f * PPM, 1.35f * PPM);
        return new SpriteComponent(sprite, new SpriteProcessor() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(Position.BOTTOM_CENTER);
                return true;
            }

            @Override
            public float getAlpha() {
                return recoveryBlink ? 0f : 1f;
            }

            @Override
            public boolean isFlipX() {
                return getComponent(BehaviorComponent.class).is(WALL_SLIDING) ? isFacing(F_RIGHT) : isFacing(F_LEFT);
            }

            @Override
            public float getOffsetY() {
                return getComponent(BehaviorComponent.class).is(GROUND_SLIDING) ? .1f * -PPM : 0f;
            }

        });
    }

    private AnimationComponent animationComponent() {
        Supplier<String> keySupplier = () -> {
            if (gameContext.isLevelStatus(PAUSED)) {
                return null;
            }
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (isDamaged()) {
                return behaviorComponent.is(GROUND_SLIDING) ? "LayDownDamaged" : "Damaged";
            } else if (behaviorComponent.is(AIR_DASHING)) {
                if (isChargingFully()) {
                    return "AirDashCharging";
                } else if (isCharging()) {
                    return "AirDashHalfCharging";
                } else {
                    return "AirDash";
                }
            } else if (behaviorComponent.is(GROUND_SLIDING)) {
                if (isChargingFully()) {
                    return "GroundSlideCharging";
                } else if (isCharging()) {
                    return "GroundSlideHalfCharging";
                } else {
                    return "GroundSlide";
                }
            } else if (behaviorComponent.is(WALL_SLIDING)) {
                if (isShooting()) {
                    return "WallSlideShoot";
                } else if (isChargingFully()) {
                    return "WallSlideCharging";
                } else if (isCharging()) {
                    return "WallSlideHalfCharging";
                } else {
                    return "WallSlide";
                }
            } else if (behaviorComponent.is(JUMPING) || !bodyComponent.is(FEET_ON_GROUND)) {
                if (isShooting()) {
                    return "JumpShoot";
                } else if (isChargingFully()) {
                    return "JumpCharging";
                } else if (isCharging()) {
                    return "JumpHalfCharging";
                } else {
                    return "Jump";
                }
            } else if (bodyComponent.is(FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
                if (isShooting()) {
                    return "RunShoot";
                } else if (isChargingFully()) {
                    return "RunCharging";
                } else if (isCharging()) {
                    return "RunHalfCharging";
                } else {
                    return "Run";
                }
            } else if (behaviorComponent.is(CLIMBING)) {
                if (isShooting()) {
                    return "ClimbShoot";
                } else if (isChargingFully()) {
                    return "ClimbCharging";
                } else if (isCharging()) {
                    return "ClimbHalfCharging";
                } else {
                    return "Climb";
                }
            } else if (bodyComponent.is(FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
                if (isShooting()) {
                    return "SlipSlideShoot";
                } else if (isChargingFully()) {
                    return "SlipSlideCharging";
                } else if (isCharging()) {
                    return "SlipSlideHalfCharging";
                } else {
                    return "SlipSlide";
                }
            } else {
                if (isShooting()) {
                    return "StandShoot";
                } else if (isChargingFully()) {
                    return "StandCharging";
                } else if (isCharging()) {
                    return "StandHalfCharging";
                } else {
                    return "Stand";
                }
            }
        };
        Map<MegamanWeapon, Map<String, TimedAnimation>> weaponToAnimMap = new EnumMap<>(MegamanWeapon.class);
        final float chargingAnimTime = .125f;
        for (MegamanWeapon megamanWeapon : MegamanWeapon.values()) {

            // TODO: Temporary, do not include any but mega buster

            if (megamanWeapon != MEGA_BUSTER) {
                continue;
            }

            String textureAtlasKey;
            switch (megamanWeapon) {
                case MEGA_BUSTER -> textureAtlasKey = MEGAMAN.getSrc();
                case FLAME_TOSS -> textureAtlasKey = MEGAMAN_FIRE.getSrc();
                default -> throw new IllegalStateException();
            }
            TextureAtlas textureAtlas = gameContext.getAsset(textureAtlasKey, TextureAtlas.class);
            Map<String, TimedAnimation> animations = new HashMap<>();
            animations.put("Climb", new TimedAnimation(textureAtlas.findRegion("Climb"), 2, .125f));
            animations.put("ClimbShoot", new TimedAnimation(textureAtlas.findRegion("ClimbShoot")));
            animations.put("ClimbHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("ClimbHalfCharging"), 2, chargingAnimTime));
            animations.put("ClimbCharging", new TimedAnimation(
                    textureAtlas.findRegion("ClimbCharging"), 2, chargingAnimTime));
            animations.put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            animations.put("StandCharging", new TimedAnimation(
                    textureAtlas.findRegion("StandCharging"), 2, chargingAnimTime));
            animations.put("StandHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("StandHalfCharging"), 2, chargingAnimTime));
            animations.put("StandShoot", new TimedAnimation(textureAtlas.findRegion("StandShoot")));
            animations.put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 3, .05f));
            animations.put("LayDownDamaged", new TimedAnimation(textureAtlas.findRegion("LayDownDamaged"), 3, .05f));
            animations.put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 4, .125f));
            animations.put("RunCharging", new TimedAnimation(textureAtlas
                    .findRegion("RunCharging"), 4, chargingAnimTime));
            animations.put("RunHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("RunHalfCharging"), 4, chargingAnimTime));
            animations.put("RunShoot", new TimedAnimation(textureAtlas.findRegion("RunShoot"), 4, .125f));
            animations.put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
            animations.put("JumpCharging", new TimedAnimation(
                    textureAtlas.findRegion("JumpCharging"), 2, chargingAnimTime));
            animations.put("JumpHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("JumpHalfCharging"), 2, chargingAnimTime));
            animations.put("JumpShoot", new TimedAnimation(textureAtlas.findRegion("JumpShoot")));
            animations.put("WallSlide", new TimedAnimation(textureAtlas.findRegion("WallSlide")));
            animations.put("WallSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("WallSlideCharging"), 2, chargingAnimTime));
            animations.put("WallSlideHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("WallSlideHalfCharging"), 2, chargingAnimTime));
            animations.put("WallSlideShoot", new TimedAnimation(textureAtlas.findRegion("WallSlideShoot")));
            animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
            animations.put("GroundSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("GroundSlideCharging"), 2, chargingAnimTime));
            animations.put("GroundSlideHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("GroundSlideHalfCharging"), 2, chargingAnimTime));
            animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
            animations.put("AirDashCharging", new TimedAnimation(
                    textureAtlas.findRegion("AirDashCharging"), 2, chargingAnimTime));
            animations.put("AirDashHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("AirDashHalfCharging"), 2, chargingAnimTime));
            animations.put("SlipSlide", new TimedAnimation(textureAtlas.findRegion("SlipSlide")));
            animations.put("SlipSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("SlipSlideCharging"), 2, chargingAnimTime));
            animations.put("SlipSlideHalfCharging", new TimedAnimation(
                    textureAtlas.findRegion("SlipSlideHalfCharging"), 2, chargingAnimTime));
            animations.put("SlipSlideShoot", new TimedAnimation(textureAtlas.findRegion("SlipSlideShoot")));
            weaponToAnimMap.put(megamanWeapon, animations);
        }
        return new AnimationComponent(keySupplier, key -> weaponToAnimMap.get(currentWeapon).get(key));
    }

}
