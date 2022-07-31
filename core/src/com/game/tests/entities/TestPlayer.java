package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Entity;
import com.game.Message;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.controllers.ControllerAdapter;
import com.game.controllers.ControllerComponent;
import com.game.core.*;
import com.game.damage.DamageNegotiation;
import com.game.debugging.DebugRectComponent;
import com.game.damage.Damageable;
import com.game.damage.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.levels.CameraFocusable;
import com.game.sounds.SoundComponent;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.UtilMethods;
import com.game.utils.objects.Wrapper;
import com.game.weapons.WeaponDef;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.*;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;
import static com.game.controllers.ControllerButton.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.tests.entities.TestPlayer.AButtonTask.*;
import static com.game.tests.entities.TestPlayer.TestPlayerWeapon.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.BodySense.*;
import static com.game.world.FixtureType.*;
import static com.game.world.FixtureType.LEFT;
import static com.game.world.FixtureType.RIGHT;

@Getter
@Setter
public class TestPlayer extends Entity implements Damageable, Faceable, CameraFocusable {

    public enum AButtonTask {
        JUMP,
        AIR_DASH
    }

    public enum TestPlayerWeapon {
        MEGA_BUSTER,
        FLAME_BUSTER
    }

    private static final float EXPLOSION_ORB_SPEED = 3.5f;

    private final Music music;
    private final IController controller;
    private final IAssetLoader assetLoader;
    private final IMessageDispatcher messageDispatcher;
    private final IEntitiesAndSystemsManager esManager;

    private final Map<Class<? extends Damager>, DamageNegotiation> damageNegotiations = new HashMap<>();

    private final Timer damageTimer = new Timer(.75f);
    private final Timer chargingTimer = new Timer(.5f);
    private final Timer airDashTimer = new Timer(.25f);
    private final Timer groundSlideTimer = new Timer(.35f);
    private final Timer shootCoolDownTimer = new Timer(.1f);
    private final Timer shootAnimationTimer = new Timer(.5f);
    private final Timer damageRecoveryTimer = new Timer(1.5f);
    private final Timer wallJumpImpetusTimer = new Timer(.2f);
    private final Timer damageRecoveryBlinkTimer = new Timer(.05f);

    private final Map<TestPlayerWeapon, WeaponDef> testPlayerWeapons = new EnumMap<>(TestPlayerWeapon.class);
    private TestPlayerWeapon currentWeapon;

    private boolean recoveryBlink;
    private Facing facing = F_RIGHT;
    private AButtonTask aButtonTask = JUMP;

