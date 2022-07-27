package com.game.debugging;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@NoArgsConstructor
public class DebugLinesComponent implements Component {

    Map<Supplier<List<Vector2>>, Supplier<Color>> debugLinesSupplierMap = new HashMap<>();

    public DebugLinesComponent(Supplier<List<Vector2>> linesSupplier, Supplier<Color> colorSupplier) {
        addLinesToDebug(linesSupplier, colorSupplier);
    }

    public void addLinesToDebug(Supplier<List<Vector2>> linesSupplier, Supplier<Color> colorSupplier) {
        debugLinesSupplierMap.put(linesSupplier, colorSupplier);
    }

}
