package com.game.menus;

import com.game.utils.enums.Direction;

/**
 * The interface MenuButton.
 */
public interface MenuButton {

    /**
     * Action on select. Return if selection should stop user from being able to continue navigating buttons.
     * WARNING: If the method returns true, then the user will no longer be able to use the current menu.
     * Only return true if a new screen is to be opened on select.
     */
    boolean onSelect(float delta);

    /**
     * On navigate.
     *
     * @param direction the direction
     */
    void onNavigate(Direction direction, float delta);

}
