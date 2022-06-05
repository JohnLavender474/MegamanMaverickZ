package com.mygdx.game.entities.megaman;

import com.mygdx.game.controllers.ControllerButton;
import com.mygdx.game.controllers.ControllerButtonActuator;
import com.mygdx.game.controllers.ControllerComponent;
import com.mygdx.game.core.Entity;
import com.mygdx.game.entities.*;
import com.mygdx.game.screens.game.GameCameraFocusable;
import com.mygdx.game.sprites.SpriteComponent;
import com.mygdx.game.updatables.UpdatableComponent;
import com.mygdx.game.utils.Direction;
import com.mygdx.game.world.BodyComponent;
import lombok.Getter;
import org.xml.sax.SAXParseException;

import java.util.*;

import static com.mygdx.game.ConstVals.ViewVals.PPM;
import static com.mygdx.game.entities.ActorState.*;

/**
 * Megaman implementation of {@link Entity}.
 */
@Getter
public class Megaman extends Entity implements Actor, GameCameraFocusable {

    // all constants are expressed in world units
    public static final float RUN_SPEED_PER_SECOND = 3.5f;
    public static final float JUMP_INIT_IMPULSE = 9f;
    public static final float GRAVITY_DELTA = 0.5f;
    public static final float MAX_GRAVITY = -9f;

    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class);
    private final Set<ActorState> states = EnumSet.noneOf(ActorState.class);
    private final Set<MegamanAbility> megamanAbilities = new HashSet<>();
    private final Set<MegamanWeapon> megamanWeapons = new HashSet<>();
    private final Set<Direction> movementDirections = new HashSet<>();

    /**
     * Instantiates a new Megaman.
     */
    public Megaman() {
        for (Direction direction : Direction.values()) {
            collisionFlags.put(direction, false);
        }
        addComponent(defineBodyComponent());
        addComponent(defineSpriteComponent());
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
    }

    public boolean is(ActorState actorState) {
        return states.contains(actorState);
    }

    /**
     * Is colliding with obstacle.
     *
     * @param direction the direction of the collision to check for
     * @return if there is a collision in the direction
     */
    public boolean isCollidingWithObstacle(Direction direction) {
        return collisionFlags.get(direction);
    }

    private void runLeft(float delta) {
        states.add(ActorState.RUNNING_LEFT);
        if (isCollidingWithObstacle(Direction.LEFT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x -= RUN_SPEED_PER_SECOND * PPM * delta;
    }

    private void runRight(float delta) {
        states.add(RUNNING_RIGHT);
        if (isCollidingWithObstacle(Direction.RIGHT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x += RUN_SPEED_PER_SECOND * PPM * delta;
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
                runLeft(delta);
            } else if (is(RUNNING_RIGHT)) {
                runRight(delta);
            }
        });
        // update jumping
        updatableComponent.getUpdatables().add((delta) -> {
            BodyComponent bodyComponent = getComponent(BodyComponent.class);
            if (is(JUMPING)) {
                bodyComponent.getVelocity().y += JUMP_INIT_IMPULSE * PPM;
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
        return controllerComponent;
    }

    private SpriteComponent defineSpriteComponent() {
        SpriteComponent spriteComponent = new SpriteComponent();
        
        return spriteComponent;
    }

}
