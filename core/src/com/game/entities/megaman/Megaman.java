package com.game.entities.megaman;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.ConstVals.TextureAssets;
import com.game.Entity;
import com.game.GameContext2d;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.animations.TimedAnimation;
import com.game.behaviors.Behavior;
import com.game.behaviors.BehaviorComponent;
import com.game.behaviors.BehaviorType;
import com.game.contracts.Damageable;
import com.game.contracts.Damager;
import com.game.contracts.Faceable;
import com.game.contracts.Facing;
import com.game.controllers.ControllerAdapter;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerComponent;
import com.game.debugging.DebugComponent;
import com.game.entities.projectiles.Bullet;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import com.game.world.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.behaviors.BehaviorType.*;

/**
 * Megaman implementation of {@link Entity}.
 */
@Getter
@Setter
public class Megaman implements Entity, Damageable, Faceable, LevelCameraFocusable {

    private static final float RUN_SPEED = 4f;
    
    public enum AButtonTask {
        JUMP,
        AIR_DASH
    }
    
    private final GameContext2d gameContext;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean dead;
    
    private boolean isCharging;
    private Facing facing = Facing.RIGHT;
    private AButtonTask aButtonTask = AButtonTask.JUMP;

    private final Rectangle priorFocusBox = new Rectangle(0f, 0f, .8f * PPM, .95f * PPM);
    private final Rectangle currentFocusBox = new Rectangle(0f, 0f, .8f * PPM, .95f * PPM);

    private final Timer airDashTimer = new Timer(.25f);
    private final Timer groundSlideTimer = new Timer(.35f);
    private final Timer wallJumpImpetusTimer = new Timer(.2f);
    private final Timer megaBusterChargingTimer = new Timer(.5f);
    private final Timer shootCoolDownTimer = new Timer(.1f);
    private final Timer shootAnimationTimer = new Timer(.5f);

    private final Timer damageTimer = new Timer(.75f);
    private final Timer damageRecoveryTimer = new Timer(1.5f);
    private final Timer damageRecoveryBlinkTimer = new Timer(.05f);
    private boolean recoveryBlink;
    
    public Megaman(GameContext2d gameContext) {
        this.gameContext = gameContext;
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
        addComponent(defineBehaviorComponent());
        addComponent(defineBodyComponent());
        addComponent(defineDebugComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineAnimationComponent(gameContext.getAsset(TextureAssets.MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class)));
        shootCoolDownTimer.setToEnd();
        shootAnimationTimer.setToEnd();
        wallJumpImpetusTimer.setToEnd();
        damageTimer.setToEnd();
        damageRecoveryTimer.setToEnd();
    }

    @Override
    public void onDeath() {

    }

    @Override
    public Set<Class<? extends Damager>> getDamagerMaskSet() {
        return Set.of();
    }

    @Override
    public void takeDamageFrom(Class<? extends Damager> damagerClass) {
        damageTimer.reset();
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

    public void shoot() {
        if (isDamaged()) {
            return;
        }
        Vector2 trajectory = new Vector2(15f * (facing == Facing.LEFT ? -PPM : PPM), 0f);
        Vector2 spawn = getComponent(BodyComponent.class).getCenter().add(facing == Facing.LEFT ? -15f : 15f, 1f);
        if (getComponent(BehaviorComponent.class).is(WALL_SLIDING)) {
            spawn.y += 3.5f;
        } else if (!getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND)) {
            spawn.y += 4.5f;
        }
        Bullet bullet = new Bullet(gameContext, this, trajectory, spawn);
        gameContext.addEntity(bullet);
        shootCoolDownTimer.reset();
        shootAnimationTimer.reset();
    }

