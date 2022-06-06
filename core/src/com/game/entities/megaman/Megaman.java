package com.game.entities.megaman;

import com.badlogic.gdx.math.Rectangle;
import com.game.animations.AnimationComponent;
import com.game.animations.Animator;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerButtonActuator;
import com.game.controllers.ControllerComponent;
import com.game.entities.Entity;
import com.game.entities.*;
import com.game.screens.levels.LevelCameraFocusable;
import com.game.sprites.SpriteComponent;
import com.game.updatables.UpdatableComponent;
import com.game.utils.Direction;
import com.game.utils.TimedAnimation;
import com.game.world.BodyComponent;
import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.entities.ActorState.*;

/**
 * Megaman implementation of {@link Entity}.
 */
@Getter
public class Megaman extends Entity implements Actor, LevelCameraFocusable {

    // all constants are expressed in world units
    public static final float RUN_SPEED_PER_SECOND = 3.5f;
    public static final float JUMP_INIT_IMPULSE = 9f;
    public static final float GRAVITY_DELTA = 0.5f;
    public static final float MAX_GRAVITY = -9f;

    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class);
    private final Set<ActorState> states = EnumSet.noneOf(ActorState.class);
    private final Set<Direction> movementDirections = new HashSet<>();
    private final MegamanStats megamanStats;

    // default facing right, if not true then must be facing left
    private boolean facingRight = true;

    /**
     * Instantiates a new Megaman.
     */
    public Megaman(MegamanStats megamanStats) {
        this.megamanStats = megamanStats;
        for (Direction direction : Direction.values()) {
            collisionFlags.put(direction, false);
        }
        addComponent(new SpriteComponent());
        addComponent(defineBodyComponent());
        addComponent(defineAnimationComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
    }

    @Override
    public Rectangle getBoundingBox() {
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        return bodyComponent.getCollisionBox();
    }

    private void runLeft(float delta) {
        states.add(ActorState.RUNNING_LEFT);
        if (isColliding(Direction.LEFT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x -= RUN_SPEED_PER_SECOND * PPM * delta;
    }

    private void runRight(float delta) {
        states.add(RUNNING_RIGHT);
        if (isColliding(Direction.RIGHT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x += RUN_SPEED_PER_SECOND * PPM * delta;
    }

    private AnimationComponent defineAnimationComponent() {
        // Key supplier
        Supplier<String> keySupplier = () -> {
            if (is(DAMAGED)) {
                return "Damaged";
            } else if (isAirDashing()) {
                return "AirDash";
            } else if (isGroundDashing()) {
                return "GroundDash";
            } else if (isWallSliding()) {
                return "WallSlide";
            } else if (is(JUMPING)) {
                return "Jump";
            } else if (isRunning()) {
                return "Run";
            } else if (isClimbing()) {
                return "Climb";
            } else {
                return "Stand";
            }
        };
        // Define animations

        // Animations map
        Map<String, TimedAnimation> animations = new HashMap<>();
        // Define component
        Animator animator = new Animator(keySupplier, animations);
        AnimationComponent animationComponent = new AnimationComponent(animator);
        return animationComponent;
    }

    private BodyComponent defineBodyComponent() {
        BodyComponent bodyComponent = new BodyComponent();

        // TODO: define body component
        return bodyComponent;
    }

    private UpdatableComponent defineUpdatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // update running
        updatableComponent.getUpdatables().add((delta) -> {
            if (is(RUNNING_LEFT)) {
                facingRight = false;
                runLeft(delta);
            } else if (is(RUNNING_RIGHT)) {
                facingRight = true;
                runRight(delta);
            }
        });
        // update jumping
        updatableComponent.getUpdatables().add((delta) -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (is(JUMPING)) {
                bodyComponent.getImpulse().y += JUMP_INIT_IMPULSE * PPM;
            }
            if (bodyComponent.getImpulse().y <= 0f) {
                setIsNot(JUMPING);
            }
        });
        // update gravity
        updatableComponent.getUpdatables().add((delta) -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (is(GROUNDED)) {
                bodyComponent.getGravity().y = 0f;
            } else {
                bodyComponent.getGravity().y = Math.max(MAX_GRAVITY * PPM * delta,
                                                        bodyComponent.getGravity().y - (GRAVITY_DELTA * PPM * delta));
            }
        });
        return updatableComponent;
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.putActuator(ControllerButton.LEFT, new ControllerButtonActuator() {
            @Override
            public void onJustPressed(float delta) {
                setIs(RUNNING_LEFT);
                setIsNot(RUNNING_RIGHT);
            }
            @Override
            public void onJustReleased(float delta) {
                setIsNot(RUNNING_LEFT);
            }
        });
        controllerComponent.putActuator(ControllerButton.RIGHT, new ControllerButtonActuator() {
            @Override
            public void onJustPressed(float delta) {
                setIs(RUNNING_RIGHT);
                setIsNot(RUNNING_LEFT);
            }
            @Override
            public void onJustReleased(float delta) {
                setIsNot(RUNNING_RIGHT);
            }
        });
        controllerComponent.putActuator(ControllerButton.X, new ControllerButtonActuator() {
            @Override
            public void onJustPressed(float delta) {
                if (is(GROUNDED)) {
                    setIs(JUMPING);
                }
            }
            @Override
            public void onJustReleased(float delta) {
                setIsNot(JUMPING);
            }
        });
        return controllerComponent;
    }

}
