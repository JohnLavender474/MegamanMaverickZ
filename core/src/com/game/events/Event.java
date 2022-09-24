package com.game.events;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class Event {

    private final Set<EventType> eventTypes;
    private final Map<String, Object> details;

    public Event(EventType eventType, String key, Object object) {
        this(eventType, new HashMap<>() {{
            put(key, object);
        }});
    }

    public Event(EventType eventType, Map<String, Object> details) {
        this.eventTypes = EnumSet.of(eventType);
        this.details = details;
    }

    public Event(EventType... eventTypes) {
        this(eventTypes.length > 0 ? EnumSet.of(eventTypes[0], eventTypes) : EnumSet.noneOf(EventType.class));
    }

    public Event(Set<EventType> eventTypes) {
        this(eventTypes, new HashMap<>());
    }

    public Set<EventType> getEventTypes() {
        return Collections.unmodifiableSet(eventTypes);
    }

    public boolean is(EventType eventType) {
        return eventTypes.contains(eventType);
    }

    public void putDetails(String key, Object o) {
        details.put(key, o);
    }

    public <T> T getDetails(String key, Class<T> tClass) {
        return tClass.cast(getDetails(key));
    }

    public Object getDetails(String key) {
        return details.get(key);
    }

}
