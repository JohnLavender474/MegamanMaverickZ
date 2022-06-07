package com.game.acting;

import com.game.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Defines an {@link com.game.entities.Entity} that can perform any of the actions enumerated in {@link ActorAction}.
 * It is not required for the entity to implement all actor action values.
 */
@Getter
@RequiredArgsConstructor
public class ActorComponent implements Component, Actor {
    private final Actor actor;
    private final List<ActorAction> actions = new ArrayList<>();
    private final Set<ActorState> activeStates = EnumSet.noneOf(ActorState.class);
}
