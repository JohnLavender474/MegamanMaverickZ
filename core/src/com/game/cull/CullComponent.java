package com.game.cull;

import com.game.events.EventType;
import com.game.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.*;

@NoArgsConstructor
@Getter(AccessLevel.PACKAGE)
public class CullComponent extends Component {

    private final Set<EventType> eventTypes = new HashSet<>();

    public CullComponent(EventType... eventTypes) {
        this(asList(eventTypes));
    }

    public CullComponent(Collection<EventType> eventTypes) {
        eventTypes.forEach(e -> setCullOnEvent(e, true));
    }

    public boolean isCullEvent(EventType eventType) {
        return eventTypes.contains(eventType);
    }

    public void setCullOnEvent(EventType eventType, boolean cull) {
        if (cull) {
            eventTypes.add(eventType);
        } else {
            eventTypes.remove(eventType);
        }
    }

}
