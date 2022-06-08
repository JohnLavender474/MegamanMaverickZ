package com.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.ProcessState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.Map;

/**
 * Defines a fixture fixed to a body. Offset is from the center of the body.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Fixture {
    private final FixtureType fixtureType;
    private final Vector2 offset = new Vector2();
    private final Rectangle fixtureBox = new Rectangle();
    private final Map<ProcessState, ContactListener> contactListeners = new EnumMap<>(ProcessState.class);
}
