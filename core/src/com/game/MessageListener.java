package com.game;

import java.util.Set;
import java.util.function.Function;

public interface MessageListener {

    Set<Function<Object, Boolean>> messagerMaskSet();

    void listenToMessage(Object owner, Object message, float delta);

    default boolean isListeningForMessageFrom(Object o) {
        return messagerMaskSet().stream().anyMatch(action -> action.apply(o));
    }

}
