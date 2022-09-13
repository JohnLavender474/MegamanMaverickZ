package com.game.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.game.utils.objects.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Color.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;
import static com.badlogic.gdx.math.Vector2.*;
import static com.game.utils.objects.Pair.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LineHandle {

    private Supplier<Pair<Vector2>> lineSupplier = () -> pairOf(X, Y);
    private Supplier<ShapeType> shapeTypeSupplier = () -> Line;
    private Supplier<Boolean> doRenderSupplier = () -> true;
    private Supplier<Float> thicknessSupplier = () -> 1f;
    private Supplier<Color> colorSupplier = () -> BLACK;

    public Pair<Vector2> getLine() {
        return lineSupplier.get();
    }

    public ShapeType getShapeType() {
        return shapeTypeSupplier.get();
    }

    public Color getColor() {
        return colorSupplier.get();
    }

    public float getThickness() {
        return thicknessSupplier.get();
    }

    public boolean doRender() {
        return doRenderSupplier.get();
    }

}
