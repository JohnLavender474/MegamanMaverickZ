package com.mygdx.game.screens.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.UtilMethods;
import com.mygdx.game.utils.exceptions.InvalidArgumentException;

import java.util.*;

/**
 * Handler for {@link OrthographicCamera} that makes it scroll from one {@link Vector2} target to the next.
 * Targets are supplied via collection of {@link ScrollSectionDef}.
 */
public class ScrollingGameCameraManager extends GameCameraManager {

    private final Vector2 initialPos = new Vector2();
    private final List<ScrollSectionDef> scrollSectionDefs = new ArrayList<>();
    private int currentIndex = 0;

    /**
     * See {@link #ScrollingGameCameraManager(OrthographicCamera, Vector2, Collection)}.
     * {@link OrthographicCamera#position} is passed to initial position parameter.
     *
     * @param camera            the camera
     * @param scrollSectionDefs the scroll section defs
     */
    public ScrollingGameCameraManager(OrthographicCamera camera, Collection<ScrollSectionDef> scrollSectionDefs) {
        this(camera, UtilMethods.toVec2(camera.position), scrollSectionDefs);
    }

    /**
     * Instantiates a new Scrolling Game Camera Handler. Initial position of scrolling process is set.
     *
     * @param camera            the camera
     * @param initialPos        the initial pos
     * @param scrollSectionDefs the scroll section defs
     */
    public ScrollingGameCameraManager(OrthographicCamera camera, Vector2 initialPos,
                                      Collection<ScrollSectionDef> scrollSectionDefs) {
        super(camera);
        this.initialPos.set(initialPos);
        this.scrollSectionDefs.addAll(scrollSectionDefs);
    }

    /**
     * Gets key of current scroll section def. Returns null if the scrolling process is completely finished and there
     * is not another target to scroll to.
     *
     * @return the key of the current scroll section def
     */
    public String getKeyOfCurrentScrollSectionDef() {
        return isFinished() ? null : scrollSectionDefs.get(currentIndex).key();
    }

    /**
     * Return if is finished.
     *
     * @return is finished
     */
    public boolean isFinished() {
        return currentIndex >= scrollSectionDefs.size();
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
     *                                  {@link #scrollSectionDefs}
     */
    public void setCurrentIndex(int currentIndex)
            throws InvalidArgumentException {
        if (currentIndex < 0 || currentIndex >= scrollSectionDefs.size()) {
            throw new InvalidArgumentException(String.valueOf(currentIndex), "current index");
        }
        if (currentIndex > 0) {
            ScrollSectionDef scrollSectionDef = scrollSectionDefs.get(currentIndex - 1);
            initialPos.set(scrollSectionDef.target());
        }
        this.currentIndex = currentIndex;
    }

    @Override
    public void update(float delta) {
        if (isFinished()) {
            return;
        }
        ScrollSectionDef scrollSectionDef = scrollSectionDefs.get(currentIndex);
        scrollSectionDef.timeTicker().update(delta);
        Vector2 pos = UtilMethods.interpolate(initialPos, scrollSectionDef.target(),
                                              scrollSectionDef.timeTicker().getRatio());
        camera.position.x = pos.x;
        camera.position.y = pos.y;
        if (scrollSectionDef.timeTicker().isFinished()) {
            currentIndex++;
            scrollSectionDef.timeTicker().reset();
            initialPos.set(scrollSectionDef.target());
        }
    }

}
