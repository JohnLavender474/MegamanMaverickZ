package com.game.levels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.updatables.Updatable;
import com.game.utils.Direction;
import com.game.utils.ProcessState;
import com.game.utils.Timer;
import lombok.Getter;

import java.util.Map;

import static com.game.ConstVals.ViewVals.PPM;
import static com.game.utils.UtilMethods.*;

/**
 * Handler for {@link OrthographicCamera} that makes it transition between {@link Rectangle} "rooms" and follow the
 * {@link CameraFocusable} if contained in a "room" (otherwise camera does nothing). Game rooms are stored within
 * a map as
 * keys and can optionally be associated with a String value.
 */
public class LevelCameraManager implements Updatable {

    private final Camera camera;
    private final Timer transitionTimer;
    private final Map<Rectangle, String> gameRooms;
    private final Vector2 transTargetPos = new Vector2();
    private final Vector2 transStartPos = new Vector2();
    private CameraFocusable queuedFocusable;
    private CameraFocusable focusable;
    private Rectangle currentGameRoom;
    @Getter
    private ProcessState transitionState;
    @Getter
    private Direction transitionDirection;
    private boolean updating;

    public LevelCameraManager(Camera camera, Timer transitionTimer, Map<Rectangle, String> gameRooms,
                              CameraFocusable focusable) {
        this.camera = camera;
        this.gameRooms = gameRooms;
        this.focusable = focusable;
        this.transitionTimer = transitionTimer;
    }

    /**
     * Sets the focusable. If updating, then action is queued for next update cycle.
     *
     * @param focusable the focusable
     */
    public void setFocusable(CameraFocusable focusable) {
        setCamToFocusable();
        if (updating) {
            queuedFocusable = focusable;
        } else {
            this.focusable = focusable;
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
     * Gets transition time ticker ratio.
     *
     * @return the transition time ticker ratio
     */
    public float getTransitionTimeTickerRatio() {
        return transitionTimer.getRatio();
    }

    @Override
    public void update(float delta) {
        if (queuedFocusable != null) {
            focusable = queuedFocusable;
            queuedFocusable = null;
        }
        updating = true;
        if (transitionState == null) {
            /*
             case 1: if current game room is null, try to find next game room and assign it to current game room,
             wait until next update cycle to attempt another action

             case 2: if current game room contains focusable, then set camera position to getCurrentFocus and
             correct bounds if applicable

             case 3: if current game room is not null and doesn't contain focusable, then set next game room,
             and if next game room is a neighbour, then init transition process, otherwise jump directly to
             focusable on next update cycle
             */
            if (currentGameRoom == null) {
                currentGameRoom = nextGameRoom();
            } else if (currentGameRoom.contains(focusable.getFocus())) {
                setCamToFocusable();
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
                // generic 5 * PPM by 5 * PPM square is used to determine push direction
                Rectangle overlap = new Rectangle();
                Rectangle boundingBox = new Rectangle(0f, 0f, 5f * PPM, 5f * PPM).setCenter(focusable.getFocus());
                transitionDirection = getOverlapPushDirection(boundingBox, currentGameRoom, overlap);
                // go ahead and set current game room to next room, which needs to be done even if
                // transition direction is null
                currentGameRoom = nextGameRoom;
                if (transitionDirection == null) {
                    return;
                }
                // set transition state to BEGIN, set start and target vector2 values
                transitionState = ProcessState.BEGIN;
                transStartPos.set(toVec2(camera.position));
                transTargetPos.set(toVec2(camera.position));
                switch (transitionDirection) {
                    case LEFT -> transTargetPos.x =
                            (nextGameRoom.x + nextGameRoom.width) - Math.min(nextGameRoom.width / 2.0f,
                                    camera.viewportWidth / 2.0f);
                    case RIGHT -> transTargetPos.x = nextGameRoom.x + Math.min(nextGameRoom.width / 2.0f,
                            camera.viewportWidth / 2.0f);
                    case UP -> transTargetPos.y = nextGameRoom.y + Math.min(nextGameRoom.height / 2.0f,
                            camera.viewportHeight / 2.0f);
                    case DOWN -> transTargetPos.y =
                            (nextGameRoom.y + nextGameRoom.height) - Math.min(nextGameRoom.height / 2.0f,
                                    camera.viewportHeight / 2.0f);
                }
            }
        } else {
            switch (transitionState) {
                case END:
                    transitionState = null;
                    transitionDirection = null;
                    transStartPos.setZero();
                    transTargetPos.setZero();
                    transitionTimer.reset();
                    break;
                case BEGIN:
                    transitionState = ProcessState.CONTINUE;
                case CONTINUE:
                    transitionTimer.update(delta);
                    Vector2 pos = interpolate(transStartPos, transTargetPos, getTransitionTimeTickerRatio());
                    camera.position.x = pos.x;
                    camera.position.y = pos.y;
                    transitionState = transitionTimer.isFinished() ? ProcessState.END : ProcessState.CONTINUE;
                    break;
            }
        }
        updating = false;
    }

    private Rectangle nextGameRoom() {
        return gameRooms.keySet().stream().filter(gameRoom -> gameRoom.contains(
                focusable.getFocus())).findFirst().orElse(null);
    }

    private void setCamToFocusable() {
        camera.position.x = focusable.getFocus().x;
        camera.position.y = focusable.getFocus().y;
    }

}