package com.game.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.game.tests.screens.TestCameraRoomShiftScreen1;
import com.game.tests.screens.TestDamageScreen;
import com.game.tests.screens.TestDeathScreen;
import com.game.tests.screens.TestTiledMapScreen1;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GameTestRunner extends Game {

    private final TestScreen testKey;
    private final Map<TestScreen, Screen> testScreens = new HashMap<>() {{
        put(TestScreen.TEST_CAMERA_ROOM_SHIFT, new TestCameraRoomShiftScreen1());
        put(TestScreen.TEST_DAMAGER, new TestDamageScreen());
        put(TestScreen.TEST_DEATH, new TestDeathScreen());
        put(TestScreen.TEST_TILED_MAP_1, new TestTiledMapScreen1());
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
        TEST_CAMERA_ROOM_SHIFT,
        TEST_DAMAGER,
        TEST_DEATH,
        TEST_TILED_MAP_1
    }

}
