package com.game.behaviors;

import com.game.Component;
import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

/**
 * {@link Component} implementation for handling behaviors.
 */
@Getter
public class BehaviorComponent implements Component {

    private final List<Behavior> behaviors = new ArrayList<>();
    private final Set<BehaviorType> activeBehaviors = EnumSet.noneOf(BehaviorType.class);

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