    public TestPlayer(Vector2 spawn, Music music, IController controller, IAssetLoader assetLoader,
                      IMessageDispatcher messageDispatcher, IEntitiesAndSystemsManager esManager) {
        this.music = music;
        this.esManager = esManager;
        this.controller = controller;
        this.assetLoader = assetLoader;
        this.messageDispatcher = messageDispatcher;
        defineWeapons();
        defineDamageNegotiations();
        setCurrentWeapon(FLAME_BUSTER);
        addComponent(new SoundComponent());
        addComponent(defineHealthComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
        addComponent(defineBehaviorComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugRectComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent());
        damageTimer.setToEnd();
        shootCoolDownTimer.setToEnd();
        shootAnimationTimer.setToEnd();
        wallJumpImpetusTimer.setToEnd();
        damageRecoveryTimer.setToEnd();
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
        getComponent(HealthComponent.class).sub(damageNegotiation.damage());
        Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanDamage.mp3")).play();
    }

    @Override
    public Vector2 getFocus() {
        return bottomCenterPoint(getComponent(BodyComponent.class).getCollisionBox());
    }

    @Override
    public boolean isInvincible() {
        return !damageTimer.isFinished() || !damageRecoveryTimer.isFinished();
    }

    public boolean isDamaged() {
        return !damageTimer.isFinished();
    }

    public boolean isShooting() {
        return !shootAnimationTimer.isFinished();
    }

    public boolean isCharging() {
        return chargingTimer.isFinished();
    }

    private void stopCharging() {
        chargingTimer.reset();
        getComponent(SoundComponent.class).stopLoopingSound(MEGA_BUSTER_CHARGING_SOUND);
    }

    private void defineDamageNegotiations() {
        damageNegotiations.put(TestMet.class, new DamageNegotiation(5));
        damageNegotiations.put(TestBullet.class, new DamageNegotiation(10));
        damageNegotiations.put(TestFireball.class, new DamageNegotiation(3));
        damageNegotiations.put(TestSniperJoe.class, new DamageNegotiation(5));
        damageNegotiations.put(TestFloatingCan.class, new DamageNegotiation(5));
        damageNegotiations.put(TestSuctionRoller.class, new DamageNegotiation(10));
    }

    private void defineWeapons() {
        Supplier<Vector2> spawn = () -> {
            Vector2 spawnPos = getComponent(BodyComponent.class).getCenter().add(facing == F_LEFT ? -15f : 15f, 1f);
            if (getComponent(BehaviorComponent.class).is(WALL_SLIDING)) {
                spawnPos.y += 3.5f;
            } else if (!getComponent(BodyComponent.class).is(FEET_ON_GROUND)) {
                spawnPos.y += 4.5f;
            }
            return spawnPos;
        };
        testPlayerWeapons.put(MEGA_BUSTER, new WeaponDef(() -> {
            Vector2 trajectory = new Vector2(15f * (facing == F_LEFT ? -PPM : PPM), 0f);
            if (isCharging()) {
                return new TestChargedShot(this, trajectory, spawn.get(), facing, assetLoader, esManager);
            } else {
                return new TestBullet(this, trajectory, spawn.get(), assetLoader, esManager);
            }
        }, .1f, () -> getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_BULLET_SHOT_SOUND)));
        testPlayerWeapons.put(FLAME_BUSTER, new WeaponDef(() -> {
            Vector2 impulse = new Vector2(35f * (isFacing(F_LEFT) ? -PPM : PPM), 10f * PPM);
            if (isCharging()) {
                // TODO: return charging fireball
            } else {
                // TODO: return normal fireball
            }
            return new TestFireball(assetLoader, this, impulse, spawn.get());
        }, .75f, () -> getComponent(SoundComponent.class).requestSound(CRASH_BOMBER_SOUND)));
    }

    private void shoot() {
        WeaponDef weaponDef = testPlayerWeapons.get(currentWeapon);
        if (weaponDef == null) {
            throw new IllegalStateException();
        }
        if (isDamaged() || getComponent(BehaviorComponent.class).is(GROUND_SLIDING, AIR_DASHING) ||
                weaponDef.isDepleted() || !weaponDef.isCooldownTimerFinished()) {
            return;
        }
        esManager.addEntity(weaponDef.getWeaponInstance());
        weaponDef.resetCooldownTimer();
        shootAnimationTimer.reset();
        weaponDef.runOnShoot();
    }

