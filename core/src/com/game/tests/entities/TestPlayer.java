package com.game.tests.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals;
import com.game.Message;
import com.game.animations.AnimationComponent;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.controllers.ControllerAdapter;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerComponent;
import com.game.core.*;
import com.game.debugging.DebugRectComponent;
import com.game.entities.contracts.Damageable;
import com.game.entities.contracts.Damager;
import com.game.entities.contracts.Faceable;
import com.game.entities.contracts.Facing;
import com.game.health.HealthComponent;
import com.game.levels.CameraFocusable;
import com.game.sprites.SpriteAdapter;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.enums.Position;
import com.game.utils.objects.Timer;
import com.game.utils.UtilMethods;
import com.game.utils.objects.Wrapper;
import com.game.world.BodyComponent;
import com.game.world.BodySense;
import com.game.world.BodyType;
import com.game.world.Fixture;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.SoundAssets.*;
import static com.game.ConstVals.TextureAssets.MEGAMAN_TEXTURE_ATLAS;
import static com.game.ConstVals.TextureAssets.OBJECTS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;
import static com.game.entities.contracts.Facing.*;
import static com.game.utils.enums.Position.*;
import static com.game.world.FixtureType.*;

@Getter
@Setter
public class TestPlayer implements IEntity, Damageable, Faceable, CameraFocusable {

    public enum AButtonTask {
        JUMP,
        AIR_DASH
    }

    private static final float EXPLOSION_ORB_SPEED = 3.5f;

    private final IController controller;
    private final IAssetLoader assetLoader;
    private final IMessageDispatcher messageDispatcher;
    private final IEntitiesAndSystemsManager entitiesAndSystemsManager;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Set<Class<? extends Damager>> damagerMaskSet = Set.of(
            TestDamager.class, TestBullet.class, TestMet.class, TestSniperJoe.class, TestSuctionRoller.class);
    private final Timer airDashTimer = new Timer(.25f);
    private final Timer groundSlideTimer = new Timer(.35f);
    private final Timer wallJumpImpetusTimer = new Timer(.2f);
    private final Timer megaBusterChargingTimer = new Timer(.5f);
    private final Timer shootCoolDownTimer = new Timer(.1f);
    private final Timer shootAnimationTimer = new Timer(.5f);
    private final Timer damageRecoveryTimer = new Timer(1.5f);
    private final Timer damageRecoveryBlinkTimer = new Timer(.05f);
    private final Timer damageTimer = new Timer(.75f);
    private final Sound chargingSound;
    private final Music music;
    private boolean dead;
    private boolean isCharging;
    private boolean recoveryBlink;
    private Facing facing = F_RIGHT;
    private AButtonTask aButtonTask = AButtonTask.JUMP;

    public TestPlayer(Vector2 spawn, Music music, IController controller, IAssetLoader assetLoader,
                      IMessageDispatcher messageDispatcher, IEntitiesAndSystemsManager entitiesAndSystemsManager) {
        chargingSound = assetLoader.getAsset(MEGA_BUSTER_CHARGING_SOUND, Sound.class);
        this.music = music;
        this.controller = controller;
        this.assetLoader = assetLoader;
        this.messageDispatcher = messageDispatcher;
        this.entitiesAndSystemsManager = entitiesAndSystemsManager;
        addComponent(defineHealthComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
        addComponent(defineBehaviorComponent());
        addComponent(defineBodyComponent(spawn));
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(assetLoader.getAsset(MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class)));
        shootCoolDownTimer.setToEnd();
        shootAnimationTimer.setToEnd();
        wallJumpImpetusTimer.setToEnd();
        damageTimer.setToEnd();
        damageRecoveryTimer.setToEnd();
    }

    @Override
    public void takeDamageFrom(Damager damager) {
        damageTimer.reset();
        getComponent(HealthComponent.class).sub(20);
        Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanDamage.mp3")).play();
    }

