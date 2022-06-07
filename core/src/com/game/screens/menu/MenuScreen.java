package com.game.screens.menu;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.GameContext2d;
import com.game.controllers.ControllerButton;
import com.game.utils.Direction;
import lombok.Getter;

import java.util.Map;

import static com.game.ConstVals.ViewVals.*;

/**
 * The base class for all menu screens. Menu buttons need to be defined via {@link #defineMenuButtons()}.
 */
public abstract class MenuScreen extends ScreenAdapter {

    protected final Viewport viewport;
    protected final GameContext2d gameContext;
    @Getter private String currentMenuButtonKey;
    private final Map<String, MenuButton> menuButtons;

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public MenuScreen(GameContext2d gameContext) {
        this.gameContext = gameContext;
        this.viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        this.menuButtons = defineMenuButtons();
    }

    /**
     * Assigns map of {@link MenuButton} in the constructor.
     *
     * @return the map of menu buttons
     */
    protected abstract Map<String, MenuButton> defineMenuButtons();

    /**
     * Sets menu button.
     *
     * @param menuButtonKey the menu button key
     */
    public void setMenuButton(String menuButtonKey) {
        currentMenuButtonKey = menuButtonKey;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        viewport.apply();
        MenuButton menuButton = menuButtons.get(currentMenuButtonKey);
        if (menuButton != null) {
            if (gameContext.isJustPressed(ControllerButton.X) ||
                    gameContext.isJustPressed(ControllerButton.A) ||
                    gameContext.isJustPressed(ControllerButton.START)) {
                menuButton.onSelect(delta);
            }
            if (gameContext.isJustPressed(ControllerButton.UP)) {
                menuButton.onNavigate(Direction.UP, delta);
            } else if (gameContext.isJustPressed(ControllerButton.DOWN)) {
                menuButton.onNavigate(Direction.DOWN, delta);
            } else if (gameContext.isJustPressed(ControllerButton.LEFT)) {
                menuButton.onNavigate(Direction.LEFT, delta);
            } else if (gameContext.isJustPressed(ControllerButton.RIGHT)) {
                menuButton.onNavigate(Direction.RIGHT, delta);
            }
        }
    }

    @Override
    public void show() {
        Vector3 camPos = viewport.getCamera().position;
        camPos.x = (VIEW_WIDTH * PPM) / 2f;
        camPos.y = (VIEW_HEIGHT * PPM) / 2f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
