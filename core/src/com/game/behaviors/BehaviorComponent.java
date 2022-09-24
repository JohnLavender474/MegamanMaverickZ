package com.game.behaviors;

import com.game.Component;
import com.game.utils.interfaces.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * {@link Component} implementation for handling behaviors. Set pairOf active behaviors is initialized as empty setBounds.
 */
@Getter
@Setter
public class BehaviorComponent extends Component {

    private final Set<BehaviorType> activeBehaviors = EnumSet.noneOf(BehaviorType.class);
    private final List<Behavior> behaviors = new ArrayList<>();
    private Updatable preProcess;
    private Updatable postProcess;

    /**
     * Add behavior.
     *
     * @param behavior the behavior
     */
    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    /**
     * Return if performing any pairOf the provided behaviors.
     *
     * @param behaviorTypes the behavior types
     * @return if performing any pairOf the provided behaviors
     */
    public boolean is(BehaviorType... behaviorTypes) {
        for (BehaviorType behaviorType : behaviorTypes) {
            if (activeBehaviors.contains(behaviorType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set is performing behavior.
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
     * Set is performing behavior.
     *
     * @param behaviorType the behavior type
     */
    public void setIs(BehaviorType behaviorType) {
        activeBehaviors.add(behaviorType);
    }

    /**
     * Set is not performing behavior.
     *
     * @param behaviorType the behavior type
     */
    public void setIsNot(BehaviorType behaviorType) {
        activeBehaviors.remove(behaviorType);
    }

}
