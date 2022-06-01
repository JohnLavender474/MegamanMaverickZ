package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.*;
import lombok.Getter;

import java.util.*;

/**
 * Handler for {@link OrthographicCamera} that makes it transition between {@link Rectangle} "rooms" and follow the
 * {@link Focusable} if contained in a "room" (otherwise camera does nothing). Game rooms are stored within a map as
 * keys and can optionally be associated with a String value.
 */
public class RoomsGameCameraManager extends GameCameraManager {

    private final Map<Rectangle, String> gameRooms = new HashMap<>();
    private final Vector2 transTargetPos = new Vector2();
    private final Vector2 transStartPos = new Vector2();
    private final TimeTicker transitionTimeTicker;
    private final Focusable focusable;
    private Rectangle currentGameRoom;
    @Getter
    private ProcessState transitionState;
    @Getter
    private Direction transitionDirection;

    /**
     * Instantiates a new Rooms game camera handler.
     *
     * @param camera             the camera
     * @param focusable          the focusable
     * @param gameRooms          the game rooms
     * @throws RoomsGameCameraHandlerException thrown if camera or focusable is null or if game rooms is empty or if
     *                                         transition duration is less than or equal to 0
     */
    public RoomsGameCameraManager(OrthographicCamera camera, Focusable focusable,
                                  Map<Rectangle, String> gameRooms, TimeTicker transitionTimeTicker)
            throws RoomsGameCameraHandlerException {
        super(camera);
        this.focusable = focusable;
        this.gameRooms.putAll(gameRooms);
        this.transitionTimeTicker = transitionTimeTicker;
        if (focusable == null || camera == null || gameRooms.isEmpty()) {
            throw new RoomsGameCameraHandlerException(
                    "Fields focusable and gameRooms cannot be null, game rooms cannot be empty, and transition " +
                            "duration must be greater than 0. focusable: [" + focusable + "], camera: " +
                            "[" + camera + "], gameRooms: [" + gameRooms + "]");
        }
    }

    /**
     * Returns the String value associated with {@link #currentGameRoom}.
     *
     * @return the String value associated with the current game room
     */
    public String getCurrentGameRoomName() {
        return gameRooms.get(currentGameRoom);
    }

    /**
     * Gets trans target pos copy.
     *
     * @return the trans target pos copy
     */
    public Vector2 getTransTargetPosCopy() {
        return transTargetPos.cpy();
    }

    /**
     * Gets trans start pos copy.
     *
     * @return the trans start pos copy
     */
    public Vector2 getTransStartPosCopy() {
        return transStartPos.cpy();
    }

    /**
     * Gets transition time ticker ratio.
     *
     * @return the transition time ticker ratio
     */
    public float getTransitionTimeTickerRatio() {
        return transitionTimeTicker.getRatio();
    }

    /**
     * Returns if the {@link Focusable} is not contained in any of the game rooms.
     *
     * @return if focusable is not contained in any of the game rooms
     */
    public boolean isFocusableBoundingBoxInAnyGameRoom() {
        return gameRooms.keySet().stream().anyMatch(
                gameRoom -> gameRoom.overlaps(focusable.boundingBox()));
    }

    private Rectangle nextGameRoom() {
        return gameRooms.keySet().stream().filter(
                gameRoom -> gameRoom.contains(focusable.focus())).findFirst().orElse(null);
    }

    @Override
    public void update(float delta)
            throws RoomsGameCameraHandlerException {
        if (focusable == null || camera == null || gameRooms.isEmpty()) {
            throw new RoomsGameCameraHandlerException(
                    "Fields focusable and gameRooms cannot be null, game rooms cannot be empty, and transition " +
                            "duration must be greater than 0. focusable: [" + focusable + "], camera: " +
                            "[" + camera + "], gameRooms: [" + gameRooms + "]");
        }
        if (transitionState == null) {
            /*
             case 1: if current game room is null, try to find next game room and assign it to current game room,
             wait until next update cycle to attempt another action

             case 2: if current game room contains focusable, then set camera position to focus and correct bounds
             if applicable

             case 3: if current game room is not null and doesn't contain focusable, then set next game room,
             and if next game room is a neighbour, then init transition process, otherwise jump directly to
             focusable on next update cycle
             */
            if (currentGameRoom == null) {
                currentGameRoom = nextGameRoom();
            } else if (currentGameRoom.contains(focusable.focus())) {
                Vector2 focus = focusable.focus();
                camera.position.x = focus.x;
                camera.position.y = focus.y;
                if (camera.position.y > (currentGameRoom.y + currentGameRoom.height) - camera.viewportHeight / 2.0f) {
                    camera.position.y = (currentGameRoom.y + currentGameRoom.height) - camera.viewportHeight / 2.0f;
                }
                if (camera.position.y < currentGameRoom.y + camera.viewportHeight / 2.0f) {
                    camera.position.y = currentGameRoom.y + camera.viewportHeight / 2.0f;
                }
                if (camera.position.x > (currentGameRoom.x + currentGameRoom.width) - camera.viewportWidth / 2.0f) {
                    camera.position.x = (currentGameRoom.x + currentGameRoom.width) - camera.viewportWidth / 2.0f;
                }
                if (camera.position.x < currentGameRoom.x + camera.viewportWidth / 2.0f) {
                    camera.position.x = currentGameRoom.x + camera.viewportWidth / 2.0f;
                }
            } else {
                Rectangle nextGameRoom = nextGameRoom();
                // if next game room is null, do nothing and return
                if (nextGameRoom == null) {
                    return;
                }
                Rectangle overlap = new Rectangle();
                transitionDirection = UtilMethods.getOverlapPushDirection(
                        focusable.boundingBox(), currentGameRoom, overlap);
                // go ahead and set current game room to next room, which needs to be done even if
                // transition direction is null
                currentGameRoom = nextGameRoom;
                if (transitionDirection == null) {
                    return;
                }
                // set transition state to BEGIN, set start and target vector2 values
                transitionState = ProcessState.BEGIN;
                transStartPos.set(UtilMethods.toVec2(camera.position));
                transTargetPos.set(UtilMethods.toVec2(camera.position));
                switch (transitionDirection) {
                    case LEFT -> transTargetPos.x = (nextGameRoom.x + nextGameRoom.width) -
                            Math.min(nextGameRoom.width / 2.0f, camera.viewportWidth / 2.0f);
                    case RIGHT -> transTargetPos.x = nextGameRoom.x +
                            Math.min(nextGameRoom.width / 2.0f, camera.viewportWidth / 2.0f);
                    case UP -> transTargetPos.y = nextGameRoom.y +
                            Math.min(nextGameRoom.height / 2.0f, camera.viewportHeight / 2.0f);
                    case DOWN -> transTargetPos.y = (nextGameRoom.y + nextGameRoom.height) -
                            Math.min(nextGameRoom.height / 2.0f, camera.viewportHeight / 2.0f);
                }
            }
        } else {
            switch (transitionState) {
                case END:
                    transitionState = null;
                    transitionDirection = null;
                    transStartPos.setZero();
                    transTargetPos.setZero();
                    break;
                case BEGIN:
                    transitionState = ProcessState.CONTINUE;
                case CONTINUE:
                    transitionTimeTicker.update(delta);
                    Vector2 pos = UtilMethods.interpolate(
                            transStartPos, transTargetPos, getTransitionTimeTickerRatio());
                    camera.position.x = pos.x;
                    camera.position.y = pos.y;
                    transitionState = transitionTimeTicker.isFinished() ? ProcessState.END : ProcessState.CONTINUE;
                    break;
            }
        }
    }

}