package com.game.updatables;

import com.game.core.Component;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link Component} implementation for pre-processing.
 */
@Getter
@Setter
public class UpdatableComponent implements Component {
    private Updatable updatable;
}
