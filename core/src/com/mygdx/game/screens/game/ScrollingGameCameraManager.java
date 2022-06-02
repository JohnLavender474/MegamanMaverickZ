package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidArgumentException;

import java.util.*;

/**
 * Handler for {@link OrthographicCamera} that makes it scroll from one {@link Vector2} target to the next.
 * Targets are supplied via collection of {@link ScrollingSectionDef}.
 */
public class ScrollingGameCameraManager extends GameCameraManager {

    private final Vector2 initialPos = new Vector2();
    private final List<ScrollingSectionDef> scrollingSectionDefs = new ArrayList<>();
    private int currentIndex = 0;

    /**
     * See {@link #ScrollingGameCameraManager(OrthographicCamera, Vector2, Collection)}.
     * {@link OrthographicCamera#position} is passed to initial position parameter.
     *
     * @param camera            the camera
     * @param scrollingSectionDefs the scroll section defs
     */
    public ScrollingGameCameraManager(OrthographicCamera camera, Collection<ScrollingSectionDef> scrollingSectionDefs) {
        this(camera, UtilMethods.toVec2(camera.position), scrollingSectionDefs);
    }

    /**
     * Instantiates a new Scrolling Game Camera Handler. Initial position of scrolling process is set.
     *
     * @param camera            the camera
     * @param initialPos        the initial pos
     * @param scrollingSectionDefs the scroll section defs
     */
    public ScrollingGameCameraManager(OrthographicCamera camera, Vector2 initialPos,
                                      Collection<ScrollingSectionDef> scrollingSectionDefs) {
        super(camera);
        this.initialPos.set(initialPos);
        this.scrollingSectionDefs.addAll(scrollingSectionDefs);
    }

    /**
     * Gets key of current scroll section def. Returns null if the scrolling process is completely finished and there
     * is not another target to scroll to.
     *
     * @return the key of the current scroll section def
     */
    public String getKeyOfCurrentScrollSectionDef() {
        return isFinished() ? null : scrollingSectionDefs.get(currentIndex).key();
    }

    /**
     * Return if is finished.
     *
     * @return is finished
     */
    public boolean isFinished() {
        return currentIndex >= scrollingSectionDefs.size();
    }

    /**
     * Gets current scroll section def index.
     *
     * @return the current scroll section def index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets current scroll section def index.
     *
     * @param currentIndex the index to set the current index to
     * @throws InvalidArgumentException thrown if index is less than 0 or greater than or equal to size of
     *                                  {@link #scrollingSectionDefs}
     */
    public void setCurrentIndex(int currentIndex)
            throws InvalidArgumentException {
        if (currentIndex < 0 || currentIndex >= scrollingSectionDefs.size()) {
            throw new InvalidArgumentException(String.valueOf(currentIndex), "current index");
        }
        if (currentIndex > 0) {
            ScrollingSectionDef scrollingSectionDef = scrollingSectionDefs.get(currentIndex - 1);
            initialPos.set(scrollingSectionDef.target());
        }
        this.currentIndex = currentIndex;
    }

    @Override
    public void update(float delta) {
        if (isFinished()) {
            return;
        }
        ScrollingSectionDef scrollingSectionDef = scrollingSectionDefs.get(currentIndex);
        scrollingSectionDef.timeTicker().update(delta);
        Vector2 pos = UtilMethods.interpolate(initialPos, scrollingSectionDef.target(),
                                              scrollingSectionDef.timeTicker().getRatio());
        camera.position.x = pos.x;
        camera.position.y = pos.y;
        if (scrollingSectionDef.timeTicker().isFinished()) {
            currentIndex++;
            scrollingSectionDef.timeTicker().reset();
            initialPos.set(scrollingSectionDef.target());
        }
    }

}