    private HealthComponent defineHealthComponent() {
        return new HealthComponent(100, () -> {
            messageDispatcher.addMessage(new Message(this, "DEAD"));
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
            trajectories.forEach(trajectory -> esManager.addEntity(new TestExplosionOrb(
                    assetLoader, getComponent(BodyComponent.class).getCenter(), trajectory)));
            Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanDefeat.mp3")).play();
            getComponent(SoundComponent.class).stopLoopingSound(MEGA_BUSTER_CHARGING_SOUND);
            music.stop();
        });
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            if (chargingTimer.isJustFinished()) {
                getComponent(SoundComponent.class).requestSound(MEGA_BUSTER_CHARGING_SOUND, true, .5f);
            }
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
                recoveryBlink = false;
            }
            shootAnimationTimer.update(delta);
            wallJumpImpetusTimer.update(delta);
            testPlayerWeapons.get(currentWeapon).updateCooldownTimer(delta);
        });
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.addControllerAdapter(DPAD_LEFT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (wallJumpImpetusTimer.isFinished()) {
                    setFacing(behaviorComponent.is(WALL_SLIDING) ? F_RIGHT : F_LEFT);
                }
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x > -4f * PPM) {
                    bodyComponent.applyImpulse(-PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased() {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
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
                if (wallJumpImpetusTimer.isFinished()) {
                    setFacing(behaviorComponent.is(WALL_SLIDING) ? F_LEFT : F_RIGHT);
                }
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x < 4f * PPM) {
                    bodyComponent.applyImpulse(PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased() {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
            }


        });
        controllerComponent.addControllerAdapter(X, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    chargingTimer.reset();
                    return;
                }
                chargingTimer.update(delta);
            }

            @Override
            public void onJustReleased() {
                shoot();
                stopCharging();
            }

        });
        return controllerComponent;
    }

    private BehaviorComponent defineBehaviorComponent() {
        BehaviorComponent behaviorComponent = new BehaviorComponent();
        Behavior wallSlide = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (isDamaged()) {
                    return false;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                return wallJumpImpetusTimer.isFinished() && !bodyComponent.is(FEET_ON_GROUND) &&
                        ((bodyComponent.is(TOUCHING_WALL_SLIDE_LEFT) && controller.isPressed(DPAD_LEFT)) ||
                                (bodyComponent.is(TOUCHING_WALL_SLIDE_RIGHT) && controller.isPressed(DPAD_RIGHT)));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(WALL_SLIDING);
                setAButtonTask(JUMP);
            }

            @Override
            protected void act(float delta) {
                getComponent(BodyComponent.class).applyResistanceY(1.25f);
            }

            @Override
            protected void end() {
                behaviorComponent.setIsNot(WALL_SLIDING);
                setAButtonTask(AIR_DASH);
            }

        };
        behaviorComponent.addBehavior(wallSlide);
        // Jump
        Behavior jump = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (isDamaged() || controller.isPressed(DPAD_DOWN) ||
                        bodyComponent.is(HEAD_TOUCHING_BLOCK)) {
                    return false;
                }
                return behaviorComponent.is(JUMPING) ?
                        // case 1
                        bodyComponent.getVelocity().y > 0f && controller.isPressed(A) :
                        // case 2
                        aButtonTask == JUMP && controller.isJustPressed(A) &&
                                (bodyComponent.is(FEET_ON_GROUND) || behaviorComponent.is(WALL_SLIDING));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(JUMPING);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (behaviorComponent.is(WALL_SLIDING)) {
                    bodyComponent.applyImpulse((isFacing(F_LEFT) ? -1f : 1f) * 15f * PPM, 32f * PPM);
                    wallJumpImpetusTimer.reset();
                } else {
                    bodyComponent.applyImpulse(0f, 18f * PPM);
                }
            }

            @Override
            protected void act(float delta) {}

            @Override
            protected void end() {
                behaviorComponent.setIsNot(JUMPING);
                getComponent(BodyComponent.class).getVelocity().y = 0f;
            }

        };
        behaviorComponent.addBehavior(jump);
        // Air dash
        Behavior airDash = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                if (isDamaged() || behaviorComponent.is(WALL_SLIDING) ||
                        getComponent(BodyComponent.class).is(FEET_ON_GROUND) ||
                        airDashTimer.isFinished()) {
                    return false;
                }
                return behaviorComponent.is(AIR_DASHING) ? controller.isPressed(A) :
                        controller.isJustPressed(A) && getAButtonTask() == AIR_DASH;
            }

            @Override
            protected void init() {
                getComponent(SoundComponent.class).requestSound(WHOOSH_SOUND, false);
                getComponent(BodyComponent.class).setGravityOn(false);
                behaviorComponent.setIs(AIR_DASHING);
                setAButtonTask(JUMP);
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
                float x = 12f * PPM;
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
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (behaviorComponent.is(GROUND_SLIDING) && bodyComponent.is(HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                if (isDamaged() || !bodyComponent.is(FEET_ON_GROUND) || groundSlideTimer.isFinished()) {
                    return false;
                }
                if (!behaviorComponent.is(GROUND_SLIDING)) {
                    return controller.isPressed(DPAD_DOWN) &&
                            controller.isJustPressed(A);
                } else {
                    return controller.isPressed(DPAD_DOWN) &&
                            controller.isPressed(A);
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
                float x = 12f * PPM;
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

    private BodyComponent defineBodyComponent(Vector2 spawn) {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPosition(spawn);
        bodyComponent.setWidth(.8f * PPM);
        Fixture feet = new Fixture(this, FEET);
        feet.setSize(9.5f, .75f);
        bodyComponent.addFixture(feet);
        Fixture head = new Fixture(this, HEAD);
        head.setSize(10f, 2f);
        head.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(head);
        Fixture left = new Fixture(this, LEFT);
        left.setWidth(1f);
        left.setOffset(-.45f * PPM, 0f);
        bodyComponent.addFixture(left);
        Fixture right = new Fixture(this, RIGHT);
        right.setWidth(1f);
        right.setOffset(.45f * PPM, 0f);
        bodyComponent.addFixture(right);
        Fixture hitBox = new Fixture(this, DAMAGEABLE_BOX);
        hitBox.setSize(PPM, .75f * PPM);
        hitBox.setDebugColor(Color.RED);
        bodyComponent.addFixture(hitBox);
        bodyComponent.setPreProcess(delta -> {
            hitBox.setOffset(0f, bodyComponent.is(FEET_ON_GROUND) ? 0f : 4f);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (behaviorComponent.is(GROUND_SLIDING)) {
                bodyComponent.setHeight(.45f * PPM);
                feet.setOffset(0f, -PPM / 4f);
                left.setHeight(PPM / 4f);
                right.setHeight(PPM / 4f);
            } else {
                bodyComponent.setHeight(.95f * PPM);
                feet.setOffset(0f, -PPM / 2f);
                left.setHeight(PPM * .5f);
                right.setHeight(PPM * .5f);
            }
            bodyComponent.setGravity((behaviorComponent.is(JUMPING) ? -20f : -60f) * PPM);
        });
        return bodyComponent;
    }

    private DebugRectComponent defineDebugRectComponent() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        DebugRectComponent debugRectComponent = new DebugRectComponent();
        debugRectComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.GREEN);
        bodyComponent.getFixtures().forEach(fixture -> debugRectComponent.addDebugHandle(
                fixture::getFixtureBox, fixture::getDebugColor));
        return debugRectComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        Sprite sprite = new Sprite();
        sprite.setSize(1.65f * PPM, 1.35f * PPM);
        return new SpriteComponent(sprite, new SpriteAdapter() {

            @Override
            public boolean setPositioning(Wrapper<Rectangle> bounds, Wrapper<Position> position) {
                bounds.setData(getComponent(BodyComponent.class).getCollisionBox());
                position.setData(BOTTOM_CENTER);
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
                return getComponent(BehaviorComponent.class).is(GROUND_SLIDING) ? -.035f * PPM : 0f;
            }

        });
    }

    private AnimationComponent defineAnimationComponent() {
        Supplier<String> keySupplier = () -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (isDamaged()) {
                return behaviorComponent.is(GROUND_SLIDING) ? "LayDownDamaged" : "Damaged";
            } else if (behaviorComponent.is(AIR_DASHING)) {
                return isCharging() ? "AirDashCharging" : "AirDash";
            } else if (behaviorComponent.is(GROUND_SLIDING)) {
                return isCharging() ? "GroundSlideCharging" : "GroundSlide";
            } else if (behaviorComponent.is(WALL_SLIDING)) {
                if (isShooting()) {
                    return "WallSlideShoot";
                } else if (isCharging()) {
                    return "WallSlideCharging";
                } else {
                    return "WallSlide";
                }
            } else if (behaviorComponent.is(JUMPING) || !bodyComponent.is(FEET_ON_GROUND)) {
                if (isShooting()) {
                    return "JumpShoot";
                } else if (isCharging()) {
                    return "JumpCharging";
                } else {
                    return "Jump";
                }
            } else if (bodyComponent.is(FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
                if (isShooting()) {
                    return "RunShoot";
                } else if (isCharging()) {
                    return "RunCharging";
                } else {
                    return "Run";
                }
            } else if (behaviorComponent.is(CLIMBING)) {
                if (isShooting()) {
                    return "ClimbShoot";
                } else if (isCharging()) {
                    return "ClimbCharging";
                } else {
                    return "Climb";
                }
            } else if (bodyComponent.is(FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
                if (isShooting()) {
                    return "SlipSlideShoot";
                } else if (isCharging()) {
                    return "SlipSlideCharging";
                } else {
                    return "SlipSlide";
                }
            } else {
                if (isShooting()) {
                    return "StandShoot";
                } else if (isCharging()) {
                    return "StandCharging";
                } else {
                    return "Stand";
                }
            }
        };
        Map<TestPlayerWeapon, Map<String, TimedAnimation>> weaponToAnimMap = new EnumMap<>(TestPlayerWeapon.class);
        for (TestPlayerWeapon weapon : TestPlayerWeapon.values()) {
            String textureAtlasKey;
            switch (weapon) {
                case MEGA_BUSTER -> textureAtlasKey = MEGAMAN_TEXTURE_ATLAS;
                case FLAME_BUSTER -> textureAtlasKey = MEGAMAN_FIRE_TEXTURE_ATLAS;
                default -> throw new IllegalStateException();
            }
            TextureAtlas textureAtlas = assetLoader.getAsset(textureAtlasKey, TextureAtlas.class);
            Map<String, TimedAnimation> animations = new HashMap<>();
            animations.put("Climb", new TimedAnimation(textureAtlas.findRegion("Climb"), 2, .125f));
            animations.put("ClimbShoot", new TimedAnimation(textureAtlas.findRegion("ClimbShoot")));
            animations.put("ClimbCharging", new TimedAnimation(textureAtlas.findRegion("ClimbCharging"), 2, .125f));
            animations.put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            animations.put("StandCharging", new TimedAnimation(textureAtlas.findRegion("StandCharging"), 2, .125f));
            animations.put("StandShoot", new TimedAnimation(textureAtlas.findRegion("StandShoot")));
            animations.put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 3, .05f));
            animations.put("LayDownDamaged", new TimedAnimation(textureAtlas.findRegion("LayDownDamaged"), 3, .05f));
            animations.put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 4, .125f));
            animations.put("RunCharging", new TimedAnimation(textureAtlas.findRegion("RunCharging"), 4, .125f));
            animations.put("RunShoot", new TimedAnimation(textureAtlas.findRegion("RunShoot"), 4, .125f));
            animations.put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
            animations.put("JumpCharging", new TimedAnimation(textureAtlas.findRegion("JumpCharging"), 2, .125f));
            animations.put("JumpShoot", new TimedAnimation(textureAtlas.findRegion("JumpShoot")));
            animations.put("WallSlide", new TimedAnimation(textureAtlas.findRegion("WallSlide")));
            animations.put("WallSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("WallSlideCharging"), 2, .125f));
            animations.put("WallSlideShoot", new TimedAnimation(textureAtlas.findRegion("WallSlideShoot")));
            animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
            animations.put("GroundSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("GroundSlideCharging"), 2, .125f));
            animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
            animations.put("AirDashCharging", new TimedAnimation(textureAtlas.findRegion("AirDashCharging"), 2, .125f));
            animations.put("SlipSlide", new TimedAnimation(textureAtlas.findRegion("SlipSlide")));
            animations.put("SlipSlideCharging", new TimedAnimation(
                    textureAtlas.findRegion("SlipSlideCharging"), 2, .125f));
            animations.put("SlipSlideShoot", new TimedAnimation(textureAtlas.findRegion("SlipSlideShoot")));
            weaponToAnimMap.put(weapon, animations);
        }
        return new AnimationComponent(keySupplier, key -> weaponToAnimMap.get(currentWeapon).get(key));
    }

}
