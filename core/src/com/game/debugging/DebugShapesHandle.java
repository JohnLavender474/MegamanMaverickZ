package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Shape2D;
import com.game.utils.interfaces.UpdatableConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class DebugShapesHandle {

    private final Shape2D shape;
    private final ShapeType shapeType;
    private final Supplier<Color> colorSupplier;
    private final UpdatableConsumer<Shape2D> updatableConsumer;

    public DebugShapesHandle(Shape2D shape, ShapeType shapeType, Supplier<Color> colorSupplier) {
        this(shape, shapeType, colorSupplier, (shape1, delta) -> {});
    }

    public Color getColor() {
        return colorSupplier.get();
    }

}
