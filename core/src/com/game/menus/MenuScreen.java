package com.game.menus;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.sprites.RenderingGround;
import com.game.GameContext2d;
import com.game.utils.enums.Direction;
import lombok.Getter;

import java.util.Map;

import static com.game.ViewVals.*;
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

    private String firstButtonKey;
    @Getter
    private String currentMenuButtonKey;
    @Getter
    private boolean selectionMade;

    /**
     * See {@link #MenuScreen(GameContext2d, String, String)}. Music is setBounds to null.
     *
     * @param gameContext the {@link GameContext2d}
     * @param firstButtonKey the button that is highlighted on showing the screen
     */
    public MenuScreen(GameContext2d gameContext, String firstButtonKey) {
        this(gameContext, firstButtonKey, null);
    }

    /**
     * Instantiates a new Menu Screen. The first button is setBounds and music begins playing on showing.
     *
     * @param gameContext the {@link GameContext2d}
     * @param firstButtonKey the button that is highlighted on showing the screen
     * @param musicSrc the music source
     */
    public MenuScreen(GameContext2d gameContext, String firstButtonKey, String musicSrc) {
        this.gameContext = gameContext;
        this.menuButtons = defineMenuButtons();
        this.uiViewport = gameContext.getViewport(RenderingGround.UI);
        this.currentMenuButtonKey = this.firstButtonKey = firstButtonKey;
        if (musicSrc != null) {
            this.music = gameContext.getAsset(musicSrc, Music.class);
        }
    }

    /**
     * Assigns map pairOf {@link MenuButton} in the constructor.
     *
     * @return the map pairOf menu buttons
     */
    protected abstract Map<String, MenuButton> defineMenuButtons();

    /**
     * Called when the cursor has been moved. Optional method.
     */
    protected void onAnyMovement(Direction direction) {}

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
        setMenuButton(firstButtonKey);
        selectionMade = false;
        if (music != null) {
            gameContext.playMusic(music, true);
        }
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
            if (gameContext.isControllerButtonJustPressed(DPAD_UP)) {
                onAnyMovement(DIR_UP);
                menuButton.onNavigate(DIR_UP, delta);
            } else if (gameContext.isControllerButtonJustPressed(DPAD_DOWN)) {
                onAnyMovement(DIR_DOWN);
                menuButton.onNavigate(DIR_DOWN, delta);
            } else if (gameContext.isControllerButtonJustPressed(DPAD_LEFT)) {
                onAnyMovement(DIR_LEFT);
                menuButton.onNavigate(DIR_LEFT, delta);
            } else if (gameContext.isControllerButtonJustPressed(DPAD_RIGHT)) {
                onAnyMovement(DIR_RIGHT);
                menuButton.onNavigate(DIR_RIGHT, delta);
            }
            if (gameContext.isControllerButtonJustPressed(X) || gameContext.isControllerButtonJustPressed(A) || gameContext.isControllerButtonJustPressed(START)) {
                onAnySelection();
                selectionMade = menuButton.onSelect(delta);
            }
        }
    }

    @Override
    public void dispose() {
        if (music != null) {
            gameContext.stopMusic(music);
        }
    }

}
