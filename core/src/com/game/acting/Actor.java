package com.game.acting;

import java.util.Set;

/**
 * Describes a game object that is capable of having any of the state defined in {@link ActorState}. Default
 * implementation requires only a {@link Set<ActorState>} instance be provided. In most cases, methods
 * {@link #is(ActorState)}, {@link #setIs(ActorState)}, and {@link #setIsNot(ActorState)} don't need to be overridden.
 */
public interface Actor {

    /**
     * Get states.
     *
     * @return the states
     */
    Set<ActorState> getActiveStates();

    /**
     * Clear states.
     */
    default void clearStates() {
        getActiveStates().clear();
    }

    /**
     * Is performing state.
     *
     * @param state the state
     * @return if performing state
     */
    default boolean is(ActorState state) {
        return getActiveStates().contains(state);
    }

    /**
     * Set is performing state.
     *
     * @param state the state
     */
    default void setIs(ActorState state) {
        getActiveStates().add(state);
    }

    /**
     * Set is not performing state.
     *
     * @param state the state
     */
    default void setIsNot(ActorState state) {
        getActiveStates().remove(state);
    }

}
