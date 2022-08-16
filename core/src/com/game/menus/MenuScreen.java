package com.game.menus;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.RenderingGround;
import com.game.GameContext2d;
import lombok.Getter;

import java.util.Map;

import static com.game.ConstVals.ViewVals.*;
import static com.game.controllers.ControllerButton.*;
import static com.game.utils.enums.Direction.*;

/**
 * The base class for all menu screens. Menu buttons need to be defined via {@link #defineMenuButtons()}.
 */
public abstract class MenuScreen extends ScreenAdapter {

    protected final GameContext2d gameContext;
    protected final Viewport uiViewport;

    protected Music music;

    private final Map<String, MenuButton> menuButtons;

    @Getter
    private String currentMenuButtonKey;
    @Getter
    private boolean selectionMade;


    /**
     * Instantiates a new Menu Screen. The first button is set and music begins playing on showing.
     *
     * @param gameContext the {@link GameContext2d}
     * @param firstButton the button that is highlighted on showing the screen
     * @param musicSrc the music source
     */
    public MenuScreen(GameContext2d gameContext, String firstButton, String musicSrc) {
        this.gameContext = gameContext;
        this.menuButtons = defineMenuButtons();
        this.music = gameContext.getAsset(musicSrc, Music.class);
        this.uiViewport = gameContext.getViewport(RenderingGround.UI);
        setMenuButton(firstButton);
    }

    /**
     * Assigns map of {@link MenuButton} in the constructor.
     *
     * @return the map of menu buttons
     */
    protected abstract Map<String, MenuButton> defineMenuButtons();

    /**
     * Called when the cursor has been moved. Optional method.
     */
    protected void onAnyMovement() {}

    /**
     * Called when any selection has been made. Optional method.
     */
    protected void onAnySelection() {}

    /**
     * Set menu button.
     *
     * @param menuButtonKey the menu button key
     */
    public void setMenuButton(String menuButtonKey) {
        currentMenuButtonKey = menuButtonKey;
    }

    @Override
    public void show() {
        music.play();
        music.setLooping(true);
        gameContext.setDoUpdateController(true);
        Vector3 camPos = uiViewport.getCamera().position;
        camPos.x = (VIEW_WIDTH * PPM) / 2f;
        camPos.y = (VIEW_HEIGHT * PPM) / 2f;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (isSelectionMade()) {
            return;
        }
        MenuButton menuButton = menuButtons.get(currentMenuButtonKey);
        if (menuButton != null) {
            if (gameContext.isJustPressed(DPAD_UP)) {
                onAnyMovement();
                menuButton.onNavigate(DIR_UP, delta);
            } else if (gameContext.isJustPressed(DPAD_DOWN)) {
                onAnyMovement();
                menuButton.onNavigate(DIR_DOWN, delta);
            } else if (gameContext.isJustPressed(DPAD_LEFT)) {
                onAnyMovement();
                menuButton.onNavigate(DIR_LEFT, delta);
            } else if (gameContext.isJustPressed(DPAD_RIGHT)) {
                onAnyMovement();
                menuButton.onNavigate(DIR_RIGHT, delta);
            }
            if (gameContext.isJustPressed(X) || gameContext.isJustPressed(A) || gameContext.isJustPressed(START)) {
                onAnySelection();
                selectionMade = menuButton.onSelect(delta);
            }
        }
    }

    @Override
    public void dispose() {
        music.setLooping(false);
        music.stop();
    }

}
