package com.game.behaviors;

import com.game.Component;
import com.game.utils.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * {@link Component} implementation for handling behaviors.
 */
@Getter
@Setter
public class BehaviorComponent implements Component {

    private Updatable preProcess;
    private Updatable postProcess;
    private final List<Behavior> behaviors = new ArrayList<>();
    private final Set<BehaviorType> activeBehaviors = EnumSet.noneOf(BehaviorType.class);
    private final Set<BehaviorType> allowedBehaviors = EnumSet.noneOf(BehaviorType.class);

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

    public boolean can(BehaviorType behaviorType) {
        return allowedBehaviors.contains(behaviorType);
    }

    public void setCan(BehaviorType behaviorType) {
        allowedBehaviors.add(behaviorType);
    }

    public void setCannot(BehaviorType behaviorType) {
        allowedBehaviors.remove(behaviorType);
    }

}
