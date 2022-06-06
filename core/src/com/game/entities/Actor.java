package com.game.entities;

import java.util.Set;

/**
 * Describes a game object that is capable of having any of the state defined in {@link ActorState}. Default
 * implementation requires only a {@link Set<ActorState>} instance be provided. In most cases, methods
 * {@link #is(ActorState)}, {@link #setIs(ActorState)}, and {@link #setIsNot(ActorState)} don't need to be overridden.
 */
public interface Actor {

    /**
     * Gets states.
     *
     * @return the states
     */
    Set<ActorState> getStates();

    /**
     * Is performing state
     *
     * @param state the state
     * @return if performing state
     */
    default boolean is(ActorState state) {
        return getStates().contains(state);
    }

    /**
     * Sets is performing state
     *
     * @param state the state
     */
    default void setIs(ActorState state) {
        getStates().add(state);
    }

    /**
     * Sets is not performing state
     *
     * @param state the state
     */
    default void setIsNot(ActorState state) {
        getStates().remove(state);
    }

    default boolean isStanding() {
        return getStates().contains(ActorState.STANDING_LEFT) ||
                getStates().contains(ActorState.STANDING_RIGHT);
    }

    default boolean isRunning() {
        return getStates().contains(ActorState.RUNNING_LEFT) ||
                getStates().contains(ActorState.RUNNING_RIGHT);
    }

    default boolean isDashing() {
        return getStates().contains(ActorState.DASHING_LEFT) ||
                getStates().contains(ActorState.DASHING_RIGHT);
    }

    default boolean isWallSliding() {
        return getStates().contains(ActorState.WALL_SLIDING_LEFT) ||
                getStates().contains(ActorState.WALL_SLIDING_RIGHT);
    }

}
