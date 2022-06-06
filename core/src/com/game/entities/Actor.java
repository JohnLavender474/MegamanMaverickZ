package com.game.entities;

import com.game.utils.Direction;

import java.util.Map;
import java.util.Set;

/**
 * Describes a game object that is capable of having any of the state defined in {@link ActorState}. Default
 * implementation requires only a {@link Set<ActorState>} instance be provided. In most cases, methods
 * {@link #is(ActorState)}, {@link #setIs(ActorState)}, and {@link #setIsNot(ActorState)} don't need to be overridden.
 */
public interface Actor {

    /**
     * Gets collision flags map.
     *
     * @return collision flags map
     */
    Map<Direction, Boolean> getCollisionFlags();

    /**
     * Gets is colliding in the direction
     *
     * @param direction the direction
     * @return if colliding in the direction
     */
    default boolean isColliding(Direction direction) {
        return getCollisionFlags().get(direction);
    }

    /**
     * Get states.
     *
     * @return the states
     */
    Set<ActorState> getStates();

    /**
     * Clear states.
     */
    default void clearStates() {
        getStates().clear();
    }

    /**
     * Is performing state.
     *
     * @param state the state
     * @return if performing state
     */
    default boolean is(ActorState state) {
        return getStates().contains(state);
    }

    /**
     * Set is performing state.
     *
     * @param state the state
     */
    default void setIs(ActorState state) {
        getStates().add(state);
    }

    /**
     * Set is not performing state.
     *
     * @param state the state
     */
    default void setIsNot(ActorState state) {
        getStates().remove(state);
    }

    /**
     * Is standing.
     *
     * @return is standing
     */
    default boolean isClimbing() {
        return getStates().contains(ActorState.CLIMBING_UP) ||
                getStates().contains(ActorState.CLIMBING_DOWN);
    }

    /**
     * Is running.
     *
     * @return is running
     */
    default boolean isRunning() {
        return getStates().contains(ActorState.RUNNING_LEFT) ||
                getStates().contains(ActorState.RUNNING_RIGHT);
    }

    /**
     * Is air dashing.
     *
     * @return is air dashing
     */
    default boolean isAirDashing() {
        return getStates().contains(ActorState.AIR_DASHING_LEFT) ||
                getStates().contains(ActorState.AIR_DASHING_RIGHT);
    }

    /**
     * Is ground dashing.
     *
     * @return is ground dashing
     */
    default boolean isGroundDashing() {
        return getStates().contains(ActorState.GROUND_DASHING_LEFT) ||
                getStates().contains(ActorState.GROUND_DASHING_RIGHT);
    }

    /**
     * Is wall sliding.
     *
     * @return is wall sliding
     */
    default boolean isWallSliding() {
        return getStates().contains(ActorState.WALL_SLIDING_LEFT) ||
                getStates().contains(ActorState.WALL_SLIDING_RIGHT);
    }

}
