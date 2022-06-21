package com.game.screens.levels;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.GdxHeadlessTestRunner;
import com.game.utils.Direction;
import com.game.utils.ProcessState;
import com.game.utils.Timer;
import com.game.utils.UtilMethods;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(GdxHeadlessTestRunner.class)
public class LevelCameraManagerTest {

    private static class TestLevelCameraFocusable extends Rectangle implements LevelCameraFocusable {

        private TestLevelCameraFocusable(float width, float height) {
            super(0f, 0f, width, height);
        }

        @Override
        public Vector2 getFocus() {
            return UtilMethods.centerPoint(this);
        }

        @Override
        public Rectangle getCurrentBoundingBox() {
            return this;
        }

        @Override
        public Rectangle getPriorBoundingBox() {
            return this;
        }

    }

    private OrthographicCamera camera;
    private TestLevelCameraFocusable testFocusable;
    private LevelCameraManager rgch;
    private Map<Rectangle, String> gameRooms;

    @Before
    public void setUp() {
        testFocusable = new TestLevelCameraFocusable(1f, 1f);
        camera = new OrthographicCamera();
        gameRooms = new HashMap<>();
        gameRooms.put(new Rectangle(0f, 10f, 10f, 10f), "Top");
        gameRooms.put(new Rectangle(-10f, 0f, 10f, 10f), "Left");
        gameRooms.put(new Rectangle(10f, 0f, 10f, 10f), "Right");
        gameRooms.put(new Rectangle(0f, -10f, 10f, 10f), "Bottom");
        gameRooms.put(new Rectangle(0f, 0f, 10f, 10f), "Center");
        rgch = new LevelCameraManager(camera, new Timer(1f),
                                      gameRooms, testFocusable);
    }

    @Test
    public void focusableCentered() {
        // given
        testFocusable.setCenter(5f, 5f);
        // when
        rgch.update(1f);
        // then
        assertEquals("Center", rgch.getCurrentGameRoomName());
        // when
        rgch.update(1f);
        // then
        assertNull(rgch.getTransitionState());
        assertEquals("Center", rgch.getCurrentGameRoomName());
        assertEquals(UtilMethods.toVec3(testFocusable.getFocus()), camera.position);
    }

    @Test
    public void transitionRight() {
        // given
        testFocusable.setCenter(5f, 5f);
        // when
        rgch.update(1f);
        // then
        assertEquals("Center", rgch.getCurrentGameRoomName());
        // when
        rgch.update(1f);
        // then
        assertNull(rgch.getTransitionState());
        assertEquals("Center", rgch.getCurrentGameRoomName());
        assertEquals(UtilMethods.toVec3(testFocusable.getFocus()), camera.position);
        // when
        Vector2 oldPos = testFocusable.getFocus();
        testFocusable.setPosition(9.55f, 0f);
        rgch.update(1f);
        // then
        assertEquals("Right", rgch.getCurrentGameRoomName());
        assertEquals(ProcessState.BEGIN, rgch.getTransitionState());
        assertEquals(Direction.RIGHT, rgch.getTransitionDirection());
        assertEquals(UtilMethods.toVec3(oldPos), camera.position);
        // when
        rgch.update(.5f);
        Vector2 targetPos = oldPos.cpy();
        targetPos.x = 10f + Math.min(10f / 2.0f, camera.viewportWidth / 2.0f);
        Vector2 interpolatedPos1 = UtilMethods.interpolate(
                oldPos, targetPos, rgch.getTransitionTimeTickerRatio());
        // then
        assertEquals("Right", rgch.getCurrentGameRoomName());
        assertEquals(ProcessState.CONTINUE, rgch.getTransitionState());
        assertEquals(Direction.RIGHT, rgch.getTransitionDirection());
        assertEquals(oldPos, rgch.getTransStartPosCopy());
        assertEquals(targetPos, rgch.getTransTargetPosCopy());
        assertEquals(UtilMethods.toVec3(interpolatedPos1), camera.position);
        // when
        rgch.update(.5f);
        Vector2 interpolatedPos2 = UtilMethods.interpolate(
                oldPos, targetPos, rgch.getTransitionTimeTickerRatio());
        // then
        assertEquals("Right", rgch.getCurrentGameRoomName());
        assertEquals(ProcessState.END, rgch.getTransitionState());
        assertEquals(Direction.RIGHT, rgch.getTransitionDirection());
        assertEquals(oldPos, rgch.getTransStartPosCopy());
        assertEquals(targetPos, rgch.getTransTargetPosCopy());
        assertEquals(UtilMethods.toVec3(interpolatedPos2), camera.position);
        // when
        Vector3 finalPos = camera.position.cpy();
        rgch.update(.5f);
        // then
        assertEquals("Right", rgch.getCurrentGameRoomName());
        assertNull(rgch.getTransitionState());
        assertNull(rgch.getTransitionDirection());
        assertEquals(new Vector2(), rgch.getTransStartPosCopy());
        assertEquals(new Vector2(), rgch.getTransTargetPosCopy());
        assertEquals(finalPos, camera.position);
    }

    @Test
    public void fromInBoundsToOutOfBounds() {
        // given
        testFocusable.setCenter(5f, -5f);
        // when
        rgch.update(1f);
        // then
        assertEquals("Bottom", rgch.getCurrentGameRoomName());
        // when
        rgch.update(1f);
        // then
        assertNull(rgch.getTransitionState());
        assertEquals("Bottom", rgch.getCurrentGameRoomName());
        assertEquals(UtilMethods.toVec3(testFocusable.getFocus()), camera.position);
        // when
        testFocusable.setPosition(100f, 100f);
        Vector3 pos = camera.position.cpy();
        rgch.update(1f);
        // then
        assertEquals("Bottom", rgch.getCurrentGameRoomName());
        assertFalse(rgch.isFocusableBoundingBoxInAnyGameRoom());
        assertNull(rgch.getTransitionDirection());
        assertNull(rgch.getTransitionState());
        assertEquals(pos, camera.position);
    }

    @Test
    public void snapToGameRoom() {
        // given
        testFocusable.setCenter(-5f, 5f);
        // when
        rgch.update(1f);
        // then
        assertEquals("Left", rgch.getCurrentGameRoomName());
        // when
        rgch.update(1f);
        // then
        assertNull(rgch.getTransitionState());
        assertEquals("Left", rgch.getCurrentGameRoomName());
        assertEquals(UtilMethods.toVec3(testFocusable.getFocus()), camera.position);
        // when
        Vector3 pos = camera.position.cpy();
        testFocusable.setCenter(15f, 5f);
        rgch.update(1f);
        // then
        assertNull(rgch.getTransitionState());
        assertEquals("Right", rgch.getCurrentGameRoomName());
        assertEquals(pos, camera.position);
    }

}