package com.game.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.game.tests.screens.TestBossSelectScreen;
import com.game.tests.screens.TestEnemiesScreen;
import com.game.tests.screens.TestPathfindingScreen;
import com.game.tests.screens.TestTrajectoriesScreen;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.badlogic.gdx.Gdx.*;
import static com.game.tests.GameTestRunner.TestScreen.*;

@RequiredArgsConstructor
public class GameTestRunner extends Game {

    private final TestScreen testKey;
    private final Map<TestScreen, Screen> testScreens = Map.of(
            TEST_TRAJECTORIES, new TestTrajectoriesScreen(),
            TEST_PATHFINDING, new TestPathfindingScreen(),
            TEST_BOSS_SELECT, new TestBossSelectScreen(),
            TEST_ENEMIES, new TestEnemiesScreen());

    @Override
    public void create() {
        setScreen(testScreens.get(testKey));
    }

    @Override
    public void render() {
        gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            app.exit();
        }
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        screen.dispose();
    }

    public enum TestScreen {
        TEST_ENEMIES,
        TEST_PATHFINDING,
        TEST_BOSS_SELECT,
        TEST_TRAJECTORIES
    }

}
