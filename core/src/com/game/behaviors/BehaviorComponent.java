package com.game.behaviors;

import com.game.Component;
import com.game.utils.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * {@link Component} implementation for handling behaviors. Set of active behaviors is initialized as empty set.
 */
@Getter
@Setter
public class BehaviorComponent implements Component {

    private Updatable preProcess;
    private Updatable postProcess;
    private final List<Behavior> behaviors = new ArrayList<>();
    private final Set<BehaviorType> activeBehaviors = EnumSet.noneOf(BehaviorType.class);

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    public boolean is(BehaviorType behaviorType) {
        return activeBehaviors.contains(behaviorType);
    }

    public void setIs(BehaviorType behaviorType) {
        activeBehaviors.add(behaviorType);
    }

    public void setIsNot(BehaviorType behaviorType) {
        activeBehaviors.remove(behaviorType);
    }

}