    @Override
    public Vector2 getFocus() {
        return UtilMethods.bottomCenterPoint(getComponent(BodyComponent.class).getCollisionBox());
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
        return megaBusterChargingTimer.isFinished();
    }

    public void shoot() {
        if (isDamaged()) {
            chargingSound.stop();
            return;
        }
        Vector2 trajectory = new Vector2(15f * (facing == F_LEFT ? -PPM : PPM), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().add(
                facing == F_LEFT ? -12.5f : 12.5f, 1f);
        if (getComponent(BehaviorComponent.class).is(WALL_SLIDING)) {
            spawn.y += 3.5f;
        } else if (!getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND)) {
            spawn.y += 4.5f;
        }
        if (megaBusterChargingTimer.isFinished()) {
            // megaBusterChargingTimer.reset();
            chargingSound.stop();
            TestChargedShot testChargedShot = new TestChargedShot(
                    this, trajectory, spawn, facing, assetLoader, entitiesAndSystemsManager);
            entitiesAndSystemsManager.addEntity(testChargedShot);
        } else {
            TextureRegion yellowBullet = assetLoader.getAsset(OBJECTS_TEXTURE_ATLAS, TextureAtlas.class)
                    .findRegion("YellowBullet");
            TestBullet bullet = new TestBullet(this, trajectory, spawn, yellowBullet,
                    assetLoader, entitiesAndSystemsManager);
            entitiesAndSystemsManager.addEntity(bullet);
        }
        shootCoolDownTimer.reset();
        shootAnimationTimer.reset();
        Gdx.audio.newSound(Gdx.files.internal("sounds/MegaBusterBulletShot.mp3")).play();
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
            trajectories.forEach(trajectory -> entitiesAndSystemsManager.addEntity(new TestExplosionOrb(
                    assetLoader, getComponent(BodyComponent.class).getCenter(), trajectory)));
            Gdx.audio.newSound(Gdx.files.internal("sounds/MegamanDefeat.mp3")).play();
            music.stop();
        });
    }

    private UpdatableComponent defineUpdatableComponent() {
        return new UpdatableComponent(delta -> {
            if (megaBusterChargingTimer.isJustFinished()) {
                long id = chargingSound.loop();
                chargingSound.setVolume(id, .5f);
            }
            damageTimer.update(delta);
            if (isDamaged()) {
                chargingSound.stop();
                megaBusterChargingTimer.reset();
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
            wallJumpImpetusTimer.update(delta);
            shootCoolDownTimer.update(delta);
            shootAnimationTimer.update(delta);
            setCharging(megaBusterChargingTimer.isFinished());
        });
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.addControllerAdapter(ControllerButton.LEFT, new ControllerAdapter() {

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
        controllerComponent.addControllerAdapter(ControllerButton.RIGHT, new ControllerAdapter() {

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
        controllerComponent.addControllerAdapter(ControllerButton.X, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    megaBusterChargingTimer.reset();
                    return;
                }
                megaBusterChargingTimer.update(delta);
            }

            @Override
            public void onJustReleased() {
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (shootCoolDownTimer.isFinished() && !behaviorComponent.is(GROUND_SLIDING) &&
                        !behaviorComponent.is(AIR_DASHING)) {
                    shoot();
                }
                megaBusterChargingTimer.reset();
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
                return wallJumpImpetusTimer.isFinished() && !bodyComponent.is(BodySense.FEET_ON_GROUND) &&
                        ((bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_LEFT) &&
                                controller.isPressed(ControllerButton.LEFT)) ||
                                (bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_RIGHT) &&
                                        controller.isPressed(ControllerButton.RIGHT)));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(WALL_SLIDING);
                setAButtonTask(AButtonTask.JUMP);
            }

            @Override
            protected void act(float delta) {
                getComponent(BodyComponent.class).applyResistanceY(1.25f);
            }

            @Override
            protected void end() {
                behaviorComponent.setIsNot(WALL_SLIDING);
                setAButtonTask(AButtonTask.AIR_DASH);
            }

        };
        behaviorComponent.addBehavior(wallSlide);
        // Jump
        Behavior jump = new Behavior() {

            @Override
            protected boolean evaluate(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (isDamaged() || controller.isPressed(ControllerButton.DOWN) ||
                        bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return false;
                }
                return behaviorComponent.is(JUMPING) ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f && controller.isPressed(ControllerButton.A) :
                        // case 2
                        aButtonTask == AButtonTask.JUMP && controller.isJustPressed(ControllerButton.A) &&
                                (bodyComponent.is(BodySense.FEET_ON_GROUND) || behaviorComponent.is(WALL_SLIDING));
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
            protected void act(float delta) {
            }

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
                        getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND) ||
                        airDashTimer.isFinished()) {
                    return false;
                }
                return behaviorComponent.is(AIR_DASHING) ? controller.isPressed(ControllerButton.A) :
                        controller.isJustPressed(ControllerButton.A) && getAButtonTask() == AButtonTask.AIR_DASH;
            }

            @Override
            protected void init() {
                Gdx.audio.newSound(Gdx.files.internal("sounds/Whoosh.mp3")).play();
                getComponent(BodyComponent.class).setGravityOn(false);
                behaviorComponent.setIs(BehaviorType.AIR_DASHING);
                setAButtonTask(AButtonTask.JUMP);
            }

            @Override
            protected void act(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                airDashTimer.update(delta);
                bodyComponent.setVelocityY(0f);
                if ((isFacing(F_LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(F_RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
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
                behaviorComponent.setIsNot(BehaviorType.AIR_DASHING);
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
                if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) &&
                        bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                if (isDamaged() || !bodyComponent.is(BodySense.FEET_ON_GROUND) || groundSlideTimer.isFinished()) {
                    return false;
                }
                if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                    return controller.isPressed(ControllerButton.DOWN) &&
                            controller.isJustPressed(ControllerButton.A);
                } else {
                    return controller.isPressed(ControllerButton.DOWN) &&
                            controller.isPressed(ControllerButton.A);
                }
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(BehaviorType.GROUND_SLIDING);
            }

            @Override
            protected void act(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                groundSlideTimer.update(delta);
                if (isDamaged() ||
                        (isFacing(F_LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(F_RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
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
                behaviorComponent.setIsNot(BehaviorType.GROUND_SLIDING);
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
            hitBox.setOffset(0f, bodyComponent.is(BodySense.FEET_ON_GROUND) ? 0f : 4f);
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

    private DebugRectComponent defineDebugComponent() {
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

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
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
            } else if (behaviorComponent.is(JUMPING) || !bodyComponent.is(BodySense.FEET_ON_GROUND)) {
                if (isShooting()) {
                    return "JumpShoot";
                } else if (isCharging()) {
                    return "JumpCharging";
                } else {
                    return "Jump";
                }
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
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
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
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
        animations.put("WallSlideCharging", new TimedAnimation(textureAtlas.findRegion("WallSlideCharging"), 2, .125f));
        animations.put("WallSlideShoot", new TimedAnimation(textureAtlas.findRegion("WallSlideShoot")));
        animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
        animations.put("GroundSlideCharging", new TimedAnimation(
                textureAtlas.findRegion("GroundSlideCharging"), 2, .125f));
        animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
        animations.put("AirDashCharging", new TimedAnimation(textureAtlas.findRegion("AirDashCharging"), 2, .125f));
        animations.put("SlipSlide", new TimedAnimation(textureAtlas.findRegion("SlipSlide")));
        animations.put("SlipSlideCharging", new TimedAnimation(textureAtlas.findRegion("SlipSlideCharging"), 2, .125f));
        animations.put("SlipSlideShoot", new TimedAnimation(textureAtlas.findRegion("SlipSlideShoot")));
        return new AnimationComponent(keySupplier, animations);
    }

}
