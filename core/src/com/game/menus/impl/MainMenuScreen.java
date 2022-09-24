package com.game.menus.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.ConstFuncs;
import com.game.GameScreen;
import com.game.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.utils.ScreenSlide;
import com.game.utils.enums.Direction;
import com.game.text.MegaTextHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.GameScreen.BOSS_SELECT;
import static com.game.assets.MusicAsset.MM11_WILY_STAGE_MUSIC;
import static com.game.sprites.RenderingGround.*;
import static com.game.assets.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.assets.SoundAsset.SELECT_PING_SOUND;
import static com.game.assets.TextureAsset.DECORATIONS;
import static com.game.assets.TextureAsset.MEGAMAN_MAIN_MENU;
import static com.game.ViewVals.*;
import static com.game.menus.impl.MainMenuScreen.MainMenuButton.*;
import static com.game.menus.impl.MainMenuScreen.SettingsButton.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Direction.*;

/**
 * Implementation pairOf {@link MenuScreen} for the main menu pairOf the game.
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
    private final List<MegaTextHandle> fonts = new ArrayList<>();
    private final Map<String, BlinkingArrow> blinkingArrows = new HashMap<>();
    private final ScreenSlide screenSlide;

    /**
     * Instantiates a new Main Menu Screen.
     *
     * @param gameContext the game context 2 d
     */
    public MainMenuScreen(GameContext2d gameContext) {
        super(gameContext, GAME_START.name(), MM11_WILY_STAGE_MUSIC.getSrc());
        // screen slide
        screenSlide = new ScreenSlide(uiViewport.getCamera(), SETTINGS_TRANS, ConstFuncs.getCamInitPos(),
                ConstFuncs.getCamInitPos().add(SETTINGS_TRANS), .5f, true);
        // buttons and arrows
        float row = .175f * PPM;
        for (MainMenuButton mainMenuButton : MainMenuButton.values()) {
            fonts.add(new MegaTextHandle(new Vector2(2f * PPM, row * PPM), mainMenuButton.prompt));
            Vector2 arrowCenter = new Vector2(1.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(mainMenuButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        // fonts
        row = .15f * PPM;
        for (SettingsButton settingsButton : SettingsButton.values()) {
            fonts.add(new MegaTextHandle(new Vector2(17f * PPM, row * PPM), settingsButton.prompt));
            Vector2 arrowCenter = new Vector2(16.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(settingsButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        fonts.add(new MegaTextHandle(new Vector2(3f * PPM, .5f * PPM), "© OLD LAVY GENES, 20XX"));
        fonts.add(new MegaTextHandle(new Vector2(21f * PPM, .15f * PPM * PPM),
                () -> "" + gameContext.getMusicVolume()));
        fonts.add(new MegaTextHandle(new Vector2(21f * PPM, ((.15f * PPM) - (.025f * PPM)) * PPM),
                () -> "" + gameContext.getSoundEffectsVolume()));
        // decorations
        TextureAtlas decorations = gameContext.getAsset(DECORATIONS.getSrc(), TextureAtlas.class);
        title.setRegion(decorations.findRegion("MegamanTitle"));
        title.setBounds(PPM, 6.5f * PPM, 14f * PPM, 8f * PPM);
        TextureAtlas mainMenu = gameContext.getAsset(MEGAMAN_MAIN_MENU.getSrc(), TextureAtlas.class);
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
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        // arrows and sprites
        BlinkingArrow blinkingArrow = blinkingArrows.get(getCurrentMenuButtonKey());
        blinkingArrow.update(delta);
        blinkingArrow.draw(spriteBatch);
        drawFiltered(title, spriteBatch);
        drawFiltered(subtitle, spriteBatch);
        drawFiltered(pose, spriteBatch);
        // fonts
        fonts.forEach(fontHandle -> fontHandle.draw(spriteBatch));
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
                        gameContext.setScreen(GameScreen.EXTRAS);
                        return true;
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
