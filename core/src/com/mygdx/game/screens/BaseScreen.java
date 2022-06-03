package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.ConstVals.TextureAssets;
import com.mygdx.game.GameContext2d;
import com.mygdx.game.utils.TimeTicker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The base class for all {@link Screen} implementations in this app.
 */
@Getter
@RequiredArgsConstructor
public abstract class BaseScreen implements Screen {

    protected final GameContext2d gameContext;

    private Sprite blackBoxSprite;
    private TimeTicker fadeTimer;
    private boolean fadingOut;
    private boolean fadingIn;
    private boolean paused;
    private Music music;

    @Override
    public void show() {
        fadeTimer = new TimeTicker();
        blackBoxSprite = new Sprite();
        TextureRegion blackBoxRegion = gameContext.getAsset(
                TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureRegion.class);
        blackBoxSprite.setRegion(blackBoxRegion);
    }

    @Override
    public void hide() {
        pause();
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void dispose() {
        if (music != null) {
            music.stop();
        }
    }

    /**
     * Sets music.
     *
     * @param key the key
     */
    public void setMusic(String key, boolean loop) {
        if (music != null) {
            music.stop();
        }
        music = gameContext.getAsset(key, Music.class);
        music.play();
        music.setLooping(loop);
    }

    /**
     * Fade in.
     *
     * @param delta the delta time
     */
    public void fadeIn(float delta) {
        if (!fadingIn) {
            initFadeIn();
        }
        fadingIn = true;
        fadingOut = false;
        fadeTimer.update(delta);
        blackBoxSprite.setAlpha(Math.max(0f, 1f - fadeTimer.getRatio()));
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        boolean spriteBatchDrawing = spriteBatch.isDrawing();
        if (!spriteBatchDrawing) {
            spriteBatch.begin();
        }
        blackBoxSprite.draw(spriteBatch);
        if (!spriteBatchDrawing) {
            spriteBatch.end();
        }
        fadingIn = !fadeTimer.isFinished();
    }

    /**
     * Fade out.
     *
     * @param delta the delta time
     */
    public void fadeOut(float delta) {
        if (!fadingOut) {
            initFadeOut();
        }
        fadingOut = true;
        fadingIn = false;
        fadeTimer.update(delta);
        blackBoxSprite.setAlpha(Math.min(1f, fadeTimer.getRatio()));
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        boolean spriteBatchDrawing = spriteBatch.isDrawing();
        if (!spriteBatchDrawing) {
            spriteBatch.begin();
        }
        blackBoxSprite.draw(spriteBatch);
        if (!spriteBatchDrawing) {
            spriteBatch.end();
        }
        fadingOut = !fadeTimer.isFinished();
    }


    private void initFadeIn()
            throws IllegalStateException {
        if (fadeTimer == null) {
            throw new IllegalStateException("To be able to call BaseScreen#fadeIn(), " +
                                                    "time ticker must not be null!");
        }
        if (blackBoxSprite == null) {
            throw new IllegalStateException("To be able to call BaseScreen#fadeIn(), " +
                                                    "black box sprite must not be null!");
        }
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeTimer.reset();
    }

    private void initFadeOut()
            throws IllegalStateException {
        if (fadeTimer == null) {
            throw new IllegalStateException("To be able to call BaseScreen#fadeOut(), " +
                                                    "time ticker must not be null!");
        }
        if (blackBoxSprite == null) {
            throw new IllegalStateException("To be able to call BaseScreen#fadeOut(), " +
                                                    "black box sprite must not be null!");
        }
        blackBoxSprite.setBounds(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackBoxSprite.setAlpha(0f);
        fadeTimer.reset();
    }

}
