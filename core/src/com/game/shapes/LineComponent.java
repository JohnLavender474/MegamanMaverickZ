package com.game.shapes;

import com.game.Component;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** Component for rendering lines. */
@Setter
@Getter
public class LineComponent extends Component {

    private final List<LineHandle> lineHandles = new ArrayList<>();

    /**
     * Add var args line handles.
     *
     * @param lineHandles the line handles
     */
    public LineComponent(LineHandle... lineHandles) {
        this(Arrays.asList(lineHandles));
    }

    /**
     * Add collection of line handles.
     *
     * @param lineHandles the line handles
     */
    public LineComponent(Collection<LineHandle> lineHandles) {
        lineHandles.forEach(this::addLineHandle);
    }

    /**
     * Add a line handle.
     *
     * @param lineHandle the line handle
     */
    public void addLineHandle(LineHandle lineHandle) {
        lineHandles.add(lineHandle);
    }

}
