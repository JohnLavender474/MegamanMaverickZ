package com.mygdx.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.MegamanMaverick;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseScreen extends ScreenAdapter {

    private final MegamanMaverick megamanGameContext;
    protected Music music;

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        megamanGameContext.getViewports().values().forEach(
                viewport -> viewport.update(width, height));
    }

}
