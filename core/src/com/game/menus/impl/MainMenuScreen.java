package com.game.menus.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import com.game.utils.objects.FontHandle;
import com.game.utils.objects.Timer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.GameScreen.*;
import static com.game.ConstVals.MusicAsset.MMX3_INTRO_STAGE_MUSIC;
import static com.game.ConstVals.SoundAsset.*;
import static com.game.ConstVals.TextureAsset.DECORATIONS_TEXTURE_ATLAS;
import static com.game.ConstVals.ViewVals.PPM;

/**
 * Implementation of {@link MenuScreen} for the main menu of the game.
 */
public class MainMenuScreen extends MenuScreen {

    private final Sprite title = new Sprite();
    private final Sprite arrow = new Sprite();
    private final Sprite helmet = new Sprite();
    private final Sprite subtitle = new Sprite();
    private final Timer arrowBlinkTimer = new Timer(0.2f);
    private final Map<MainMenuButton, FontHandle> fonts = new EnumMap<>(MainMenuButton.class);
    private final Map<MainMenuButton, Vector2> arrowCenters = new EnumMap<>(MainMenuButton.class);

    private boolean arrowIsVisible;

    /**
     * Instantiates a new Main Menu Screen.
     *
     * @param gameContext the game context 2 d
     */
    public MainMenuScreen(GameContext2d gameContext) {
        super(gameContext, MMX3_INTRO_STAGE_MUSIC.getSrc());
    }

    @Override
    public void show() {
        super.show();
        float row = 5f;
        for (MainMenuButton mainMenuButton : MainMenuButton.values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", 8);
            fontHandle.setText(mainMenuButton.prompt);
            fonts.put(mainMenuButton, fontHandle);
            fontHandle.getPosition().set(3 * PPM, row * PPM);
            Vector2 arrowCenter = new Vector2(2.5f * PPM, (row - 0.235f) * PPM);
            arrowCenters.put(mainMenuButton, arrowCenter);
            row -= 0.75f;
        }
        setMenuButton(MainMenuButton.GAME_START.name());
        TextureAtlas textureAtlas = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        arrow.setRegion(textureAtlas.findRegion("Arrow"));
        arrow.setSize(0.5f * PPM, 0.5f * PPM);
        Vector2 arrowCenter = arrowCenters.get(MainMenuButton.GAME_START);
        arrow.setCenter(arrowCenter.x, arrowCenter.y);
        title.setRegion(textureAtlas.findRegion("MegamanTitle"));
        title.setBounds(1 * PPM, 6.5f * PPM, 14f * PPM, 8f * PPM);
        helmet.setRegion(textureAtlas.findRegion("MegamanHelmet"));
        helmet.setBounds(11 * PPM, 1 * PPM, 4f * PPM, 4f * PPM);
        subtitle.setRegion(textureAtlas.findRegion("MegamanSubtitle"));
        subtitle.setBounds(4 * PPM, 6 * PPM, 8 * PPM, 2f * PPM);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        arrowBlinkTimer.update(delta);
        if (arrowBlinkTimer.isFinished()) {
            arrowIsVisible = !arrowIsVisible;
            arrowBlinkTimer.reset();
        }
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        title.draw(spriteBatch);
        helmet.draw(spriteBatch);
        subtitle.draw(spriteBatch);
        if (arrowIsVisible) {
            arrow.draw(spriteBatch);
        }
        fonts.values().forEach(fontHandle -> fontHandle.draw(spriteBatch));
        spriteBatch.end();
    }

    @Override
    protected void onMovement() {
        gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class).play();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return new HashMap<>() {{
            put(MainMenuButton.GAME_START.name(), new MenuButton() {

                @Override
                public void onSelect(float delta) {
                    gameContext.setScreen(TEST_LEVEL_1);
                    // TODO: Set to boss menu screen
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    if (direction == Direction.DIR_DOWN) {
                        setMenuButton(MainMenuButton.PASS_WORD.name());
                        Vector2 center = arrowCenters.get(MainMenuButton.PASS_WORD);
                        arrow.setCenter(center.x, center.y);
                    }
                }

            });
            put(MainMenuButton.PASS_WORD.name(), new MenuButton() {
                @Override
                public void onSelect(float delta) {
                    // TODO: Set to password screen
                    // gameContext.setScreen("");
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    switch (direction) {
                        case DIR_UP -> {
                            setMenuButton(MainMenuButton.GAME_START.name());
                            Vector2 center = arrowCenters.get(MainMenuButton.GAME_START);
                            arrow.setCenter(center.x, center.y);
                        }
                        case DIR_DOWN -> {
                            setMenuButton(MainMenuButton.SETTINGS.name());
                            Vector2 center = arrowCenters.get(MainMenuButton.SETTINGS);
                            arrow.setCenter(center.x, center.y);
                        }
                    }
                }
            });
            put(MainMenuButton.SETTINGS.name(), new MenuButton() {

                @Override
                public void onSelect(float delta) {

                    // TODO: Set to settings screen
                    // gameContext.setScreen("");
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    switch (direction) {
                        case DIR_UP -> {
                            setMenuButton(MainMenuButton.PASS_WORD.name());
                            Vector2 center = arrowCenters.get(MainMenuButton.PASS_WORD);
                            arrow.setCenter(center.x, center.y);
                        }
                        case DIR_DOWN -> {
                            setMenuButton(MainMenuButton.EXIT.name());
                            Vector2 center = arrowCenters.get(MainMenuButton.EXIT);
                            arrow.setCenter(center.x, center.y);
                        }
                    }
                }

            });
            put(MainMenuButton.EXIT.name(), new MenuButton() {

                @Override
                public void onSelect(float delta) {
                    Gdx.app.exit();
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    if (direction == Direction.DIR_UP) {
                        setMenuButton(MainMenuButton.SETTINGS.name());
                        Vector2 center = arrowCenters.get(MainMenuButton.SETTINGS);
                        arrow.setCenter(center.x, center.y);
                    }
                }

            });
        }};
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum MainMenuButton {

        GAME_START("GAME START"), PASS_WORD("PASS WORD"), SETTINGS("SETTINGS"), EXIT("EXIT");

        private final String prompt;

    }

}
