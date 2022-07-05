package com.game.entities.blocks;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the block type.
 * <p>
 * DEATH_BLOCK: Any entity that overlaps this block dies
 * STATIC_BLOCK: Is immovable
 * MOVING_BLOCK: Moves based on trajectory defined in map object properties
 * FALLING_BLOCK: Falls when stepped on by Megaman
 * EVENT_ACTUATOR: Activates event, event is queried by String
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BlockType {

    DEATH_BLOCK("DeathBlocks"), STATIC_BLOCK("StaticBlocks"), MOVING_BLOCK("MovingBlocks"), FALLING_BLOCK(
            "FallingBlocks"), EVENT_ACTIVATOR("EventActivators");

    private final String blockTypeLayerName;

}
