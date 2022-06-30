package com.game.world;

/**
 * Defines the body type of {@link BodyComponent}.
 * <p>
 * STATIC: Are immovable, cannot be passed through by dynamic bodies.
 * DYNAMIC: Cannot pass through static bodies.
 * ABSTRACT: Can pass through static and dynamic bodies.
 */
public enum BodyType {
    STATIC,
    DYNAMIC,
    ABSTRACT
}
