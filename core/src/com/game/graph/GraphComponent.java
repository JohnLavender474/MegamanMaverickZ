package com.game.graph;

import com.badlogic.gdx.math.Rectangle;
import com.game.core.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GraphComponent extends Component {

    private final Map<Supplier<Rectangle>, Supplier<Collection<Object>>> suppliers = new HashMap<>();

    public GraphComponent(Supplier<Rectangle> boundsSupplier, Supplier<Collection<Object>> objsSupplier) {
        addSupplier(boundsSupplier, objsSupplier);
    }

    public void addSupplier(Supplier<Rectangle> boundsSupplier, Supplier<Collection<Object>> objsSupplier) {
        suppliers.put(boundsSupplier, objsSupplier);
    }

}
