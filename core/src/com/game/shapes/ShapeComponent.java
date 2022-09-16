package com.game.shapes;

import com.badlogic.gdx.math.Shape2D;
import com.game.core.Component;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/** Component for rendering shapes. */
@Getter
public class ShapeComponent extends Component {

    private final List<ShapeHandle> shapeHandles = new ArrayList<>();

    /**
     * Add var args shape handles.
     *
     * @param shapeHandles the shape handles
     */
    public ShapeComponent(ShapeHandle... shapeHandles) {
        this(Arrays.asList(shapeHandles));
    }

    /**
     * Add simple var arg shape handles with only shape field set.
     *
     * @param shapes the shapes
     */
    public ShapeComponent(Shape2D... shapes) {
        this(Arrays.stream(shapes).map(ShapeHandle::new).collect(Collectors.toList()));
    }

    /**
     * Add collection of shape handles.
     *
     * @param shapeHandles the shape handles
     */
    public ShapeComponent(Collection<ShapeHandle> shapeHandles) {
        shapeHandles.forEach(this::addDebugShapeHandle);
    }

    /**
     * Add a shape handle.
     *
     * @param shapeHandle the shape handle
     */
    public void addDebugShapeHandle(ShapeHandle shapeHandle) {
        shapeHandles.add(shapeHandle);
    }

}
