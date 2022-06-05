package com.mygdx.game.entities;

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
     * @return if is performing state
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

}