    private UpdatableComponent defineUpdatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        updatableComponent.setUpdatable(delta -> {
            priorFocusBox.set(currentFocusBox);
            UtilMethods.setBottomCenterToPoint(currentFocusBox, UtilMethods.bottomCenterPoint(getComponent(BodyComponent.class).getCollisionBox()));
            damageTimer.update(delta);
            if (damageTimer.isJustFinished()) {
                damageRecoveryTimer.reset();
            }
            wallJumpImpetusTimer.update(delta);
            shootCoolDownTimer.update(delta);
            shootAnimationTimer.update(delta);
            setCharging(megaBusterChargingTimer.isFinished());
        });
        return updatableComponent;
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent gameContextComponent = new ControllerComponent();
        gameContextComponent.addControllerAdapter(ControllerButton.LEFT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (wallJumpImpetusTimer.isFinished()) {
                    setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.RIGHT : Facing.LEFT);
                }
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x > -RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(-PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased(float delta) {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
            }

        });
        gameContextComponent.addControllerAdapter(ControllerButton.RIGHT, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    return;
                }
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (wallJumpImpetusTimer.isFinished()) {
                    setFacing(behaviorComponent.is(WALL_SLIDING) ? Facing.LEFT : Facing.RIGHT);
                }
                behaviorComponent.set(RUNNING, !behaviorComponent.is(WALL_SLIDING));
                if (bodyComponent.getVelocity().x < RUN_SPEED * PPM) {
                    bodyComponent.applyImpulse(PPM * 50f * delta, 0f);
                }
            }

            @Override
            public void onJustReleased(float delta) {
                getComponent(BehaviorComponent.class).setIsNot(RUNNING);
            }

        });
        gameContextComponent.addControllerAdapter(ControllerButton.X, new ControllerAdapter() {

            @Override
            public void onPressContinued(float delta) {
                if (isDamaged()) {
                    megaBusterChargingTimer.reset();
                    return;
                }
                megaBusterChargingTimer.update(delta);
            }

            @Override
            public void onJustReleased(float delta) {
                BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
                if (shootCoolDownTimer.isFinished() && !behaviorComponent.is(GROUND_SLIDING) && !behaviorComponent.is(AIR_DASHING)) {
                    shoot();
                }
                megaBusterChargingTimer.reset();
            }

        });
        return gameContextComponent;
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
                        ((bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_LEFT) && gameContext.isPressed(ControllerButton.LEFT)) ||
                                (bodyComponent.is(BodySense.TOUCHING_WALL_SLIDE_RIGHT) && gameContext.isPressed(ControllerButton.RIGHT)));
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
                if (isDamaged() || gameContext.isPressed(ControllerButton.DOWN) || bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return false;
                }
                return behaviorComponent.is(JUMPING) ?
                        // case 1
                        bodyComponent.getVelocity().y >= 0f && gameContext.isPressed(ControllerButton.A) :
                        // case 2
                        aButtonTask == AButtonTask.JUMP && gameContext.isJustPressed(ControllerButton.A) &&
                                (bodyComponent.is(BodySense.FEET_ON_GROUND) || behaviorComponent.is(WALL_SLIDING));
            }

            @Override
            protected void init() {
                behaviorComponent.setIs(JUMPING);
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                if (behaviorComponent.is(WALL_SLIDING)) {
                    bodyComponent.applyImpulse((isFacing(Facing.LEFT) ? -1f : 1f) * 15f * PPM, 32f * PPM);
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
                if (isDamaged() || behaviorComponent.is(WALL_SLIDING) || getComponent(BodyComponent.class).is(BodySense.FEET_ON_GROUND) || airDashTimer.isFinished()) {
                    return false;
                }
                return behaviorComponent.is(AIR_DASHING) ? gameContext.isPressed(ControllerButton.A) :
                        gameContext.isJustPressed(ControllerButton.A) && getAButtonTask() == AButtonTask.AIR_DASH;
            }

            @Override
            protected void init() {
                getComponent(BodyComponent.class).setGravityOn(false);
                behaviorComponent.setIs(BehaviorType.AIR_DASHING);
                setAButtonTask(AButtonTask.JUMP);
            }

            @Override
            protected void act(float delta) {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                airDashTimer.update(delta);
                bodyComponent.setVelocityY(0f);
                if ((isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
                    return;
                }
                float x = 12f * PPM;
                if (isFacing(Facing.LEFT)) {
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
                if (isFacing(Facing.LEFT)) {
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
                if (behaviorComponent.is(BehaviorType.GROUND_SLIDING) && bodyComponent.is(BodySense.HEAD_TOUCHING_BLOCK)) {
                    return true;
                }
                if (isDamaged() || !bodyComponent.is(BodySense.FEET_ON_GROUND) || groundSlideTimer.isFinished()) {
                    return false;
                }
                if (!behaviorComponent.is(BehaviorType.GROUND_SLIDING)) {
                    return gameContext.isPressed(ControllerButton.DOWN) && gameContext.isJustPressed(ControllerButton.A);
                } else {
                    return gameContext.isPressed(ControllerButton.DOWN) && gameContext.isPressed(ControllerButton.A);
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
                        (isFacing(Facing.LEFT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_LEFT)) ||
                        (isFacing(Facing.RIGHT) && bodyComponent.is(BodySense.TOUCHING_BLOCK_RIGHT))) {
                    return;
                }
                float x = 12f * PPM;
                if (isFacing(Facing.LEFT)) {
                    x *= -1f;
                }
                bodyComponent.setVelocityX(x);
            }

            @Override
            protected void end() {
                BodyComponent bodyComponent = getComponent(BodyComponent.class);
                groundSlideTimer.reset();
                behaviorComponent.setIsNot(BehaviorType.GROUND_SLIDING);
                if (isFacing(Facing.LEFT)) {
                    bodyComponent.applyImpulse(-5f * PPM, 0f);
                } else {
                    bodyComponent.applyImpulse(5f * PPM, 0f);
                }
            }
        };
        behaviorComponent.addBehavior(groundSlide);
        return behaviorComponent;
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent(BodyType.DYNAMIC);
        bodyComponent.setPosition(3f * PPM, 3f * PPM);
        bodyComponent.setWidth(.8f * PPM);
        bodyComponent.setPreProcess(delta -> {
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (behaviorComponent.is(GROUND_SLIDING)) {
                bodyComponent.setHeight(.45f * PPM);
            } else {
                bodyComponent.setHeight(.95f * PPM);
            }
            if (bodyComponent.getVelocity().y < 0f && !bodyComponent.is(BodySense.FEET_ON_GROUND)) {
                bodyComponent.setGravity(-60f * PPM);
            } else {
                bodyComponent.setGravity(-20f * PPM);
            }
        });
        Fixture feet = new Fixture(this, FixtureType.FEET);
        feet.setSize(10f, 1f);
        feet.setOffset(0f, -PPM / 2f);
        bodyComponent.addFixture(feet);
        Fixture head = new Fixture(this, FixtureType.HEAD);
        head.setSize(7f, 2f);
        head.setOffset(0f, PPM / 2f);
        bodyComponent.addFixture(head);
        Fixture left = new Fixture(this, FixtureType.LEFT);
        left.setSize(1f, .25f * PPM);
        left.setOffset(-.45f * PPM, 0f);
        bodyComponent.addFixture(left);
        Fixture right = new Fixture(this, FixtureType.RIGHT);
        right.setSize(1f, .25f * PPM);
        right.setOffset(.45f * PPM, 0f);
        bodyComponent.addFixture(right);
        Fixture hitBox = new Fixture(this, FixtureType.HIT_BOX);
        hitBox.setSize(.8f * PPM, .5f * PPM);
        bodyComponent.addFixture(hitBox);
        return bodyComponent;
    }

    private DebugComponent defineDebugComponent() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        DebugComponent debugComponent = new DebugComponent();
        debugComponent.addDebugHandle(bodyComponent::getCollisionBox, () -> Color.GREEN);
        bodyComponent.getFixtures().forEach(fixture -> debugComponent.addDebugHandle(fixture::getFixtureBox, () -> Color.GOLD));
        return debugComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.getSprite().setSize(1.65f * PPM, 1.35f * PPM);
        spriteComponent.setSpriteUpdater(delta -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            Sprite sprite = spriteComponent.getSprite();
            if (damageTimer.isFinished() && !damageRecoveryTimer.isFinished()) {
                damageRecoveryTimer.update(delta);
                damageRecoveryBlinkTimer.update(delta);
                sprite.setAlpha(recoveryBlink ? 0f : 1f);
                if (damageRecoveryBlinkTimer.isFinished()) {
                    recoveryBlink = !recoveryBlink;
                    damageRecoveryBlinkTimer.reset();
                }
            }
            if (damageRecoveryTimer.isJustFinished()) {
                sprite.setAlpha(1f);
            }
            if (behaviorComponent.is(WALL_SLIDING)) {
                sprite.setFlip(isFacing(Facing.RIGHT), false);
            } else {
                sprite.setFlip(isFacing(Facing.LEFT), false);
            }
            Vector2 bottomCenter = UtilMethods.bottomCenterPoint(bodyComponent.getCollisionBox());
            sprite.setCenterX(bottomCenter.x);
            sprite.setY(bottomCenter.y);
            if (behaviorComponent.is(GROUND_SLIDING)) {
                sprite.translateY(-.035f * PPM);
            }
        });
        return spriteComponent;
    }

    private AnimationComponent defineAnimationComponent(TextureAtlas textureAtlas) {
        Supplier<String> keySupplier = () -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            BehaviorComponent behaviorComponent = getComponent(BehaviorComponent.class);
            if (isDamaged()) {
                return behaviorComponent.is(GROUND_SLIDING) ? "LayDownDamaged" : "Damaged";
            } else if (behaviorComponent.is(AIR_DASHING)) {
                return "AirDash";
            } else if (behaviorComponent.is(GROUND_SLIDING)) {
                return "GroundSlide";
            } else if (behaviorComponent.is(WALL_SLIDING)) {
                return isShooting() ? "WallSlideShoot" : "WallSlide";
            } else if (behaviorComponent.is(JUMPING) || !bodyComponent.is(BodySense.FEET_ON_GROUND)) {
                return isShooting() ? "JumpShoot" : "Jump";
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && behaviorComponent.is(RUNNING)) {
                return isShooting() ? "RunShoot" : "Run";
            } else if (behaviorComponent.is(CLIMBING)) {
                return isShooting() ? "ClimbShoot" : "Climb";
            } else if (bodyComponent.is(BodySense.FEET_ON_GROUND) && Math.abs(bodyComponent.getVelocity().x) > 3f) {
                return isShooting() ? "SlipSlideShoot" : "SlipSlide";
            } else {
                return isShooting() ? "StandShoot" : "Stand";
            }
        };
        Map<String, TimedAnimation> animations = new HashMap<>();
        animations.put("Climb", new TimedAnimation(textureAtlas.findRegion("Climb"), 2, .125f));
        animations.put("ClimbShoot", new TimedAnimation(textureAtlas.findRegion("ClimbShoot")));
        animations.put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
        animations.put("StandShoot", new TimedAnimation(textureAtlas.findRegion("StandShoot")));
        animations.put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 3, .05f));
        animations.put("LayDownDamaged", new TimedAnimation(textureAtlas.findRegion("LayDownDamaged"), 3, .05f));
        animations.put("Run", new TimedAnimation(textureAtlas.findRegion("Run"), 4, .125f));
        animations.put("RunShoot", new TimedAnimation(textureAtlas.findRegion("RunShoot"), 4, .125f));
        animations.put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
        animations.put("JumpShoot", new TimedAnimation(textureAtlas.findRegion("JumpShoot")));
        animations.put("WallSlide", new TimedAnimation(textureAtlas.findRegion("WallSlide")));
        animations.put("WallSlideShoot", new TimedAnimation(textureAtlas.findRegion("WallSlideShoot")));
        animations.put("GroundSlide", new TimedAnimation(textureAtlas.findRegion("GroundSlide")));
        animations.put("AirDash", new TimedAnimation(textureAtlas.findRegion("AirDash")));
        animations.put("SlipSlide", new TimedAnimation(textureAtlas.findRegion("SlipSlide")));
        animations.put("SlipSlideShoot", new TimedAnimation(textureAtlas.findRegion("SlipSlideShoot")));
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        return animationComponent;
    }


}
