package com.game.screens.other;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.animations.TimedAnimation;
import com.game.backgrounds.Stars;
import com.game.core.GameContext2d;
import com.game.core.MegaTextHandle;
import com.game.core.constants.Boss;
import com.game.core.constants.GameScreen;
import com.game.core.constants.SoundAsset;
import com.game.dialogue.DialogueAnimQ;
import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Timer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.game.core.constants.MusicAsset.MM2_BOSS_INTRO;
import static com.game.core.constants.RenderingGround.UI;
import static com.game.core.constants.TextureAsset.STAGE_SELECT;
import static com.game.core.constants.ViewVals.*;
import static com.game.utils.UtilMethods.drawFiltered;
import static java.lang.Math.round;

public class LevelIntroScreen extends ScreenAdapter {

    public static final float DURATION = 7f;
    public static final float BOSS_DROP_DOWN = .25f;

    private static Map<Boss, Supplier<Queue<KeyValuePair<TimedAnimation, Timer>>>> bossIntroAnims = null;

    private final GameContext2d gameContext;

    private final Music music;
    private final TimedAnimation barAnimation;
    private final List<Stars> stars = new ArrayList<>();
    private final List<Sprite> bars = new ArrayList<>();
    private final Timer durationTimer = new Timer(DURATION);
    private final Timer bossDropDownTimer = new Timer(BOSS_DROP_DOWN);

    private final MegaTextHandle bossLetters;
    private final Timer bossLettersDelay = new Timer(1f);
    private final Timer bossLettersTimer = new Timer(.2f);

    private boolean set;
    private GameScreen nextScreen;
    private Queue<Runnable> bossLettersAnimQ;
    private KeyValuePair<Sprite, Queue<KeyValuePair<TimedAnimation, Timer>>> bossAnimDef;

    public LevelIntroScreen(GameContext2d gameContext) {
        this.gameContext = gameContext;
        music = gameContext.getAsset(MM2_BOSS_INTRO.getSrc(), Music.class);
        for (int i = 0; i < 4; i++) {
            stars.add(new Stars(gameContext, 0f, i * PPM * VIEW_HEIGHT / 4f, i + 1));
        }
        TextureRegion barRegion = gameContext.getAsset(STAGE_SELECT.getSrc(), TextureAtlas.class)
                .findRegion("Bar");
        barAnimation = new TimedAnimation(barRegion, new float[]{.3f, .15f, .15f, .15f});
        for (int i = 0; i < 4; i++) {
            Sprite bar = new Sprite();
            bar.setBounds((i * VIEW_WIDTH * PPM / 3f) - 5f, VIEW_HEIGHT * PPM / 3f,
                    (VIEW_WIDTH * PPM / 3f) + 5f, VIEW_HEIGHT * PPM / 3f);
            bars.add(bar);
        }
        bossLetters = new MegaTextHandle(round(PPM / 2f),
                new Vector2((VIEW_WIDTH * PPM / 3f) - PPM, VIEW_HEIGHT * PPM / 3f));
        if (bossIntroAnims == null) {
            bossIntroAnims = new EnumMap<>(Boss.class);
            for (Boss boss : Boss.values()) {
                bossIntroAnims.put(boss, () -> boss.getIntroAnimsQ(
                                gameContext.getAsset(boss.getTextureAtlas(), TextureAtlas.class))
                        .stream().map(i -> KeyValuePair.of(new TimedAnimation(i.key()), new Timer(i.value())))
                        .collect(Collectors.toCollection(ArrayDeque::new)));
            }
        }
    }

    public void set(Boss boss) {
        set = true;
        nextScreen = boss.getGameScreen();
        Sprite sprite = new Sprite();
        Vector2 size = boss.getSize();
        sprite.setSize(size.x * PPM, size.y * PPM);
        bossAnimDef = new KeyValuePair<>(sprite, bossIntroAnims.get(boss).get());
        Sound thump = gameContext.getAsset(SoundAsset.THUMP_SOUND.getSrc(), Sound.class);
        bossLettersAnimQ = DialogueAnimQ.getDialogueAnimQ(gameContext, bossLetters, boss.getBossName(), thump);
    }

    @Override
    public void show() {
        if (!set) {
            throw new IllegalStateException("LevelIntroScreen.java requires method 'setVertices' to be called before " +
                    "the screen can be shown");
        }
        durationTimer.reset();
        bossLettersTimer.reset();
        bossLettersDelay.reset();
        bossDropDownTimer.reset();
        stars.forEach(Stars::resetPositions);
        bossAnimDef.key().setPosition((VIEW_WIDTH * PPM / 2f) - 2f * PPM, VIEW_HEIGHT * PPM);
        bossAnimDef.value().forEach(i -> {
            i.key().reset();
            i.value().reset();
        });
        music.play();
    }

    @Override
    public void render(float delta) {
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        // stars
        stars.forEach(s -> {
            s.update(delta);
            s.draw(spriteBatch);
        });
        // bars
        barAnimation.update(delta);
        TextureRegion barRegion = barAnimation.getCurrentT();
        bars.forEach(b -> {
            b.setRegion(barRegion);
            drawFiltered(b, spriteBatch);
        });
        // Sprite falls if dropdown timer is not finished, setVertices to final pos if timer is just finished
        Sprite bossSprite = bossAnimDef.key();
        bossDropDownTimer.update(delta);
        if (!bossDropDownTimer.isFinished()) {
            float y = (VIEW_HEIGHT * PPM) -
                    (((VIEW_HEIGHT * PPM / 2f) + .85f * PPM) * bossDropDownTimer.getRatio());
            bossSprite.setPosition(bossSprite.getX(), y);
        }
        if (bossDropDownTimer.isJustFinished()) {
            bossSprite.setPosition((VIEW_WIDTH * PPM / 2f) - 2f * PPM, (VIEW_HEIGHT * PPM / 2f) - .85f * PPM);
        }
        // boss letters
        bossLettersDelay.update(delta);
        if (bossLettersDelay.isFinished() && bossDropDownTimer.isFinished() && !bossLettersAnimQ.isEmpty()) {
            bossLettersTimer.update(delta);
            if (bossLettersTimer.isFinished()) {
                bossLettersAnimQ.poll().run();
                bossLettersTimer.reset();
            }
        }
        bossLetters.draw(spriteBatch);
        // Handle anim q, if timer is finished then poll, update timer and timed anim, setVertices and draw sprite
        Queue<KeyValuePair<TimedAnimation, Timer>> bossAnimQ = bossAnimDef.value();
        if (bossAnimQ.size() > 1 && bossAnimQ.peek().value().isFinished()) {
            bossAnimQ.poll();
        }
        TimedAnimation animation = bossAnimQ.peek().key();
        Timer timer = bossAnimQ.peek().value();
        animation.update(delta);
        timer.update(delta);
        bossSprite.setRegion(animation.getCurrentT());
        drawFiltered(bossSprite, spriteBatch);
        spriteBatch.end();
        // update duration timer
        durationTimer.update(delta);
        if (durationTimer.isFinished()) {
            gameContext.setScreen(nextScreen);
        }
    }

    @Override
    public void dispose() {
        if (music != null) {
            music.stop();
        }
        nextScreen = null;
        bossAnimDef = null;
        set = false;
    }

}
