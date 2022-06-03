package com.mygdx.game.screens.menus.impl;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.ConstVals.TextureAssets;
import com.mygdx.game.GameContext2d;
import com.mygdx.game.screens.menus.MenuButton;
import com.mygdx.game.screens.menus.MenuScreen;
import com.mygdx.game.utils.Direction;
import com.mygdx.game.utils.FontHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link MenuScreen} for the main menu of the game.
 */
public class MainMenuScreen extends MenuScreen {

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MainMenuButton {

        GAME_START("GAME START"),
        PASS_WORD("PASS WORD"),
        SETTINGS("SETTINGS"),
        EXIT("EXIT");

        private final String prompt;

    }

    private final Sprite title = new Sprite();
    private final Sprite arrow = new Sprite();
    private final Sprite helmet = new Sprite();
    private final Sprite subtitle = new Sprite();
    private final Map<MainMenuButton, FontHandle> fonts = new EnumMap<>(MainMenuButton.class);

    /**
     * Instantiates a new Main Menu Screen.
     *
     * @param gameContext2d the game context 2 d
     */
    public MainMenuScreen(GameContext2d gameContext2d) {
        super(gameContext2d);
        for (MainMenuButton mainMenuButton : MainMenuButton.values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", 12);
            fontHandle.setText(mainMenuButton.prompt);
            fonts.put(mainMenuButton, fontHandle);
        }
        TextureAtlas textureAtlas = gameContext2d.getAsset(
                TextureAssets.DECORATIONS_TEXTURE_ATLAS, TextureAtlas.class);
        arrow.setRegion(textureAtlas.findRegion("Arrow"));
        title.setRegion(textureAtlas.findRegion("MegamanTitle"));
        helmet.setRegion(textureAtlas.findRegion("MegamanHelmet"));
        subtitle.setRegion(textureAtlas.findRegion("MegamanSubtitle"));
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return new HashMap<>() {{
           put(MainMenuButton.GAME_START.name(), new MenuButton() {
               @Override
               public void onSelect() {
                   // TODO: Set to boss menu screen
                   gameContext.setScreen("");
               }
               @Override
               public void onHighlighted() {

               }
               @Override
               public void onNavigate(Direction direction) {

               }
           });
           put(MainMenuButton.PASS_WORD.name(), new MenuButton() {
               @Override
               public void onSelect() {

               }
               @Override
               public void onHighlighted() {

               }
               @Override
               public void onNavigate(Direction direction) {

               }
           });
           put(MainMenuButton.SETTINGS.name(), new MenuButton() {
               @Override
               public void onSelect() {

               }
               @Override
               public void onHighlighted() {

               }
               @Override
               public void onNavigate(Direction direction) {

               }
           });
           put(MainMenuButton.EXIT.name(), new MenuButton() {
               @Override
               public void onSelect() {

               }
               @Override
               public void onHighlighted() {

               }
               @Override
               public void onNavigate(Direction direction) {

               }
           });
        }};
    }

    private void setArrowToStart() {

    }

    private void setArrowToPassword() {

    }

    private void setArrowToSettings() {

    }

    private void setArrowToExit() {

    }

}
