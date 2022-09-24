package com.game.cull;

import com.game.Entity;
import com.game.System;
import com.game.events.Event;
import com.game.events.EventListener;
import com.game.events.EventType;

import java.util.*;

public class CullSystem extends System implements EventListener {

    private final Set<EventType> events = EnumSet.noneOf(EventType.class);
    private final List<EventType> eventsToAdd = new ArrayList<>();

    public CullSystem() {
        super(CullComponent.class);
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
        CullComponent cullComponent = entity.getComponent(CullComponent.class);
        if (events.stream().anyMatch(cullComponent::isCullEvent)) {
            entity.setDead(true);
        }
    }

    @Override
    protected void postProcess(float delta) {
        events.clear();
    }

}
