package com.game.entities.megaman;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.game.ConstVals.TextureAssets;
import com.game.GameContext2d;
import com.game.acting.Actor;
import com.game.acting.ActorAction;
import com.game.acting.ActorComponent;
import com.game.acting.ActorState;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.controllers.ControllerButton;
import com.game.entities.Entity;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.*;
import com.game.utils.Timer;
import com.game.world.BodyComponent;
import com.game.world.Collidable;
import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.acting.ActorState.*;

/**
 * Megaman implementation of {@link Entity}.
 */
@Getter
public class Megaman extends Entity implements Actor, Collidable, LevelCameraFocusable {

    public enum A_ButtonAction {
        JUMP,
        AIR_DASH
    }

    // all constants are expressed in world units
    public static final float GRAVITY_DELTA = 0.5f;
    public static final float RUN_SPEED_PER_SECOND = 3.5f;
    public static final float JUMP_IMPULSE_PER_SECOND = 9f;
    public static final float MAX_GRAVITY_PER_SECOND = -9f;

    private final GameContext2d gameContext;
    private final MegamanStats megamanStats;
    private final Set<Direction> movementDirections = new HashSet<>();

    private final Timer dashingTimer = new Timer(0.75f);
    private final Timer wallJumpingTimer = new Timer(0.1f);

    // Set sprite facing direction right, if false then face left
    private boolean facingRight = true;
    // Action for the A button, no action if null
    private A_ButtonAction aButtonAction;

    /**
     * Instantiates a new Megaman.
     */
    public Megaman(GameContext2d gameContext, MegamanStats megamanStats) {
        this.gameContext = gameContext;
        this.megamanStats = megamanStats;
        addComponent(new SpriteComponent());
        addComponent(defineBodyComponent());
        addComponent(defineActorComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
    }

    @Override
    public Rectangle getBoundingBox() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        return bodyComponent.getCollisionBox();
    }

    @Override
    public Set<ActorState> getActiveStates() {
        ActorComponent actorComponent = getComponent(ActorComponent.class);
        return actorComponent.getActiveStates();
    }

