package com.mygdx.game.screens.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameContext2d;
import com.mygdx.game.controllers.ControllerButton;
import com.mygdx.game.controllers.ControllerButtonStatus;
import com.mygdx.game.controllers.ControllerListener;
import com.mygdx.game.screens.BaseScreen;
import com.mygdx.game.utils.Direction;
import lombok.Getter;
import org.lwjgl.Sys;

import java.util.EnumMap;
import java.util.Map;

import static com.mygdx.game.ConstVals.ViewVals.*;

/**
 * The type Menu screen.
 */
public abstract class MenuScreen extends BaseScreen implements ControllerListener {

    private static final Map<ControllerButton, Direction> directionalControllerButtonMappings =
            new EnumMap<>(ControllerButton.class) {{
                put(ControllerButton.UP, Direction.UP);
                put(ControllerButton.DOWN, Direction.DOWN);
                put(ControllerButton.LEFT, Direction.LEFT);
                put(ControllerButton.RIGHT, Direction.RIGHT);
            }};

    protected final Viewport viewport;
    @Getter private String currentMenuButtonKey;
    private final Map<String, MenuButton> menuButtons;

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext2d the {@link GameContext2d}
     */
    public MenuScreen(GameContext2d gameContext2d) {
        super(gameContext2d);
        /*
        this.viewport = new ScreenViewport(
                new OrthographicCamera(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM));
         */
        this.viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        this.menuButtons = defineMenuButtons();
        gameContext.getControllerManager().addControllerListener(this);
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
    public void listenToController(ControllerButton controllerButton, ControllerButtonStatus controllerButtonStatus) {
        if (controllerButtonStatus == ControllerButtonStatus.IS_JUST_PRESSED) {
            System.out.println("Is just pressed: " + controllerButton);
            MenuButton menuButton = menuButtons.get(currentMenuButtonKey);
            if (menuButton != null) {
                switch (controllerButton) {
                    case UP, DOWN, LEFT, RIGHT -> {
                        Direction direction = directionalControllerButtonMappings.get(controllerButton);
                        menuButton.onNavigate(direction);
                    }
                    case START, X -> menuButton.onSelect();
                }
            }
        }
    }

    @Override
    public void show() {
        Vector3 camPos = viewport.getCamera().position;
        camPos.x = Gdx.graphics.getWidth() / 2f;
        camPos.y = Gdx.graphics.getHeight() / 2f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        gameContext.getControllerManager().removeControllerListener(this);
    }

}
