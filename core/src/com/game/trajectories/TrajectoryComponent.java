package com.game.trajectories;

import com.game.core.Component;
import com.game.utils.Trajectory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Component} implementation for trajectories.
 */
@Getter
public class TrajectoryComponent implements Component {

    private final List<Trajectory> trajectories = new ArrayList<>();
    private int currentIndex = 0;

    public void addTrajectory(Trajectory trajectory) {
        trajectories.add(trajectory);
    }

    public Trajectory getCurrentTrajectory() {
        return trajectories.get(currentIndex);
    }

    public void setToNext() {
        currentIndex = currentIndex < trajectories.size() - 1 ? currentIndex + 1 : 0;
    }

}
