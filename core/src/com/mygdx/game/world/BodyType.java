package com.mygdx.game.world;

/**
 * Defines the body type of {@link BodyComponent}.
 *
 * STATIC: Is not affected by gravity; dynamic and other static bodies cannot pass through static bodies.
 * DYNAMIC: Is affected by gravity and cannot pass through static bodies.
 * ABSTRACT: Is not affected by gravity and can pass through static bodies.
 * KINEMATIC: Is affected by gravity and can pass through static bodies.
 */
public enum BodyType {
    STATIC,
    DYNAMIC,
    ABSTRACT,
    KINEMATIC
}
