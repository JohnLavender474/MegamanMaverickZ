package com.game.trajectories;

import com.badlogic.gdx.math.Vector2;
import com.game.Component;
import com.game.updatables.Updatable;
import com.game.utils.Initializable;
import com.game.utils.KeyValuePair;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import com.game.world.BodyComponent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * {@link Component} implementation for trajectories. This component sets the position of the {@link BodyComponent}, overriding any
 * settings for friction, velocity, etc.
 */
public class TrajectoryComponent implements Component {

    private final List<KeyValuePair<Vector2, Timer>> trajectories = new ArrayList<>();
    private int index = 0;

    public void addTrajectory(Vector2 trajectory, float duration) {
        trajectories.add(new KeyValuePair<>(trajectory, new Timer(duration)));
    }

    public Vector2 getCurrentTrajectory() {
        return trajectories.get(index).key();
    }

    public Timer getCurrentTimer() {
        return trajectories.get(index).value();
    }

    public void setToNext() {
        index++;
        if (index >= trajectories.size()) {
            index = 0;
        }
    }

}
