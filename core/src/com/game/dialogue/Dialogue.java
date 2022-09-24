package com.game.dialogue;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.core.MegaTextHandle;
import com.game.constants.SoundAsset;
import com.game.constants.TextureAsset;
import com.game.utils.interfaces.Drawable;
import com.game.utils.interfaces.Updatable;
import com.game.utils.objects.Timer;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static com.game.constants.ViewVals.PPM;
import static com.game.utils.UtilMethods.drawFiltered;
import static java.lang.Math.round;

/**
 * Process:
 *    1. Black box is expanded until black box timer is finished
 *    2. For each block pairOf dialogue, letters are "typed" out one at a time with sound playing for non-blank chars,
 *       letter anim timer is used for delay between letters
 *    3. When each block pairOf dialogue is finished (except for the last), then pressing any button triggers the
 *       next block
 *    4. When the last block pairOf dialogue is reached, then pressing any button causes the black box to shrink
 *    5. When the black box has fully collapsed, then the event is finished
 */
public class Dialogue implements Updatable, Drawable {

    private final MegaTextHandle text;
    private final Supplier<Boolean> speedUp;
    private final Supplier<Boolean> finisher;
    private final Sprite blackBox = new Sprite();
    private final Timer dialogueTypingTimer = new Timer(.1f);
    private final Queue<Queue<Runnable>> dialogueQ = new LinkedList<>();

    @Getter
    private boolean finished;

    public Dialogue(GameContext2d gameContext, Collection<String> lines,
                    Supplier<Boolean> finisher, Supplier<Boolean> speedUp) {
        this.finisher = finisher;
        this.speedUp = speedUp;
        // text
        text = new MegaTextHandle(round(PPM / 2f), new Vector2());
        // black box
        TextureRegion blackRegion = gameContext.getAsset(TextureAsset.DECORATIONS.getSrc(), TextureAtlas.class)
                .findRegion("Black");
        blackBox.setRegion(blackRegion);
        blackBox.setBounds(0f, 0f, 0f, 0f);
        // sound
        Sound typingSound = gameContext.getAsset(SoundAsset.THUMP_SOUND.getSrc(), Sound.class);
        // lines anim queue
        lines.forEach(line -> dialogueQ.add(DialogueAnimQ.getDialogueAnimQ(gameContext, text, line, typingSound)));
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        drawFiltered(blackBox, spriteBatch);
        text.draw(spriteBatch);
    }

    @Override
    public void update(float delta) {
        // if dialogue q is empty and the finished returns true at least once, then this sequence is finished
        if (dialogueQ.isEmpty()) {
            if (finisher.get()) {
                finished = true;
            }
            return;
        }
        // if current line q is finished, then remove it
        if (dialogueQ.peek().isEmpty()) {
            if (finisher.get()) {
                dialogueQ.poll();
            }
            return;
        }
        // if timer is finished, then run next anim runnable
        if (speedUp.get()) {
            dialogueTypingTimer.update(delta * 2f);
        } else {
            dialogueTypingTimer.update(delta);
        }
        if (dialogueTypingTimer.isFinished()) {
            dialogueQ.peek().poll().run();
            dialogueTypingTimer.reset();
        }
    }

}
