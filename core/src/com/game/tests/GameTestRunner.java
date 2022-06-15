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

    public enum TestScreen {
        TEST_PPM_AND_MOVEMENT_SPEEDS,
        TEST_GROUND_DASH_AND_FRICTION,
        TEST_MEGAMAN_ANIMATIONS,
        TEST_CAMERA_ROOM_SHIFT,
        TEST_SPECIAL_MOVEMENTS,
        TEST_MEGAMAN_SHOOTING,
        TEST_WORLD_SYSTEM,
    }

    private final TestScreen testKey;

    private final Map<TestScreen, Screen> testScreens = new HashMap<>() {{
        put(TestScreen.TEST_PPM_AND_MOVEMENT_SPEEDS, new TestPPMAndMovementSpeedsScreen());
        put(TestScreen.TEST_CAMERA_ROOM_SHIFT, new TestCameraRoomShiftScreen());
        put(TestScreen.TEST_MEGAMAN_ANIMATIONS, new TestMegamanAnimations());
        put(TestScreen.TEST_WORLD_SYSTEM, new TestWorldSystemScreen());
        put(TestScreen.TEST_MEGAMAN_SHOOTING, new TestMegamanShooting());
        put(TestScreen.TEST_SPECIAL_MOVEMENTS, new TestSpecialMovements());
        put(TestScreen.TEST_GROUND_DASH_AND_FRICTION, new TestGroundDashAndFrictionScreen());
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

}
