package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Shape2D;
import com.game.utils.interfaces.UpdatableConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
public class DebugShapesHandle {

    private final ShapeType shapeType;
    private final Supplier<Shape2D> shape;
    private final Supplier<Color> colorSupplier;
    private final UpdatableConsumer<Shape2D> updatableConsumer;

    public DebugShapesHandle(Supplier<Shape2D> shape, ShapeType shapeType, Supplier<Color> colorSupplier) {
        this(shape, shapeType, colorSupplier, (shape1, delta) -> {});
    }

    public DebugShapesHandle(Supplier<Shape2D> shape, ShapeType shapeType, Supplier<Color> colorSupplier,
                             UpdatableConsumer<Shape2D> updatableConsumer) {
        this.shape = shape;
        this.shapeType = shapeType;
        this.colorSupplier = colorSupplier;
        this.updatableConsumer = updatableConsumer;
    }

    public Color getColor() {
        return colorSupplier.get();
    }

}
