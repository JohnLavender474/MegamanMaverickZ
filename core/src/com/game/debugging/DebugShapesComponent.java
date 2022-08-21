package com.game.debugging;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Component;
import lombok.Getter;

import java.util.*;

import static java.util.Arrays.*;

/**
 * {@link Component} implementation for handling debugging of {@link Rectangle} instances.
 */
@Getter
public class DebugShapesComponent extends Component {

    private final List<DebugShapesHandle> debugShapesHandles;

    public DebugShapesComponent(DebugShapesHandle... debugShapesHandles) {
        this(new ArrayList<>() {{
            this.addAll(asList(debugShapesHandles));
        }});
    }

    public DebugShapesComponent(List<DebugShapesHandle> debugShapesHandles) {
        this.debugShapesHandles = debugShapesHandles;
    }

}
