package com.game.entities;

/**
 * The enumeration of all possible actor states. The {@link Actor} implementation is not required to use all
 * the actor state values.
 */
public enum ActorState {
    JUMPING,
    FALLING,
    DAMAGED,
    FLEEING,
    GROUNDED,
    ATTACKING,
    RUNNING_LEFT,
    RUNNING_RIGHT,
    CLIMBING_UP,
    CLIMBING_DOWN,
    WALL_SLIDING_LEFT,
    WALL_SLIDING_RIGHT,
    AIR_DASHING_LEFT,
    AIR_DASHING_RIGHT,
    GROUND_DASHING_LEFT,
    GROUND_DASHING_RIGHT
}

