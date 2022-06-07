package com.game.acting;

import com.game.Component;
import com.game.System;
import com.game.entities.Entity;

import java.util.Set;
import java.util.function.Supplier;

/**
 * {@link System} implementation for handling {@link ActorComponent}. Each {@link ActorAction} is processed in
 * insertion order defined in {@link ActorComponent#getActions()} and {@link ActorAction#action()} is updated if
 * and only if {@link Actor#is(ActorState)} returns true provided the value of {@link ActorAction#state()} and
 * {@link Actor#is(ActorState)} returns false for each value contained in {@link ActorAction#overrides()}.
 */
public class ActorSystem extends System {

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(ActorComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ActorComponent actorComponent = entity.getComponent(ActorComponent.class);
        actorComponent.getActions().stream().filter(action -> actorComponent.is(action.state()) &&
                              action.overrides().stream().noneMatch(Supplier::get))
                      .map(ActorAction::action).forEach(action -> action.update(delta));
        actorComponent.clearStates();
    }

}
