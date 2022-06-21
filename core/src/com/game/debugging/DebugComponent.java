package com.game.debugging;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Component;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter
public class DebugComponent implements Component {

    private final List<DebugHandle> debugHandles = new ArrayList<>();

    public void addDebugHandle(DebugHandle debugHandle) {
        debugHandles.add(debugHandle);
    }

}
