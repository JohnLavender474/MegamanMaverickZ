package com.game.updatables;

import com.game.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@link Component} implementation for pre-processing.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatableComponent implements Component {
    private Updatable updatable;
}
