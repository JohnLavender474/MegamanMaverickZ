package com.game.acting;

/**
 * The enumeration of all possible actor states. The {@link Actor} implementation is not required to use all
 * the actor state values.
 */
public enum ActorState {
    JUMPING,
    DAMAGED,
    RUNNING,
    GROUNDED,
    CLIMBING,
    ATTACKING,
    AIR_DASHING,
    WALL_SLIDING,
    HITTING_HEAD,
    GROUND_DASHING,
}

