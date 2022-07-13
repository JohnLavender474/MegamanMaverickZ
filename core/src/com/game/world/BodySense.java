package com.game.world;

/**
 * Each value signifies a contact between two fixtures each with specific values for {@link Fixture#getFixtureType()}.
 * For example, if fixture 1 has type {@link FixtureType#HEAD} and fixture 2 has type {@link FixtureType#BLOCK} and
 * both fixtures overlap, then the body component owning fixture 1 will have {@link #HEAD_TOUCHING_BLOCK} added to its
 * body senses set and fixture 2 will have {@link #HIT_BY_HEAD} added to its body senses set.
 */
public enum BodySense {
    HIT_BY_HEAD, FEET_ON_GROUND, HEAD_TOUCHING_BLOCK, TOUCHING_BLOCK_LEFT, TOUCHING_BLOCK_RIGHT,
    TOUCHING_WALL_SLIDE_LEFT, TOUCHING_WALL_SLIDE_RIGHT, TOUCHING_HITBOX_LEFT, TOUCHING_HITBOX_RIGHT
}
