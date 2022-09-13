package com.game.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Shape2D;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

/** handle object containing the information pertinent to rendering a shape. */
@Getter
@Setter
public class ShapeHandle implements Comparable<ShapeHandle> {

    private Updatable updatable = null;
    private Supplier<Color> colorSupplier = () -> BLACK;
    private Supplier<Integer> prioritySupplier = () -> 0;
    private Supplier<Shape2D> shapeSupplier = () -> null;
    private Supplier<Boolean> doRenderSupplier = () -> true;
    private Supplier<ShapeType> shapeTypeSupplier = () -> Line;

    /**
     * Copies all fields from the provided shape handle.
     *
     * @param shapeHandle the shape handle to copy
     */
    public void copyOf(ShapeHandle shapeHandle) {
        setUpdatable(shapeHandle.getUpdatable());
        setColorSupplier(shapeHandle.getColorSupplier());
        setShapeSupplier(shapeHandle.getShapeSupplier());
        setDoRenderSupplier(shapeHandle.getDoRenderSupplier());
        setShapeTypeSupplier(shapeHandle.getShapeTypeSupplier());
    }

    /**
     * Get the color.
     *
     * @return the color
     */
    public Color getColor() {
        return colorSupplier.get();
    }

    /**
     * Get the shape.
     *
     * @return the shape
     */
    public Shape2D getShape() {
        return shapeSupplier.get();
    }

    /**
     * If the shape should be rendered.
     *
     * @return if the shape should be rendered.
     */
    public boolean doRender() {
        return doRenderSupplier.get();
    }

    /**
     * Get the shape type.
     *
     * @return the shape type
     */
    public ShapeType getShapeType() {
        return shapeTypeSupplier.get();
    }

    /**
     * Get the priority of this shape handle. Higher value means higher priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return prioritySupplier.get();
    }

    @Override
    public int compareTo(ShapeHandle o) {
        return Integer.compare(getPriority(), o.getPriority());
    }

}
