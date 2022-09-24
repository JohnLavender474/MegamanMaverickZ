package com.game.core;

import com.game.constants.LevelStatus;

public interface ILevelStatus {

    LevelStatus getLevelStatus();

    default boolean isLevelStatus(LevelStatus levelStatus) {
        return getLevelStatus() == levelStatus;
    }

    void setLevelStatus(LevelStatus levelStatus);

}
