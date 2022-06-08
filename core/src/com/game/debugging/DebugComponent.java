package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.utils.KeyValuePair;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * {@link com.game.Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter(AccessLevel.PACKAGE)
public class DebugComponent implements Component {

    private final Queue<KeyValuePair<Rectangle, Color>> debugQueue = new ArrayDeque<>();

    /**
     * Debug the {@link Rectangle} with the provided {@link Color}.
     *
     * @param rectangle the rectangle
     * @param color     the color
     */
    public void debug(Rectangle rectangle, Color color) {
        debugQueue.add(new KeyValuePair<>(rectangle, color));
    }

}
