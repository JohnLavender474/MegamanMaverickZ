package com.game.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.game.core.DebugLogger.DebugLevel.*;
import static com.game.core.DebugLogger.DebugLevel.NONE;
import static com.game.utils.UtilMethods.*;
import static lombok.AccessLevel.*;

@NoArgsConstructor(access = PRIVATE)
public class DebugLogger {

    private static DebugLogger debugLogger = null;

    public enum DebugLevel {
        DEBUG,
        INFO,
        NONE
    }

    @Getter
    @Setter
    private DebugLevel debugLevel = NONE;

    public static DebugLogger getInstance() {
        if (debugLogger == null) {
            debugLogger = new DebugLogger();
        }
        return debugLogger;
    }

    public void info(String str) {
        if (equalsAny(debugLevel, INFO, DEBUG)) {
            java.lang.System.out.println(str);
        }
    }

    public void debug(String str) {
        if (debugLevel.equals(DEBUG)) {
            java.lang.System.out.println(str);
        }
    }

}
