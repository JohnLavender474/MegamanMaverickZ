package com.game.screens.menu;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.GameContext2d;
import com.game.controllers.ControllerButton;
import com.game.controllers.ControllerButtonStatus;
import com.game.controllers.ControllerListener;
import com.game.utils.Direction;
import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

import static com.game.ConstVals.ViewVals.*;

/**
 * The base class for all menu screens. Menu buttons need to be defined via {@link #defineMenuButtons()}.
 */
public abstract class MenuScreen extends ScreenAdapter implements ControllerListener {

    private static final Map<ControllerButton, Direction> directionalControllerButtonMappings =
            new EnumMap<>(ControllerButton.class) {{
                put(ControllerButton.UP, Direction.UP);
                put(ControllerButton.DOWN, Direction.DOWN);
                put(ControllerButton.LEFT, Direction.LEFT);
                put(ControllerButton.RIGHT, Direction.RIGHT);
            }};

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
    }

    @Override
    public void listenToController(ControllerButton button, ControllerButtonStatus status, float delta) {
        if (status == ControllerButtonStatus.IS_JUST_PRESSED) {
            System.out.println("Is just pressed: " + button);
            MenuButton menuButton = menuButtons.get(currentMenuButtonKey);
            if (menuButton != null) {
                switch (button) {
                    case UP, DOWN, LEFT, RIGHT -> {
                        Direction direction = directionalControllerButtonMappings.get(button);
                        menuButton.onNavigate(direction, delta);
                    }
                    case START, X -> menuButton.onSelect(delta);
                }
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
