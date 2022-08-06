package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.game.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.game.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.menus.impl.BossSelectScreen.BossSelectButton.*;

public class BossSelectScreen extends MenuScreen {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum BossSelectButton {

        TIMBER_WOMAN("Timber Woman"),
        JELLY_WOMAN("Jelly Woman"),
        TSUNAMI_MAN("Tsunami Man"),
        MANIAC_MAN("Maniac Man"),
        SHROOM_MAN("Shroom Man"),
        ATTIC_MAN("Attic Man"),
        LION_MAN("Lion Man"),
        PASSWORD("Password"),
        QUIT("Quit Game"),
        EXTRAS("Extras");

        private final String prompt;

    }

    private final Sprite topBlackBar = new Sprite();
    private final Sprite bottomBlackBar = new Sprite();

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext2d the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext2d) {
        super(gameContext2d, null);
    }

    @Override
    protected void onMovement() {
        gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class).play();
    }

    @Override
    public void render(float delta) {
        // set text
        // set starting menu button
        // set top and bottom black bars
        // set array of sprites
        // set logic for megaman face and where he looks to
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return Map.of(
                TIMBER_WOMAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                },
                JELLY_WOMAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                },
                TSUNAMI_MAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }, MANIAC_MAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }, SHROOM_MAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }, ATTIC_MAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }, LION_MAN.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }, PASSWORD.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                },
                QUIT.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                },
                EXTRAS.prompt, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                }
        );
    }

}
