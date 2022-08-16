package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import com.game.utils.objects.FontHandle;
import com.game.utils.objects.Timer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import static com.game.ConstVals.GameScreen.BOSS_SELECT;
import static com.game.ConstVals.MusicAsset.MMX3_INTRO_STAGE_MUSIC;
import static com.game.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.ConstVals.SoundAsset.SELECT_PING_SOUND;
import static com.game.ConstVals.TextureAsset.DECORATIONS_TEXTURE_ATLAS ;
import static com.game.ConstVals.ViewVals.PPM;
import static com.game.menus.impl.MainMenuScreen.MainMenuButton.*;
import static com.game.utils.UtilMethods.*;

/**
 * Implementation of {@link MenuScreen} for the main menu of the game.
 */
public class MainMenuScreen extends MenuScreen {

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum MainMenuButton {

        GAME_START("GAME START"), PASS_WORD("PASS WORD"), SETTINGS("SETTINGS"), EXIT("EXIT");

        private final String prompt;

    }

    private final Sprite title = new Sprite();
    private final Sprite helmet = new Sprite();
    private final Sprite subtitle = new Sprite();
    private final Map<MainMenuButton, FontHandle> fonts = new EnumMap<>(MainMenuButton.class);
    private final Map<MainMenuButton, BlinkingArrow> blinkingArrows = new EnumMap<>(MainMenuButton.class);

    /**
     * Instantiates a new Main Menu Screen.
     *
     * @param gameContext the game context 2 d
     */
    public MainMenuScreen(GameContext2d gameContext) {
        super(gameContext, GAME_START.name(), MMX3_INTRO_STAGE_MUSIC.getSrc());
    }

    @Override
    public void show() {
        super.show();
        float row = 5f;
        for (MainMenuButton mainMenuButton : MainMenuButton.values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", 8, new Vector2(3 * PPM, row * PPM));
            fontHandle.setText(mainMenuButton.prompt);
            fonts.put(mainMenuButton, fontHandle);
            Vector2 arrowCenter = new Vector2(2.5f * PPM, (row - .235f) * PPM);
            blinkingArrows.put(mainMenuButton, new BlinkingArrow(gameContext, arrowCenter));
            row -= 0.75f;
        }
        TextureAtlas textureAtlas = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
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
        MainMenuButton currentButton = MainMenuButton.valueOf(getCurrentMenuButtonKey());
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        BlinkingArrow blinkingArrow = blinkingArrows.get(currentButton);
        blinkingArrow.update(delta);
        blinkingArrow.draw(spriteBatch);
        drawFiltered(title, spriteBatch);
        drawFiltered(helmet, spriteBatch);
        drawFiltered(subtitle, spriteBatch);
        fonts.values().forEach(fontHandle -> fontHandle.draw(spriteBatch));
        spriteBatch.end();
    }

    @Override
    protected void onAnyMovement() {
        gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class).play();
    }

    @Override
    protected void onAnySelection() {
        gameContext.getAsset(SELECT_PING_SOUND.getSrc(), Sound.class).play(.5f);
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return Map.of(
                GAME_START.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        gameContext.setScreen(BOSS_SELECT);
                        return true;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_DOWN -> setMenuButton(PASS_WORD.name());
                        }
                    }

                },
                PASS_WORD.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        // TODO: Set to password screen
                        return true;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(GAME_START.name());
                            case DIR_DOWN -> setMenuButton(SETTINGS.name());
                        }
                    }

                },
                SETTINGS.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        // TODO: Set to settings screen
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(PASS_WORD.name());
                            case DIR_DOWN -> setMenuButton(EXIT.name());
                        }
                    }

                },
                EXIT.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        // TODO: Pop up dialog asking to confirm exit game, press X to accept, any other to abort
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(SETTINGS.name());
                        }
                    }

                });
    }

}
