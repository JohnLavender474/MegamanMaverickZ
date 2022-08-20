package com.game.debugging;

import com.game.core.Component;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Getter(AccessLevel.PACKAGE)
public class DebugMessageComponent extends Component {

    private final Map<Integer, String> debugMessages = new HashMap<>();
    private final Queue<Integer> clearQueue = new LinkedList<>();

    public void debugMessage(int index, String debugMessage) {
        debugMessages.put(index, debugMessage);
    }

    public void clear(int index) {
        clearQueue.add(index);
    }

}
