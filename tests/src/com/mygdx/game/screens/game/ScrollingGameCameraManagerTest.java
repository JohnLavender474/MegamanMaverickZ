package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GdxTestRunner;
import com.mygdx.game.utils.TimeMarkedRunnable;
import com.mygdx.game.utils.Timer;
import com.mygdx.game.utils.UtilMethods;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class ScrollingGameCameraManagerTest {

    private static final String HALFWAY_THROUGH_FIRST = "Halfway through first";
    private static final String HALFWAY_THROUGH_SECOND = "Halfway through second";

    private String status;
    private OrthographicCamera camera;
    private ScrollingGameCameraManager scrollingGameCameraManager;

    @Before
    public void setUp() {
        status = null;
        List<ScrollingSectionDef> scrollingSectionDefs = new ArrayList<>();
        // define scroll section def 1
        Timer timer1 = new Timer(1f, new TimeMarkedRunnable(
                0.5f, () -> status = HALFWAY_THROUGH_FIRST));
        scrollingSectionDefs.add(new ScrollingSectionDef(
                "First", new Vector2(10f, 12f), timer1));
        // define scroll section def 2
        Timer timer2 = new Timer(2f, new TimeMarkedRunnable(
                1f, () -> status = HALFWAY_THROUGH_SECOND));
        scrollingSectionDefs.add(new ScrollingSectionDef(
                "Second", new Vector2(-30f, 15f), timer2));
        // define scrroling game camera handler
        camera = new OrthographicCamera();
        scrollingGameCameraManager = new ScrollingGameCameraManager(
                camera, new Vector2(2f, 10f), scrollingSectionDefs);
    }

    @Test
    public void cameraCorrectAtInitialPos() {
        // given
        camera.position.x = 0f;
        camera.position.y = 0f;
        // when
        scrollingGameCameraManager.update(0f);
        // then
        assertEquals(2f, camera.position.x, 0.001f);
        assertEquals(10f, camera.position.y, 0.001f);
        assertEquals("First", scrollingGameCameraManager.getKeyOfCurrentScrollSectionDef());
    }

    @Test
    public void overHalfwayThroughFirst() {
        // given
        camera.position.x = 0f;
        camera.position.y = 0f;
        // when
        scrollingGameCameraManager.update(0.65f);
        // then
        Vector2 pos = UtilMethods.interpolate(new Vector2(2f, 10f),
                                              new Vector2(10f, 12f), 0.65f);
        assertEquals(HALFWAY_THROUGH_FIRST, status);
        assertEquals(pos.x, camera.position.x, 0.001f);
        assertEquals(pos.y, camera.position.y, 0.001f);
        assertEquals("First", scrollingGameCameraManager.getKeyOfCurrentScrollSectionDef());
    }

    @Test
    public void arrivedAtFirstTarget() {
        // given
        camera.position.x = 0f;
        camera.position.y = 0f;
        // when
        scrollingGameCameraManager.update(0.65f);
        scrollingGameCameraManager.update(0.35f);
        // then
        assertEquals(10f, camera.position.x, 0.001f);
        assertEquals(12f, camera.position.y, 0.001f);
        assertEquals("Second", scrollingGameCameraManager.getKeyOfCurrentScrollSectionDef());
    }

    @Test
    public void overHalfwayThroughSecond() {
        // given
        camera.position.x = 0f;
        camera.position.y = 0f;
        // when
        scrollingGameCameraManager.update(0.65f);
        scrollingGameCameraManager.update(0.35f);
        scrollingGameCameraManager.update(0.5f);
        scrollingGameCameraManager.update(0.65f);
        // then
        Vector2 pos = UtilMethods.interpolate(new Vector2(10f, 12f),
                                              new Vector2(-30f, 15f), (0.5f + 0.65f) / 2f);
        // then
        assertEquals(pos.x, camera.position.x, 0.001f);
        assertEquals(pos.y, camera.position.y, 0.001f);
        assertEquals(HALFWAY_THROUGH_SECOND, status);
        assertEquals("Second", scrollingGameCameraManager.getKeyOfCurrentScrollSectionDef());
    }

    @Test
    public void arrivedAtSecondTarget() {
        // given
        camera.position.x = 0f;
        camera.position.y = 0f;
        // when
        scrollingGameCameraManager.update(0.65f);
        scrollingGameCameraManager.update(0.35f);
        scrollingGameCameraManager.update(0.5f);
        scrollingGameCameraManager.update(0.65f);
        scrollingGameCameraManager.update(0.85f);
        // then
        assertTrue(scrollingGameCameraManager.isFinished());
        assertEquals(-30f, camera.position.x, 0.001f);
        assertEquals(15f, camera.position.y, 0.001f);
        assertNull(scrollingGameCameraManager.getKeyOfCurrentScrollSectionDef());
    }

}