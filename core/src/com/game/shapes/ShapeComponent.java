package com.game.shapes;

import com.badlogic.gdx.math.Shape2D;
import com.game.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

/** Component for rendering shapes. */
@Getter
@NoArgsConstructor
public class ShapeComponent extends Component {

    private final List<ShapeHandle> shapeHandles = new ArrayList<>();

    /**
     * Add var args shape handles.
     *
     * @param shapeHandles the shape handles
     */
    public ShapeComponent(ShapeHandle... shapeHandles) {
        this(asList(shapeHandles));
    }

    /**
     * Add simple var arg shape handles with only shape field set.
     *
     * @param shapes the shapes
     */
    public ShapeComponent(Shape2D... shapes) {
        this(stream(shapes).map(ShapeHandle::new).collect(toList()));
    }

    /**
     * Add collection of shape handles.
     *
     * @param shapeHandles the shape handles
     */
    public ShapeComponent(Collection<ShapeHandle> shapeHandles) {
        shapeHandles.forEach(this::addShapeHandle);
    }

    /**
     * Add a shape handle.
     *
     * @param shapeHandle the shape handle
     */
    public void addShapeHandle(ShapeHandle shapeHandle) {
        shapeHandles.add(shapeHandle);
    }

    /** Clears shape handles. */
    public void clearShapeHandles() {
        shapeHandles.clear();
    }

}
