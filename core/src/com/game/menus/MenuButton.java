package com.game.menus;

import com.game.utils.enums.Direction;

/**
 * The interface MenuButton prompt.
 */
public interface MenuButton {

    /**
     * Action on select.
     */
    void onSelect(float delta);

    /**
     * Optional action on highlighted.
     *
     * @param delta the delta time
     */
    default void onHighlighted(float delta) {}

    /**
     * On navigate.
     *
     * @param direction the direction
     */
    void onNavigate(Direction direction, float delta);

}
