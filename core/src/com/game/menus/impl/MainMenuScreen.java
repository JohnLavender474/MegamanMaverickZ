package com.game.menus.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.core.ConstVals;
import com.game.core.GameContext2d;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.menus.utils.ScreenSlide;
import com.game.utils.enums.Direction;
import com.game.core.FontHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.game.core.ConstVals.GameScreen.BOSS_SELECT;
import static com.game.core.ConstVals.MusicAsset.MMX3_INTRO_STAGE_MUSIC;
import static com.game.core.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.core.ConstVals.SoundAsset.SELECT_PING_SOUND;
import static com.game.core.ConstVals.TextureAsset.DECORATIONS_TEXTURE_ATLAS ;
import static com.game.core.ConstVals.TextureAsset.MEGAMAN_MAIN_MENU_ATLAS;
import static com.game.core.ConstVals.ViewVals.*;
import static com.game.menus.impl.MainMenuScreen.MainMenuButton.*;
import static com.game.menus.impl.MainMenuScreen.SettingsButton.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Direction.*;
import static java.lang.Math.round;

/**
 * Implementation of {@link MenuScreen} for the main menu of the game.
 */
public class MainMenuScreen extends MenuScreen {

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum MainMenuButton {

        GAME_START("GAME START"), PASS_WORD("PASS WORD"), SETTINGS("SETTINGS"),  CREDITS("CREDITS"),
        EXTRAS("EXTRAS"), EXIT("EXIT");

        private final String prompt;

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum SettingsButton {

        MUSIC_VOLUME("MUSIC: "), SOUND_EFFECTS_VOLUME("SOUND: "), BACK("BACK");

        private final String prompt;

    }

    private static final Vector3 SETTINGS_TRANS = new Vector3(15f, 0f, 0f).scl(PPM);

    private final Sprite pose = new Sprite();
    private final Sprite title = new Sprite();
    private final Sprite subtitle = new Sprite();
    private final Map<String, FontHandle> fonts = new HashMap<>();
    private final Map<String, BlinkingArrow> blinkingArrows = new HashMap<>();
    private final ScreenSlide screenSlide;
    private final FontHandle musicVolumeFont;
    private final FontHandle soundEffectsVolumeFont;

    /**
     * Instantiates a new Main Menu Screen.
     *
     * @param gameContext the game context 2 d
     */
    public MainMenuScreen(GameContext2d gameContext) {
        super(gameContext, GAME_START.name(), MMX3_INTRO_STAGE_MUSIC.getSrc());
        // screen slide
        screenSlide = new ScreenSlide(uiViewport.getCamera(), SETTINGS_TRANS, ConstVals.getCamInitPos(),
                ConstVals.getCamInitPos().add(SETTINGS_TRANS), .5f, true);
        // buttons and arrows
        float row = .175f * PPM;
        for (MainMenuButton mainMenuButton : MainMenuButton.values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", round(PPM / 2f),
                    new Vector2(2f * PPM, row * PPM));
            fontHandle.setText(mainMenuButton.prompt);
            fonts.put(mainMenuButton.name(), fontHandle);
            Vector2 arrowCenter = new Vector2(1.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(mainMenuButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        // fonts
        row = .15f * PPM;
        for (SettingsButton settingsButton : SettingsButton.values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", round(PPM / 2f),
                    new Vector2(17f * PPM, row * PPM));
            fontHandle.setText(settingsButton.prompt);
            fonts.put(settingsButton.name(), fontHandle);
            Vector2 arrowCenter = new Vector2(16.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(settingsButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        musicVolumeFont = new FontHandle("Megaman10Font.ttf", round(PPM / 2f),
                new Vector2(21f * PPM, .15f * PPM * PPM));
        soundEffectsVolumeFont = new FontHandle("Megaman10Font.ttf", round(PPM / 2f),
                new Vector2(21f * PPM, ((.15f * PPM) - (.025f * PPM)) * PPM));
        // decorations
        TextureAtlas decorations = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        title.setRegion(decorations.findRegion("MegamanTitle"));
        title.setBounds(PPM, 6.5f * PPM, 14f * PPM, 8f * PPM);
        TextureAtlas mainMenu = gameContext.getAsset(MEGAMAN_MAIN_MENU_ATLAS.getSrc(), TextureAtlas.class);
        subtitle.setRegion(mainMenu.findRegion("Subtitle8bit"));
        subtitle.setSize(8f * PPM, 8f * PPM);
        subtitle.setCenter(VIEW_WIDTH * PPM / 2f, VIEW_HEIGHT * PPM / 2f);
        pose.setRegion(mainMenu.findRegion("MegamanPose"));
        pose.setBounds(5.5f * PPM, 0f, 10f * PPM, 10f * PPM);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        // arrows and sprites
        BlinkingArrow blinkingArrow = blinkingArrows.get(getCurrentMenuButtonKey());
        blinkingArrow.update(delta);
        blinkingArrow.draw(spriteBatch);
        drawFiltered(title, spriteBatch);
        drawFiltered(subtitle, spriteBatch);
        drawFiltered(pose, spriteBatch);
        // fonts
        fonts.values().forEach(fontHandle -> fontHandle.draw(spriteBatch));
        musicVolumeFont.setText("" + gameContext.getMusicVolume());
        musicVolumeFont.draw(spriteBatch);
        soundEffectsVolumeFont.setText("" + gameContext.getSoundEffectsVolume());
        soundEffectsVolumeFont.draw(spriteBatch);
        spriteBatch.end();
        // screen slide
        screenSlide.update(delta);
        if (screenSlide.isJustFinished()) {
            screenSlide.reverse();
        }
    }

    @Override
    protected void onAnyMovement(Direction direction) {
        if (equalsAny(direction, DIR_LEFT, DIR_RIGHT)) {
            return;
        }
        Sound sound = gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class);
        gameContext.playSound(sound);
    }

    @Override
    protected void onAnySelection() {
        Sound sound = gameContext.getAsset(SELECT_PING_SOUND.getSrc(), Sound.class);
        gameContext.playSound(sound);
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
                            case DIR_UP -> setMenuButton(EXIT.name());
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
                        screenSlide.init();
                        setMenuButton(BACK.name());
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(PASS_WORD.name());
                            case DIR_DOWN -> setMenuButton(CREDITS.name());
                        }
                    }

                },
                CREDITS.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(SETTINGS.name());
                            case DIR_DOWN -> setMenuButton(EXTRAS.name());
                        }
                    }

                },
                EXTRAS.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(CREDITS.name());
                            case DIR_DOWN -> setMenuButton(EXIT.name());
                        }
                    }

                },
                EXIT.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        // TODO: Pop up dialog asking to confirm exit game, press X to accept, any other to abort
                        Gdx.app.exit();
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(EXTRAS.name());
                            case DIR_DOWN -> setMenuButton(GAME_START.name());
                        }
                    }

                },
                MUSIC_VOLUME.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_LEFT -> {
                                int volume = gameContext.getMusicVolume();
                                volume = volume == 0 ? 10 : volume - 1;
                                gameContext.setMusicVolume(volume);
                            }
                            case DIR_RIGHT -> {
                                int volume = gameContext.getMusicVolume();
                                volume = volume == 10 ? 0 : volume + 1;
                                gameContext.setMusicVolume(volume);
                            }
                            case DIR_UP -> setMenuButton(BACK.name());
                            case DIR_DOWN -> setMenuButton(SOUND_EFFECTS_VOLUME.name());
                        }
                    }

                },
                SOUND_EFFECTS_VOLUME.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_LEFT -> {
                                int volume = gameContext.getSoundEffectsVolume();
                                volume = volume == 0 ? 10 : volume - 1;
                                gameContext.setSoundEffectsVolume(volume);
                            }
                            case DIR_RIGHT -> {
                                int volume = gameContext.getSoundEffectsVolume();
                                volume = volume == 10 ? 0 : volume + 1;
                                gameContext.setSoundEffectsVolume(volume);
                            }
                            case DIR_UP -> setMenuButton(MUSIC_VOLUME.name());
                            case DIR_DOWN -> setMenuButton(BACK.name());
                        }
                    }

                },
                BACK.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        screenSlide.init();
                        setMenuButton(SETTINGS.name());
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(SOUND_EFFECTS_VOLUME.name());
                            case DIR_DOWN -> setMenuButton(MUSIC_VOLUME.name());
                        }
                    }

                });
    }

}
