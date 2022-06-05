package com.mygdx.game.megaman;

import com.mygdx.game.controllers.ControllerButton;
import com.mygdx.game.controllers.ControllerButtonActuator;
import com.mygdx.game.controllers.ControllerComponent;
import com.mygdx.game.core.Entity;
import com.mygdx.game.screens.game.GameCameraFocusable;
import com.mygdx.game.utils.Direction;
import com.mygdx.game.world.BodyComponent;
import lombok.Getter;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class Megaman extends Entity implements GameCameraFocusable {

    private static final float RUN_SPEED = 3.5f;

    private final Map<Direction, Boolean> collisionFlags = new EnumMap<>(Direction.class);
    private final Set<Direction> movementDirections = new HashSet<>();
    private final Set<MegamanState> currentStates = new HashSet<>();

    public Megaman() {
        for (Direction direction : Direction.values()) {
            collisionFlags.put(direction, false);
        }
        addComponent(defineControllerComponent());
    }

    public boolean is(MegamanState megamanState) {
        return currentStates.contains(megamanState);
    }

    public boolean isCollidingWithObstacleIn(Direction direction) {
        return collisionFlags.get(direction);
    }

    private void run(Direction direction) {
        // Regardless of whether the body is actually moved, add RUNNING to current states
        currentStates.add(MegamanState.RUNNING);
        // Apply body impulse only if not colliding with obstacle in direction
        if (isCollidingWithObstacleIn(direction)) {
            return;
        }
        BodyComponent bodyComponent = getComponent(BodyComponent.class);
        switch (direction) {
            case LEFT -> bodyComponent.getImpulse().x -= RUN_SPEED;
            case RIGHT -> bodyComponent.getImpulse().x += RUN_SPEED;
        }
    }

    private ControllerComponent defineControllerComponent() {
        ControllerComponent controllerComponent = new ControllerComponent();
        controllerComponent.putActuator(ControllerButton.LEFT, new ControllerButtonActuator() {
            @Override
            public void onPressContinued(float delta) {
                if (!movementDirections.contains(Direction.RIGHT)) {
                    run(Direction.LEFT);
                }
            }
        });
        controllerComponent.putActuator(ControllerButton.RIGHT, new ControllerButtonActuator() {
            @Override
            public void onPressContinued(float delta) {
                if (!movementDirections.contains(Direction.LEFT)) {
                    run(Direction.RIGHT);
                }
            }
        });
        return controllerComponent;
    }

}
