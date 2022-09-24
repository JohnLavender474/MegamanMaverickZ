package com.game.dialogue;

import com.badlogic.gdx.audio.Sound;
import com.game.GameContext2d;
import com.game.text.MegaTextHandle;

import java.util.LinkedList;
import java.util.Queue;

public class DialogueAnimQ {

    public static Queue<Runnable> getDialogueAnimQ(GameContext2d gameContext, MegaTextHandle m, String s, Sound sound) {
        Queue<Runnable> q = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            final int finalI = i;
            q.add(() -> {
                m.setText(s.substring(0, finalI + 1));
                if (Character.isWhitespace(s.charAt(finalI))) {
                    return;
                }
                gameContext.playSound(sound);
            });
        }
        return q;
    }

}
