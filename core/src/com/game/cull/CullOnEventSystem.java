package com.game.cull;

import com.game.Entity;
import com.game.GameContext2d;
import com.game.System;
import com.game.events.Event;
import com.game.events.EventListener;
import com.game.events.EventType;

import java.util.*;

public class CullOnEventSystem extends System implements EventListener {

    private final Set<EventType> events = EnumSet.noneOf(EventType.class);
    private final List<EventType> eventsToAdd = new ArrayList<>();

    public CullOnEventSystem(GameContext2d gameContext) {
        super(CullOnEventComponent.class);
        gameContext.addEventListener(this);
    }

    @Override
    public void listenToEvent(Event event, float delta) {
        if (isUpdating()) {
            eventsToAdd.addAll(event.getEventTypes());
        } else {
            events.addAll(event.getEventTypes());
        }
    }

    @Override
    protected void preProcess(float delta) {
        events.addAll(eventsToAdd);
        eventsToAdd.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        CullOnEventComponent cullOnEventComponent = entity.getComponent(CullOnEventComponent.class);
        if (events.stream().anyMatch(cullOnEventComponent::isCullEvent)) {
            entity.setDead(true);
        }
    }

    @Override
    protected void postProcess(float delta) {
        events.clear();
    }

}
