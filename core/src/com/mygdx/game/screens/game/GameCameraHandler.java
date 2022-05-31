package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.*;
import com.mygdx.game.utils.TimeTicker;

import java.util.*;

/**
 * Handles the gameplay camera, specifically handling transitions between "game rooms".
 */
public class GameCameraHandler implements Updatable {

    private final TimeTicker transitionTimeTicker = new TimeTicker();
    private final Vector2 transTargetPos = new Vector2();
    private final Vector2 transStartPos = new Vector2();
    private final OrthographicCamera gameCamera;
    private final Set<Rectangle> gameRooms;
    private Direction transitionDirection;
    private ProcessState transitionState;
    private Rectangle currentGameRoom;
    private Focusable focusable;

    /**
     * Instantiates a new Game camera handler.
     *
     * @param gameCamera         the game camera
     * @param transitionDuration the transition duration
     * @param gameRooms          the game rooms
     */
    public GameCameraHandler(OrthographicCamera gameCamera, float transitionDuration, Set<Rectangle> gameRooms) {
        transitionTimeTicker.setDuration(transitionDuration);
        this.gameCamera = gameCamera;
        this.gameRooms = gameRooms;
    }

    @Override
    public void update(float delta) {
        // If focusable is null, then there is nothing more to do.
        if (focusable == null) {
            return;
        }
        // If the current game room is null, then attempt to find the next game room.
        // If finding next game room is unsuccessful, then there is nothing more to do.
        if (currentGameRoom == null && !findNextGameRoom()) {
            return;
        }
        Vector2 focus = focusable.focus();
        if (transitionState != null || !currentGameRoom.contains(focus)) {
            // If the transition process is initialized or if the focusable is not in the current game room,
            // then call the transition process.
            transition(delta);
        } else {
            // If no transition is initialized or required, then simply set the game camera's position and
            // correct its bounds.
            gameCamera.position.x = focus.x;
            gameCamera.position.y = focus.y;
            correctBounds();
        }
    }

    /**
     * Sets focusable.
     *
     * @param focusable the focusable
     */
    public void setFocusable(Focusable focusable) {
        this.focusable = focusable;
    }

    /**
     * Gets transition direction.
     *
     * @return the transition direction
     */
    public Direction getTransitionDirection() {
        return transitionDirection;
    }

    /**
     * Add game rooms.
     *
     * @param gameRooms the game rooms
     */
    public void addGameRooms(Collection<Rectangle> gameRooms) {
        this.gameRooms.addAll(gameRooms);
    }

    private void transition(float delta) {
        // Transition process ends with resetting transition state to uninitialized state.
        if (transitionState == ProcessState.END) {
            transitionState = null;
            return;
        }
        // Transition process is requested but is uninitialized.
        if (transitionState == null) {
            transitionState = ProcessState.BEGIN;
            transitionTimeTicker.reset();
            Rectangle overlap = new Rectangle();
            transitionDirection = UtilMethods.getOverlapPushDirection(
                    currentGameRoom, focusable.boundingBox(), overlap);
            if (transitionDirection == null) {
                // If null, then abort transition and set current game room to null
                currentGameRoom = null;
                transitionState = null;
                return;
            }
            transStartPos.set(gameCamera.position.x, gameCamera.position.y);
            switch (transitionDirection) {
                case LEFT -> transTargetPos.x = (currentGameRoom.x + currentGameRoom.width) -
                        Math.min(currentGameRoom.width / 2.0f, gameCamera.viewportWidth / 2.0f);
                case RIGHT -> transTargetPos.x = currentGameRoom.x +
                        Math.min(currentGameRoom.width / 2.0f, gameCamera.viewportWidth / 2.0f);
                case UP -> transTargetPos.y = currentGameRoom.y +
                        Math.min(currentGameRoom.height / 2.0f, gameCamera.viewportHeight / 2.0f);
                case DOWN ->  transTargetPos.y = (currentGameRoom.y + currentGameRoom.height) -
                        Math.min(currentGameRoom.height / 2.0f, gameCamera.viewportHeight / 2.0f);
            }
            // End transition process initialization here
            return;
        }
        // Reach here if transition process is already initialized and underway.
        Vector2 pos = UtilMethods.interpolate(transStartPos, transTargetPos, delta);
        gameCamera.position.x = pos.x;
        gameCamera.position.y = pos.y;
        transitionTimeTicker.update(delta);
        transitionState = transitionTimeTicker.isFinished() ? ProcessState.END : ProcessState.CONTINUE;
    }

    private void correctBounds() {
        if (gameCamera.position.y > (currentGameRoom.y + currentGameRoom.height) - gameCamera.viewportHeight / 2.0f) {
            gameCamera.position.y = (currentGameRoom.y + currentGameRoom.height) - gameCamera.viewportHeight / 2.0f;
        }
        if (gameCamera.position.y < currentGameRoom.y + gameCamera.viewportHeight / 2.0f) {
            gameCamera.position.y = currentGameRoom.y + gameCamera.viewportHeight / 2.0f;
        }
        if (gameCamera.position.x > (currentGameRoom.x + currentGameRoom.width) - gameCamera.viewportWidth / 2.0f) {
            gameCamera.position.x = (currentGameRoom.x + currentGameRoom.width) - gameCamera.viewportWidth / 2.0f;
        }
        if (gameCamera.position.x < currentGameRoom.x + gameCamera.viewportWidth / 2.0f) {
            gameCamera.position.x = currentGameRoom.x + gameCamera.viewportWidth / 2.0f;
        }
    }
    
    private boolean findNextGameRoom() {
        for (Rectangle gameRoom : gameRooms) {
            if (gameRoom.contains(focusable.focus())) {
                currentGameRoom = gameRoom;
                return true;
            }
        }
        currentGameRoom = null;
        return false;
    }

}
