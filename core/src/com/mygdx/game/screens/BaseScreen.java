package com.mygdx.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.GameContext;

public abstract class BaseScreen extends ScreenAdapter {

    private final GameContext gameContext;
    protected Music music;

    public BaseScreen(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        gameContext.getViewports().values().forEach(
                viewport -> viewport.update(width, height));
    }

}