    @Override
    public Map<Direction, Boolean> getCollisionFlags() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        return bodyComponent.getCollisionFlags();
    }

    private void run(float delta) {
        if ((facingRight && isColliding(Direction.RIGHT)) ||
                (!facingRight && isColliding(Direction.LEFT))) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        float x = RUN_SPEED_PER_SECOND * PPM * delta;
        bodyComponent.getImpulse().x += facingRight ? x : -x;
    }

    private void jump(float delta) {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().y += JUMP_IMPULSE_PER_SECOND * PPM * delta;
    }

    private UpdatableComponent defineUpdatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // Is grounded
        updatableComponent.getUpdatables().add((delta) -> {
            if (isColliding(Direction.DOWN)) {
                setIs(GROUNDED);
            }
        });
        // Is wall sliding
        updatableComponent.getUpdatables().add((delta) -> {
            boolean canWallSlide = (isColliding(Direction.LEFT) &&
                    gameContext.isPressed(ControllerButton.LEFT)) ||
                    (isColliding(Direction.RIGHT) &&
                            gameContext.isPressed(ControllerButton.RIGHT));
            if (canWallSlide && !is(AIR_DASHING) && !is(GROUND_DASHING) && !is(GROUNDED)) {
                setIs(WALL_SLIDING);
            }
        });
        // Is jumping
        updatableComponent.getUpdatables().add((delta) -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (bodyComponent.getImpulse().y > 0f && !is(CLIMBING)) {
                setIs(JUMPING);
            }
        });
        // Set gravity
        updatableComponent.getUpdatables().add((delta) -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (is(CLIMBING) || is(AIR_DASHING)) {
                bodyComponent.getGravity().y = 0f;
            } else if (is(GROUNDED) || is(GROUND_DASHING)) {
                bodyComponent.getGravity().y = -0.15f;
            } else if (is(WALL_SLIDING)) {
                bodyComponent.getGravity().y = (MAX_GRAVITY_PER_SECOND * PPM * delta) / 3f;
            } else {
                bodyComponent.getGravity().y = Math.max(
                        MAX_GRAVITY_PER_SECOND * PPM * delta,
                        bodyComponent.getGravity().y - (GRAVITY_DELTA * PPM * delta));
            }
        });
        // Clear states
        updatableComponent.getUpdatables().add((delta) -> clearStates());
        return updatableComponent;
    }

    private ActorComponent defineActorComponent() {
        ActorComponent actorComponent = new ActorComponent(this);
        final BodyComponent bodyComponent = getComponent(BodyComponent.class);
        // Grounded
        actorComponent.getActions().add(new ActorAction(
                GROUNDED, List.of(), (delta) -> bodyComponent.getGravity().y = -0.1f));
        // Climbing
        List<Supplier<Boolean>> climbingOverrides = List.of(() -> is(DAMAGED));
        actorComponent.getActions().add(new ActorAction(
                CLIMBING, climbingOverrides, (delta) -> bodyComponent.getGravity().y = 0f));
        // Running
        List<Supplier<Boolean>> runningOverrides = List.of(() -> is(RUNNING),
                                                           () -> is(CLIMBING),
                                                               () -> is(DAMAGED),
                                                               () -> is(AIR_DASHING),
                                                               () -> is(GROUND_DASHING),
                                                               () -> isColliding(Direction.LEFT));
        actorComponent.getActions().add(new ActorAction(
                RUNNING, runningOverrides, this::run));
        // Ground dashing
        List<Supplier<Boolean>> groundDashingOverrides = List.of(() -> !is(GROUNDED),
                                                                 () -> isColliding(Direction.LEFT),
                                                                 () -> isColliding(Direction.RIGHT));
        actorComponent.getActions().add(new ActorAction(
                GROUND_DASHING, groundDashingOverrides, (delta) ->
                bodyComponent.getGravity().y = 0.15f));
        // Wall sliding
        List<Supplier<Boolean>> wallSlidingOverrides = List.of(() -> is(DAMAGED),
                                                               () -> is(JUMPING),
                                                               () -> is(GROUNDED),
                                                               () -> is(AIR_DASHING),
                                                               () -> is(GROUND_DASHING));
        actorComponent.getActions().add(new ActorAction(
                WALL_SLIDING, wallSlidingOverrides, (delta) ->
            bodyComponent.getGravity().y = (MAX_GRAVITY_PER_SECOND * PPM * delta) / 3f));
        // Jumping
        List<Supplier<Boolean>> jumpingOverrides = List.of(() -> is(DAMAGED),
                                                           () -> is(GROUNDED),
                                                           () -> is(WALL_SLIDING),
                                                           () -> is(HITTING_HEAD));
        actorComponent.getActions().add(new ActorAction(JUMPING, jumpingOverrides, this::jump));
        return actorComponent;
    }

    private AnimationComponent defineAnimationComponent() {
        // Key supplier
        Supplier<String> keySupplier = () -> {
            if (is(DAMAGED)) {
                return "Damaged";
            } else if (is(AIR_DASHING)) {
                return "AirDash";
            } else if (is(GROUND_DASHING)) {
                return "GroundDash";
            } else if (is(WALL_SLIDING)) {
                return "WallSlide";
            } else if (is(JUMPING)) {
                return "Jump";
            } else if (is(RUNNING)) {
                return "Run";
            } else if (is(CLIMBING)) {
                return "Climb";
            } else {
                return "Stand";
            }
        };
        // Define animations
        Map<String, TimedAnimation> animations = new HashMap<>();
        TextureAtlas textureAtlas = gameContext.loadAsset(
                TextureAssets.MEGAMAN_TEXTURE_ATLAS, TextureAtlas.class);
        animations.put("Climb", new TimedAnimation(
                textureAtlas.findRegion("Climb"), 2, 0.125f));
        animations.put("Stand", new TimedAnimation(
                textureAtlas.findRegion("Stand"), new float[]{1.5f, 0.15f}));
        animations.put("Damaged", new TimedAnimation(
                textureAtlas.findRegion("Damaged"), 3, 0.05f));
        animations.put("Run", new TimedAnimation(
                textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("Jump", new TimedAnimation(
                textureAtlas.findRegion("Run"), 4, 0.125f));
        animations.put("WallSlide", new TimedAnimation(
                textureAtlas.findRegion("WallSlide")));
        animations.put("GroundDash", new TimedAnimation(
                textureAtlas.findRegion("GroundDash")));
        animations.put("AirDash", new TimedAnimation(
                textureAtlas.findRegion("AirDash")));
        // Define component
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        return animationComponent;
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.getCollisionBox().setSize(0.75f, 1.5f);
        // TODO: define body component
        return bodyComponent;
    }

}
