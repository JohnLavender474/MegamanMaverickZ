package com.game.menus.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.ConstFuncs;
import com.game.GameContext2d;
import com.game.GameScreen;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.utils.ScreenSlide;
import com.game.text.MegaTextHandle;
import com.game.utils.enums.Direction;
import com.game.utils.objects.Timer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.game.GameScreen.BOSS_SELECT;
import static com.game.ViewVals.*;
import static com.game.assets.MusicAsset.MM11_WILY_STAGE_MUSIC;
import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.DECORATIONS;
import static com.game.assets.TextureAsset.MEGAMAN_MAIN_MENU;
import static com.game.menus.impl.MainMenuScreen.MainMenuButton.*;
import static com.game.menus.impl.MainMenuScreen.SettingsButton.*;
import static com.game.sprites.RenderingGround.UI;
import static com.game.utils.UtilMethods.drawFiltered;
import static com.game.utils.UtilMethods.equalsAny;
import static com.game.utils.enums.Direction.DIR_LEFT;
import static com.game.utils.enums.Direction.DIR_RIGHT;

/**
 * Implementation pairOf {@link MenuScreen} for the main menu pairOf the game.
 */
public class MainMenuScreen extends MenuScreen {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum MainMenuButton {

        GAME_START("GAME START"),
        PASS_WORD("PASS WORD"),
        SETTINGS("SETTINGS"),
        CREDITS("CREDITS"),
        EXTRAS("EXTRAS"),
        EXIT("EXIT");

        private final String prompt;

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum SettingsButton {

        BACK("BACK"),
        MUSIC_VOLUME("MUSIC: "),
        SOUND_EFFECTS_VOLUME("SOUND: "),
        FPS("FPS: ");
        // CONTROLLER_SETTINGS("CONTROLLER SETTINGS");

        private final String prompt;

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ControllerSettingsButton {

        UP("Up"),
        DOWN("Down"),
        LEFT("Left"),
        RIGHT("Right"),
        X("X"),
        A("A"),
        START("Start");

        private final String str;

    }

    private static final List<Integer> FPS_OPTIONS = List.of(30, 45, 60, 75);
    private static final Vector3 SETTINGS_TRANS = new Vector3(15f, 0f, 0f).scl(PPM);

    private final ScreenSlide screenSlide;
    private final Sprite pose = new Sprite();
    private final Sprite title = new Sprite();
    private final Sprite subtitle = new Sprite();
    private final Timer settingsArrowTimer = new Timer(.3f);
    private final List<MegaTextHandle> fonts = new ArrayList<>();
    private final List<Sprite> settingsArrows = new ArrayList<>();
    private final Map<String, BlinkingArrow> blinkingArrows = new HashMap<>();

    private boolean settingsArrowBlink;
    private int fpsIndex = 2;

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
            fonts.add(new MegaTextHandle(new Vector2(2f * PPM, row * PPM), mainMenuButton.getPrompt()));
            Vector2 arrowCenter = new Vector2(1.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(mainMenuButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        // fonts
        row = .4f * PPM;
        for (SettingsButton settingsButton : SettingsButton.values()) {
            fonts.add(new MegaTextHandle(new Vector2(17f * PPM, row * PPM), settingsButton.getPrompt()));
            Vector2 arrowCenter = new Vector2(16.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(settingsButton.name(), new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        fonts.add(new MegaTextHandle(new Vector2(3f * PPM, .5f * PPM), "© OLD LAVY GENES, 20XX"));
        fonts.add(new MegaTextHandle(new Vector2(21f * PPM, 12f * PPM),
                () -> "" + gameContext.getMusicVolume()));
        fonts.add(new MegaTextHandle(new Vector2(21f * PPM, 11.2f * PPM),
                () -> "" + gameContext.getSoundEffectsVolume()));
        fonts.add(new MegaTextHandle(new Vector2(21f * PPM, 10.4f * PPM),
                () -> "" + FPS_OPTIONS.get(fpsIndex)));
        TextureRegion arrowRegion = gameContext.getAsset(DECORATIONS.getSrc(), TextureAtlas.class)
                .findRegion("Arrow");
        // settings blinking arrows
        float y = 11.55f;
        for (int i = 0; i < 6; i++) {
            if (i != 0 && i % 2 == 0) {
                y -= .85f;
            }
            Sprite blinkingArrow = new Sprite(arrowRegion);
            blinkingArrow.setBounds((i % 2 == 0 ? 20.25f : 22.5f) * PPM, y * PPM, PPM / 2f, PPM / 2f);
            if (i % 2 == 0) {
                blinkingArrow.setFlip(true, false);
            }
            settingsArrows.add(blinkingArrow);
        }
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
        settingsArrowTimer.update(delta);
        if (settingsArrowTimer.isFinished()) {
            settingsArrowBlink = !settingsArrowBlink;
            settingsArrowTimer.reset();
        }
        if (settingsArrowBlink) {
            settingsArrows.forEach(s -> drawFiltered(s, spriteBatch));
        }
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
        Sound sound = gameContext.getAsset(PAUSE_SOUND.getSrc(), Sound.class);
        gameContext.playSound(sound);
    }

    @Override
    protected void onAnySelection() {
        Sound sound = gameContext.getAsset(SELECT_PING_SOUND.getSrc(), Sound.class);
        gameContext.playSound(sound);
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return new HashMap<>() {{
                put(GAME_START.name(), new MenuButton() {

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

                });
                put(PASS_WORD.name(), new MenuButton() {

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

                });
                put(SETTINGS.name(), new MenuButton() {

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

                });
                put(CREDITS.name(), new MenuButton() {

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

                });
                put(EXTRAS.name(), new MenuButton() {

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

                });
                put(EXIT.name(), new MenuButton() {

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

                });
                put(MUSIC_VOLUME.name(), new MenuButton() {

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

                });
                put(SOUND_EFFECTS_VOLUME.name(), new MenuButton() {

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
                            case DIR_DOWN -> setMenuButton(FPS.name());
                        }
                    }

                });
                put(FPS.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        return false;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_LEFT -> changeFPS(-1);
                            case DIR_RIGHT -> changeFPS(1);
                            case DIR_UP -> setMenuButton(SOUND_EFFECTS_VOLUME.name());
                            // case DIR_DOWN -> setMenuButton(CONTROLLER_SETTINGS.name());
                            case DIR_DOWN -> setMenuButton(BACK.name());
                        }
                    }

                });
                /*
                put(CONTROLLER_SETTINGS.name(), new MenuButton() {

                    @Override
                    public boolean onSelect(float delta) {
                        gameContext.setScreen(GameScreen.CONTROLLER_SETTINGS);
                        return true;
                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {
                        switch (direction) {
                            case DIR_UP -> setMenuButton(FPS.name());
                            case DIR_DOWN -> setMenuButton(BACK.name());
                        }
                    }

                });
                 */
                put(BACK.name(), new MenuButton() {

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
        }};
    }

    private void changeFPS(int i) {
        int next = fpsIndex + i;
        if (next < 0) {
            next = FPS_OPTIONS.size() - 1;
        } else if (next >= FPS_OPTIONS.size()) {
            next = 0;
        }
        fpsIndex = next;
        int fps = FPS_OPTIONS.get(fpsIndex);
        Gdx.graphics.setForegroundFPS(fps);
    }

}
