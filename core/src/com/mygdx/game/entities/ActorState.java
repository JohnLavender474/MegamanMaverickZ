package com.mygdx.game.entities;

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
    DASHING_LEFT,
    RUNNING_LEFT,
    DASHING_RIGHT,
    RUNNING_RIGHT,
    WALL_SLIDING_LEFT,
    WALL_SLIDING_RIGHT
}

