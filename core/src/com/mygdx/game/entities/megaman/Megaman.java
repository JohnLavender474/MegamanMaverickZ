package com.mygdx.game.entities.megaman;

import com.mygdx.game.controllers.ControllerButton;
import com.mygdx.game.controllers.ControllerButtonActuator;
import com.mygdx.game.controllers.ControllerComponent;
import com.mygdx.game.core.Entity;
import com.mygdx.game.entities.*;
import com.mygdx.game.screens.game.GameCameraFocusable;
import com.mygdx.game.updatables.UpdatableComponent;
import com.mygdx.game.utils.Direction;
import com.mygdx.game.world.BodyComponent;
import lombok.Getter;

import java.util.*;

import static com.mygdx.game.entities.ActorState.RUNNING_LEFT;
import static com.mygdx.game.entities.ActorState.RUNNING_RIGHT;

@Getter
public class Megaman extends Entity implements Actor, GameCameraFocusable {

    private static final float RUN_SPEED = 3.5f;

    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class);
    private final Set<ActorState> states = EnumSet.noneOf(ActorState.class);
    private final Set<Direction> movementDirections = new HashSet<>();

    public Megaman() {
        for (Direction direction : Direction.values()) {
            collisionFlags.put(direction, false);
        }
        addComponent(defineUpdatableComponent());
        addComponent(defineControllerComponent());
    }

    public boolean is(ActorState actorState) {
        return states.contains(actorState);
    }

    public boolean isCollidingWithObstacle(Direction direction) {
        return collisionFlags.get(direction);
    }

    private void runLeft() {
        states.add(ActorState.RUNNING_LEFT);
        if (isCollidingWithObstacle(Direction.LEFT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x -= RUN_SPEED;
    }

    private void runRight() {
        states.add(RUNNING_RIGHT);
        if (isCollidingWithObstacle(Direction.RIGHT)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        bodyComponent.getImpulse().x += RUN_SPEED;
    }

    private UpdatableComponent defineUpdatableComponent() {
        UpdatableComponent updatableComponent = new UpdatableComponent();
        // update running
        updatableComponent.getUpdatables().add((delta) -> {
            if (is(RUNNING_LEFT)) {
                runLeft();
            } else if (is(RUNNING_RIGHT)) {
                runRight();
            }
        });

        return updatableComponent;
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.putActuator(ControllerButton.LEFT, new ControllerButtonActuator() {
            @Override
            public void onJustPressed(float delta) {
                if (!is(RUNNING_RIGHT)) {
                    setIs(RUNNING_LEFT);
                }
            }
        });
        controllerComponent.putActuator(ControllerButton.RIGHT, new ControllerButtonActuator() {
            @Override
            public void onJustPressed(float delta) {
                if (!is(RUNNING_LEFT)) {
                    setIs(RUNNING_RIGHT);
                }
            }
        });
        return controllerComponent;
    }

}
