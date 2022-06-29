package com.game.behaviors;

import com.game.Component;
import com.game.updatables.Updatable;
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

    /**
     * Add behavior.
     *
     * @param behavior the behavior
     */
    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    /**
     * Is boolean.
     *
     * @param behaviorType the behavior type
     * @return the boolean
     */
    public boolean is(BehaviorType behaviorType) {
        return activeBehaviors.contains(behaviorType);
    }

    /**
     * Set.
     *
     * @param behaviorType the behavior type
     * @param isBehavior   the is behavior
     */
    public void set(BehaviorType behaviorType, boolean isBehavior) {
        if (isBehavior) {
            setIs(behaviorType);
        } else {
            setIsNot(behaviorType);
        }
    }

    /**
     * Set is.
     *
     * @param behaviorType the behavior type
     */
    public void setIs(BehaviorType behaviorType) {
        activeBehaviors.add(behaviorType);
    }

    /**
     * Set is not.
     *
     * @param behaviorType the behavior type
     */
    public void setIsNot(BehaviorType behaviorType) {
        activeBehaviors.remove(behaviorType);
    }

}
