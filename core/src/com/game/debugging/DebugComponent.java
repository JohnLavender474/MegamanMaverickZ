package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.utils.KeyValuePair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter
public class DebugComponent implements Component {

    private final List<KeyValuePair<Supplier<Rectangle>, Supplier<Color>>> debugHandles = new ArrayList<>();

    public void addDebugHandle(Supplier<Rectangle> rectSupplier, Supplier<Color> colorSupplier) {
        debugHandles.add(new KeyValuePair<>(rectSupplier, colorSupplier));
    }

}
