package com.game.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.game.utils.DebugLogger.DebugLevel.*;
import static com.game.utils.DebugLogger.DebugLevel.NONE;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class DebugLogger {

    private static DebugLogger debugLogger = null;

    public enum DebugLevel {
        DEBUG,
        INFO,
        NONE
    }

    private final Map<Class<?>, DebugLevel> objDebugLevels = new HashMap<>();

    @Getter
    @Setter
    private DebugLevel globalDebugLevel = NONE;

    public static DebugLogger getInstance() {
        if (debugLogger == null) {
            debugLogger = new DebugLogger();
        }
        return debugLogger;
    }

    public void putDebugClass(Class<?> c, DebugLevel debugLevel) {
        objDebugLevels.put(c, debugLevel);
    }

    public void info(Class<?> c, String str) {
        if (globalDebugLevel.equals(INFO) && objDebugLevels.containsKey(c) && objDebugLevels.get(c).equals(INFO)) {
            java.lang.System.out.println(str);
        }
        debug(c, str);
    }

    public void info(String str) {
        if (globalDebugLevel.equals(INFO)) {
            java.lang.System.out.println(str);
        }
        debug(str);
    }

    public void debug(Class<?> c, String str) {
        if (globalDebugLevel.equals(DEBUG) && objDebugLevels.containsKey(c) && objDebugLevels.get(c).equals(DEBUG)) {
            java.lang.System.out.println(str);
        }
    }

    public void debug(String str) {
        if (globalDebugLevel.equals(DEBUG)) {
            java.lang.System.out.println(str);
        }
    }

}
