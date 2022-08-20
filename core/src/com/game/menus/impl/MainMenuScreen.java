package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.core.GameContext2d;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import com.game.utils.objects.FontHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import static com.game.core.ConstVals.GameScreen.BOSS_SELECT;
import static com.game.core.ConstVals.MusicAsset.MMX3_INTRO_STAGE_MUSIC;
import static com.game.core.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.core.ConstVals.SoundAsset.SELECT_PING_SOUND;
import static com.game.core.ConstVals.TextureAsset.DECORATIONS_TEXTURE_ATLAS ;
import static com.game.core.ConstVals.TextureAsset.MEGAMAN_MAIN_MENU_ATLAS;
import static com.game.core.ConstVals.ViewVals.*;
import static com.game.menus.impl.MainMenuScreen.MainMenuButton.*;
import static com.game.utils.UtilMethods.*;
import static com.game.utils.enums.Direction.DIR_LEFT;
import static com.game.utils.enums.Direction.DIR_RIGHT;
import static java.lang.Math.round;

/**
 * Implementation of {@link MenuScreen} for the main menu of the game.
 */
public class MainMenuScreen extends MenuScreen {

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum MainMenuButton {

        GAME_START("GAME START"), PASS_WORD("PASS WORD"), SETTINGS("SETTINGS"), EXIT("EXIT");

        private final String prompt;

    }

    private final Sprite z = new Sprite();
    private final Sprite pose = new Sprite();
    private final Sprite title = new Sprite();
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
        float row = .15f * PPM;
        for (MainMenuButton mainMenuButton : values()) {
            FontHandle fontHandle = new FontHandle("Megaman10Font.ttf", round(PPM / 2f),
                    new Vector2(2f * PPM, row * PPM));
            fontHandle.setText(mainMenuButton.prompt);
            fonts.put(mainMenuButton, fontHandle);
            Vector2 arrowCenter = new Vector2(1.5f * PPM, (row - (.0075f * PPM)) * PPM);
            blinkingArrows.put(mainMenuButton, new BlinkingArrow(gameContext, arrowCenter));
            row -= PPM * .025f;
        }
        TextureAtlas decorations = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        title.setRegion(decorations.findRegion("MegamanTitle"));
        title.setBounds(PPM, 6.5f * PPM, 14f * PPM, 8f * PPM);
        TextureAtlas mainMenu = gameContext.getAsset(MEGAMAN_MAIN_MENU_ATLAS.getSrc(), TextureAtlas.class);
        subtitle.setRegion(mainMenu.findRegion("Subtitle8bit"));
        subtitle.setSize(8f * PPM, 8f * PPM);
        subtitle.setCenter((VIEW_WIDTH * PPM / 2f) - 1.75f * PPM, VIEW_HEIGHT * PPM / 2f);
        pose.setRegion(mainMenu.findRegion("MegamanPose"));
        pose.setBounds(5.5f * PPM, 0f, 10f * PPM, 10f * PPM);
        z.setRegion(mainMenu.findRegion("Z8bit"));
        z.setBounds(subtitle.getX() + 6f * PPM, subtitle.getY(), 8f * PPM, 8f * PPM);
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
        drawFiltered(subtitle, spriteBatch);
        drawFiltered(z, spriteBatch);
        drawFiltered(pose, spriteBatch);
        fonts.values().forEach(fontHandle -> fontHandle.draw(spriteBatch));
        spriteBatch.end();
    }

    @Override
    protected void onAnyMovement(Direction direction) {
        if (equalsAny(direction, DIR_LEFT, DIR_RIGHT)) {
            return;
        }
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
                            case DIR_DOWN -> setMenuButton(GAME_START.name());
                        }
                    }

                });
    }

}
