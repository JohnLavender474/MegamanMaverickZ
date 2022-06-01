package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.utils.Updatable;
import lombok.RequiredArgsConstructor;

/**
 * Base handler for game camera.
 */
@RequiredArgsConstructor
public abstract class GameCameraManager implements Updatable {
    protected final OrthographicCamera camera;
}
