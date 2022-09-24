package com.game.dialogue;

import com.game.GameContext2d;

import java.util.function.Supplier;

public class DialogueFinishers {

    public static Supplier<Boolean> finishByAnyButtonJustPressed(GameContext2d gameContext) {
        return gameContext::isAnyControllerButtonJustPressed;
    }

}
