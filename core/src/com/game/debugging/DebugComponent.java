package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import com.game.utils.KeyValuePair;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter
@NoArgsConstructor
public class DebugComponent implements Component {

    private final List<KeyValuePair<Supplier<Rectangle>, Supplier<Color>>> debugHandles = new ArrayList<>();

    public DebugComponent(KeyValuePair<Supplier<Rectangle>, Supplier<Color>>... debugHandles) {
        this(Arrays.asList(debugHandles));
    }

    public DebugComponent(List<KeyValuePair<Supplier<Rectangle>, Supplier<Color>>> debugHandles) {
        this.debugHandles.addAll(debugHandles);
    }

    public void addDebugHandle(Supplier<Rectangle> rectSupplier, Supplier<Color> colorSupplier) {
        debugHandles.add(new KeyValuePair<>(rectSupplier, colorSupplier));
    }

}
