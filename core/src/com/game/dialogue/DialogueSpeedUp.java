package com.game.dialogue;

import com.game.GameContext2d;

import java.util.function.Supplier;

public class DialogueSpeedUp {

    public static Supplier<Boolean> speedUpByAnyButtonPressed(GameContext2d gameContext) {
        return gameContext::isAnyControllerButtonPressed;
    }

}
