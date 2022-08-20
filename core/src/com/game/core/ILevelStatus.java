package com.game.core;

import static com.game.core.ConstVals.*;

public interface ILevelStatus {

    LevelStatus getLevelStatus();

    default boolean isLevelStatus(LevelStatus levelStatus) {
        return getLevelStatus() == levelStatus;
    }

    void setLevelStatus(LevelStatus levelStatus);

}
