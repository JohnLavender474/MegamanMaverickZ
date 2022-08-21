package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.game.core.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

@Setter
@Getter(AccessLevel.PACKAGE)
public class DebugLinesComponent extends Component {

    private Map<List<Vector2>, Supplier<Color>> debugLinesSupplierMap = new HashMap<>();
    private ShapeType shapeType;
    private float thickness;

    public DebugLinesComponent(Vector2 v1, Vector2 v2, Supplier<Color> colorSupplier,
                               float thickness, ShapeType shapeType) {
        this(List.of(v1, v2), colorSupplier, thickness, shapeType);
    }

    public DebugLinesComponent(List<Vector2> lines, Supplier<Color> colorSupplier, float thickness,
                               ShapeType shapeType) {
        this.debugLinesSupplierMap.put(lines, colorSupplier);
        this.thickness = thickness;
        this.shapeType = shapeType;
    }

}
