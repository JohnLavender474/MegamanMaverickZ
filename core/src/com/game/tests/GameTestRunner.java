package com.game.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.game.tests.screens.*;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GameTestRunner extends Game {

    private final TestScreen testKey;
    private final Map<TestScreen, Screen> testScreens = new HashMap<>() {{
        put(TestScreen.TEST_MOVING_PLATFORMS, new TestMovingPlatformsScreen());
        put(TestScreen.TEST_TRAJECTORIES, new TestTrajectoriesScreen());
    }};

    @Override
    public void create() {
        setScreen(testScreens.get(testKey));
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
    }

    public enum TestScreen {
        TEST_TRAJECTORIES,
        TEST_MOVING_PLATFORMS,
    }

}
