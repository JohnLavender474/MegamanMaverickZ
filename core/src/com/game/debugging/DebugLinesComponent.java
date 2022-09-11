package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Component;
import com.game.utils.objects.KeyValuePair;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*;

@Setter
@NoArgsConstructor
@Getter(AccessLevel.PACKAGE)
public class DebugLinesComponent extends Component {

    private final List<KeyValuePair<Supplier<List<Vector2>>, Supplier<Color>>> debugLines = new ArrayList<>();
    private ShapeType shapeType = Line;
    private float thickness = 1f;

    public DebugLinesComponent(Vector2 v1, Vector2 v2, Supplier<Color> colorSupplier,
                               float thickness, ShapeType shapeType) {
        this(List.of(v1, v2), colorSupplier, thickness, shapeType);
    }

    public DebugLinesComponent(List<Vector2> lines, Supplier<Color> colorSupplier,
                               float thickness, ShapeType shapeType) {
        this(() -> lines, colorSupplier, thickness, shapeType);
    }

    public DebugLinesComponent(Supplier<List<Vector2>> linesSupplier, Supplier<Color> colorSupplier,
                               float thickness, ShapeType shapeType) {
        addDebugLine(linesSupplier, colorSupplier);
        this.thickness = thickness;
        this.shapeType = shapeType;
    }

    public void addDebugLine(Supplier<List<Vector2>> linesSupplier, Supplier<Color> colorSupplier) {
        debugLines.add(new KeyValuePair<>(linesSupplier, colorSupplier));
    }

}
