package com.game.menus;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.ConstVals.RenderingGround;
import com.game.GameContext2d;
import com.game.controllers.ControllerButton;
import com.game.utils.enums.Direction;
import lombok.Getter;

import java.util.Map;

import static com.game.ConstVals.ViewVals.*;
import static com.game.controllers.ControllerButton.*;

/**
 * The base class for all menu screens. Menu buttons need to be defined via {@link #defineMenuButtons()}.
 */
public abstract class MenuScreen extends ScreenAdapter {

    protected final Viewport uiViewport;
    protected final GameContext2d gameContext;

    private final Map<String, MenuButton> menuButtons;
    private Music music;

    @Getter
    private String currentMenuButtonKey;


    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public MenuScreen(GameContext2d gameContext, String musicSrc) {
        this.gameContext = gameContext;
        this.menuButtons = defineMenuButtons();
        this.music = gameContext.getAsset(musicSrc, Music.class);
        this.uiViewport = gameContext.getViewport(RenderingGround.UI);
    }

    /**
     * Assigns map of {@link MenuButton} in the constructor.
     *
     * @return the map of menu buttons
     */
    protected abstract Map<String, MenuButton> defineMenuButtons();

    /**
     * Called when the cursor has been moved.
     */
    protected void onMovement() {}

    /**
     * Sets menu button.
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
        MenuButton menuButton = menuButtons.get(currentMenuButtonKey);
        if (menuButton != null) {
            if (gameContext.isJustPressed(X) || gameContext.isJustPressed(A) || gameContext.isJustPressed(START)) {
                menuButton.onSelect(delta);
            }
            if (gameContext.isJustPressed(DPAD_UP)) {
                onMovement();
                menuButton.onNavigate(Direction.DIR_UP, delta);
            } else if (gameContext.isJustPressed(DPAD_DOWN)) {
                onMovement();
                menuButton.onNavigate(Direction.DIR_DOWN, delta);
            } else if (gameContext.isJustPressed(DPAD_LEFT)) {
                onMovement();
                menuButton.onNavigate(Direction.DIR_LEFT, delta);
            } else if (gameContext.isJustPressed(DPAD_RIGHT)) {
                onMovement();
                menuButton.onNavigate(Direction.DIR_RIGHT, delta);
            }
        }
    }

    @Override
    public void dispose() {
        music.setLooping(false);
        music.stop();
    }

}
