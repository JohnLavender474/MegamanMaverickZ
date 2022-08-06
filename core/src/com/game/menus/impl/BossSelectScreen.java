package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.game.GameContext2d;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.utils.enums.Direction;
import com.game.utils.interfaces.Drawable;
import com.game.utils.objects.FontHandle;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.game.ConstVals.SoundAsset.CURSOR_MOVE_BLOOP_SOUND;
import static com.game.ConstVals.TextureAsset.*;
import static com.game.ConstVals.ViewVals.*;
import static com.game.menus.impl.BossSelectScreen.BossSelectButton.*;
import static lombok.AccessLevel.*;

public class BossSelectScreen extends MenuScreen {

    @RequiredArgsConstructor(access = PRIVATE)
    enum BossSelectButton {

        TIMBER_WOMAN("Timber Woman", 0, 0),
        JELLY_WOMAN("Jelly Woman", 0, 1),
        TSUNAMI_MAN("Tsunami Man", 0, 2),
        MANIAC_MAN("Maniac Man", 1, 0),
        SHROOM_MAN("Shroom Man", 1, 2),
        ATTIC_MAN("Attic Man", 2, 0),
        LION_MAN("Lion Man", 2, 1),
        // TODO: Create name for last boss
        SOMETHING_MAN("Something Man", 2, 2);

        private final String name;
        private final int x;
        private final int y;

    }

    static class BossSpriteHandle implements Drawable {

        private final Sprite sprite = new Sprite();

        private BossSpriteHandle(TextureRegion textureRegion, Vector2 center) {
            sprite.setRegion(textureRegion);
            sprite.setSize(5.33f * PPM, 4f * PPM);
            sprite.setCenter(center.x, center.y);
        }

        @Override
        public void draw(SpriteBatch spriteBatch) {
            sprite.draw(spriteBatch);
        }

    }

    private static final String STORE = "Store";
    private static final String EXTRAS = "Extras";
    private static final String PASSWORD = "Password";
    private static final String QUIT_GAME = "Quit Game";

    private final Sprite topBlackBar = new Sprite();
    private final Sprite bottomBlackBar = new Sprite();
    private final BossSpriteHandle[][] bossSpriteHandles = new BossSpriteHandle[3][3];
    private final Map<BossSelectButton, FontHandle> texts = new EnumMap<>(BossSelectButton.class);

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext) {
        super(gameContext, null);
        // TODO: Set music
    }

    @Override
    public void show() {
        // texts
        for (BossSelectButton button : BossSelectButton.values()) {
            FontHandle text = new FontHandle("Megaman10Font.ttf", 8, new Vector2(button.x, button.y));
            text.setText(button.name);
            texts.put(button, text);
        }
        // top and bottom black bar
        TextureRegion blackRegion = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class)
                .findRegion("Black");
        topBlackBar.setRegion(blackRegion);
        topBlackBar.setSize(VIEW_WIDTH * PPM, PPM);
        bottomBlackBar.set(topBlackBar);
        topBlackBar.setPosition(0f, (VIEW_HEIGHT - 1) * PPM);
    }

    @Override
    protected void onMovement() {
        gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class).play();
    }

    @Override
    public void render(float delta) {
        // begin spritebatch
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        // array of sprites

        // top and bottom black bars
        topBlackBar.draw(spriteBatch);
        bottomBlackBar.draw(spriteBatch);
        // texts
        texts.values().forEach(text -> text.draw(spriteBatch));
        // end spritebatch
        spriteBatch.end();
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        return new HashMap<>() {{
                put(TIMBER_WOMAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(JELLY_WOMAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(TSUNAMI_MAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(MANIAC_MAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(SHROOM_MAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(ATTIC_MAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(LION_MAN.name, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(STORE, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(EXTRAS, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(PASSWORD, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
                put(QUIT_GAME, new MenuButton() {

                    @Override
                    public void onSelect(float delta) {

                    }

                    @Override
                    public void onNavigate(Direction direction, float delta) {

                    }

                });
            }};
    }

}
