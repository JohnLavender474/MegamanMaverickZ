package com.game.debugging;

import com.badlogic.gdx.math.Rectangle;
import com.game.Component;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.game.Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter
public class DebugComponent implements Component {

    private final List<DebugHandle> debugHandles = new ArrayList<>();

    public void addDebugHandle(DebugHandle debugHandle) {
        debugHandles.add(debugHandle);
    }

}
