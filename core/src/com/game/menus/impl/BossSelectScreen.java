package com.game.menus.impl;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.core.ConstVals;
import com.game.core.GameContext2d;
import com.game.animations.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.core.IAssetLoader;
import com.game.menus.utils.BlinkingArrow;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.menus.utils.ScreenSlide;
import com.game.utils.interfaces.Updatable;
import com.game.utils.enums.Direction;
import com.game.utils.enums.Position;
import com.game.utils.interfaces.Drawable;
import com.game.core.FontHandle;
import com.game.utils.objects.Timer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Supplier;

import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import static com.game.core.ConstVals.*;
import static com.game.core.ConstVals.Boss.*;
import static com.game.core.ConstVals.GameScreen.*;
import static com.game.core.ConstVals.MusicAsset.*;
import static com.game.core.ConstVals.RenderingGround.*;
import static com.game.core.ConstVals.SoundAsset.*;
import static com.game.core.ConstVals.TextureAsset.*;
import static com.game.core.ConstVals.ViewVals.*;
import static com.game.utils.UtilMethods.centerPoint;
import static com.game.utils.enums.Position.*;

public class BossSelectScreen extends MenuScreen {

    private enum BossPaneStatus {
        BLINKING,
        HIGHLIGHTED,
        UNHIGHLIGHTED
    }

    private static final float PANE_BOUNDS_WIDTH = 5.33f;
    private static final float PANE_BOUNDS_HEIGHT = 4f;
    private static final float BOTTOM_OFFSET = 1.5f;
    private static final float SPRITE_HEIGHT = 2f;
    private static final float SPRITE_WIDTH = 2.5f;
    private static final float PANE_HEIGHT = 3f;
    private static final float PANE_WIDTH = 4f;

    @Getter
    @Setter
    private static class BossPane implements Updatable, Drawable {

        private final String bossName;
        private final Sprite bossSprite = new Sprite();
        private final Sprite paneSprite = new Sprite();
        private final Supplier<TimedAnimation> bossAnimation;
        private final TimedAnimation paneBlinkingAnimation;
        private final TimedAnimation paneHighlightedAnimation;
        private final TimedAnimation paneUnhighlightedAnimation;

        private BossPaneStatus bossPaneStatus = BossPaneStatus.UNHIGHLIGHTED;

        public BossPane(IAssetLoader assetLoader, Boss boss) {
            this(assetLoader, new TimedAnimation(assetLoader.getAsset(BOSS_FACES_TEXTURE_ATLAS.getSrc(),
                    TextureAtlas.class).findRegion(boss.getBossName())), boss.getBossName(), boss.getPosition());
        }

        public BossPane(IAssetLoader assetLoader, TimedAnimation timedAnimation, String bossName, Position position) {
            this(assetLoader, timedAnimation, bossName, position.getX(), position.getY());
        }

        public BossPane(IAssetLoader assetLoader, TimedAnimation bossAnimation, String bossName, int x, int y) {
            this(assetLoader, () -> bossAnimation, bossName, x, y);
        }

        public BossPane(IAssetLoader assetLoader, Supplier<TimedAnimation> bossAnimation,
                        String bossName, Position position) {
            this(assetLoader, bossAnimation, bossName, position.getX(), position.getY());
        }

        public BossPane(IAssetLoader assetLoader, Supplier<TimedAnimation> bossAnimation,
                        String bossName, int x, int y) {
            this.bossName = bossName;
            this.bossAnimation = bossAnimation;
            // setBounds pane animations
            TextureAtlas decorationAtlas = assetLoader.getAsset(
                    STAGE_SELECT_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
            TextureRegion paneUnhighlighted = decorationAtlas.findRegion("Pane");
            this.paneUnhighlightedAnimation = new TimedAnimation(paneUnhighlighted);
            TextureRegion paneBlinking = decorationAtlas.findRegion("PaneBlinking");
            this.paneBlinkingAnimation = new TimedAnimation(paneBlinking, 2, .125f);
            TextureRegion paneHighlighted = decorationAtlas.findRegion("PaneHighlighted");
            this.paneHighlightedAnimation = new TimedAnimation(paneHighlighted);
            // rect
            Rectangle rect = new Rectangle(x * PANE_BOUNDS_WIDTH * PPM,
                    BOTTOM_OFFSET * PPM + y * PANE_BOUNDS_HEIGHT * PPM,
                    PANE_BOUNDS_WIDTH * PPM, PANE_BOUNDS_HEIGHT * PPM);
            Vector2 centerPoint = centerPoint(rect);
            // met sprite
            bossSprite.setSize(SPRITE_WIDTH * PPM, SPRITE_HEIGHT * PPM);
            bossSprite.setCenter(centerPoint.x, centerPoint.y);
            // pane sprite
            paneSprite.setSize(PANE_WIDTH * PPM, PANE_HEIGHT * PPM);
            paneSprite.setCenter(centerPoint.x, centerPoint.y);
        }

        @Override
        public void update(float delta) {
            bossAnimation.get().update(delta);
            bossSprite.setRegion(bossAnimation.get().getCurrentT());
            TimedAnimation timedAnimation;
            switch (bossPaneStatus) {
                case BLINKING -> timedAnimation = paneBlinkingAnimation;
                case HIGHLIGHTED -> timedAnimation = paneHighlightedAnimation;
                case UNHIGHLIGHTED -> timedAnimation = paneUnhighlightedAnimation;
                default -> throw new IllegalStateException();
            }
            timedAnimation.update(delta);
            paneSprite.setRegion(timedAnimation.getCurrentT());
        }

        @Override
        public void draw(SpriteBatch spriteBatch) {
            Texture paneTexture = paneSprite.getTexture();
            if (paneTexture != null) {
                paneTexture.setFilter(Nearest, Nearest);
            }
            paneSprite.draw(spriteBatch);
            Texture bossTexture = bossSprite.getTexture();
            if (bossTexture != null) {
                bossTexture.setFilter(Nearest, Nearest);
            }
            bossSprite.draw(spriteBatch);
        }

    }

    private static final Vector3 INTRO_BLOCKS_TRANS = new Vector3(15f, 0f, 0f).scl(PPM);
    private static final Set<String> bossNames = new HashSet<>() {{
       for (Boss boss : Boss.values()) {
           add(boss.getBossName());
       }
    }};
    private static final String MEGAMAN_FACE = "MegamanFace";
    private static final String BACK = "BACK";

    private final Camera camera;
    private final Sound bloopSound;
    private final FontHandle bossName;
    private final ScreenSlide screenSlide;
    private final Sprite blackBar1 = new Sprite();
    private final Sprite blackBar2 = new Sprite();
    private final List<FontHandle> texts = new ArrayList<>();
    private final List<BossPane> bossPanes = new ArrayList<>();
    private final Map<Sprite, TimedAnimation> bars = new HashMap<>();
    private final Map<String, BlinkingArrow> blinkingArrows = new HashMap<>();

    private boolean outro;
    private boolean blink;
    private Boss bossSelection;

    private final Timer outroTimer = new Timer(1.05f, new ArrayList<>() {{
        for (int i = 1; i <= 10; i++) {
            add(new TimeMarkedRunnable(.1f * i, () -> blink = !blink));
        }
    }});
    private final Sprite whiteSprite = new Sprite();

    /**
     * Instantiates a new Menu Screen.
     *
     * @param gameContext the {@link GameContext2d}
     */
    public BossSelectScreen(GameContext2d gameContext) {
        super(gameContext, MEGAMAN_FACE, STAGE_SELECT_MM3_MUSIC.getSrc());
        // camera
        this.camera = gameContext.getViewport(UI).getCamera();
        this.screenSlide = new ScreenSlide(camera, INTRO_BLOCKS_TRANS,
                ConstVals.getCamInitPos().sub(INTRO_BLOCKS_TRANS), ConstVals.getCamInitPos(), .5f);
        // sound
        this.bloopSound = gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class);
        // faces
        TextureAtlas megamanFacesAtlas = gameContext.getAsset(MEGAMAN_FACES_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        Map<Position, TimedAnimation> megamanFaceAnimations = new EnumMap<>(Position.class);
        for (Position position : Position.values()) {
            TextureRegion region = megamanFacesAtlas.findRegion(position.name());
            megamanFaceAnimations.put(position, new TimedAnimation(region));
        }
        Supplier<TimedAnimation> megamanAnimSupplier = () -> {
            Boss boss = findByName(getCurrentMenuButtonKey());
            if (boss == null) {
                return megamanFaceAnimations.get(CENTER);
            }
            return megamanFaceAnimations.get(boss.getPosition());
        };
        BossPane megamanPane = new BossPane(gameContext, megamanAnimSupplier, MEGAMAN_FACE, CENTER);
        bossPanes.add(megamanPane);
        // boss bossPanes
        for (Boss boss : Boss.values()) {
            BossPane bossPane = new BossPane(gameContext, boss);
            bossPanes.add(bossPane);
        }
        // text and blinking arrows
        texts.add(new FontHandle("Megaman10Font.ttf", (int) (PPM / 2f),
                new Vector2(5.35f * PPM, 13.85f * PPM), "PRESS START"));
        texts.add(new FontHandle("Megaman10Font.ttf", (int) (PPM / 2f),
                new Vector2(12.35f * PPM, PPM), BACK));
        blinkingArrows.put(BACK, new BlinkingArrow(gameContext, new Vector2(12f * PPM, .75f * PPM)));
        // background
        TextureAtlas stageSelectAtlas = gameContext.getAsset(STAGE_SELECT_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        TextureRegion bar = stageSelectAtlas.findRegion("Bar");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Sprite sprite = new Sprite(bar);
                sprite.setBounds(i * 3f * PPM, (j * 4f * PPM) + 1.35f * PPM, 5.33f * PPM, 4f * PPM);
                TimedAnimation timedAnimation = new TimedAnimation(bar, new float[]{.3f, .15f, .15f, .15f});
                bars.put(sprite, timedAnimation);
            }
        }
        // white sprite and black bar
        TextureAtlas textureAtlas = gameContext.getAsset(DECORATIONS_TEXTURE_ATLAS.getSrc(), TextureAtlas.class);
        TextureRegion white = textureAtlas.findRegion("White");
        whiteSprite.setRegion(white);
        whiteSprite.setBounds(0f, 0f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        TextureRegion black = textureAtlas.findRegion("Black");
        blackBar1.setRegion(black);
        blackBar1.setBounds(-PPM, -PPM, 2f * PPM + VIEW_WIDTH * PPM, PPM + 1.25f * PPM);
        blackBar2.setRegion(black);
        blackBar2.setBounds(0f, 0f, .25f * PPM, VIEW_HEIGHT * PPM);
        // boss name
        bossName = new FontHandle("Megaman10Font.ttf", (int) (PPM / 2f), new Vector2(PPM, PPM));
    }

    @Override
    public void show() {
        super.show();
        screenSlide.init();
        outroTimer.reset();
        outro = false;
    }

    @Override
    protected void onAnyMovement(Direction direction) {
        gameContext.playSound(bloopSound);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // move camera if in intro
        screenSlide.update(delta);
        // begin spritebatch
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        spriteBatch.begin();
        // outro or bars
        if (outro) {
            outroTimer.update(delta);
            if (blink) {
                whiteSprite.draw(spriteBatch);
            }
        }
        bars.forEach((sprite, animation) -> {
            animation.update(delta);
            sprite.setRegion(animation.getCurrentT());
            sprite.draw(spriteBatch);
        });
        // boss bossPanes
        bossPanes.forEach(bossPane -> {
            if (bossPane.getBossName().equals(getCurrentMenuButtonKey())) {
                bossPane.setBossPaneStatus(isSelectionMade() ? BossPaneStatus.HIGHLIGHTED : BossPaneStatus.BLINKING);
            } else {
                bossPane.setBossPaneStatus(BossPaneStatus.UNHIGHLIGHTED);
            }
            bossPane.update(delta);
            bossPane.draw(spriteBatch);
        });
        // black bars
        blackBar1.draw(spriteBatch);
        blackBar2.draw(spriteBatch);
        // blinking arrow
        if (blinkingArrows.containsKey(getCurrentMenuButtonKey())) {
            BlinkingArrow blinkingArrow = blinkingArrows.get(getCurrentMenuButtonKey());
            blinkingArrow.update(delta);
            blinkingArrow.draw(spriteBatch);
        }
        // texts
        texts.forEach(text -> text.draw(spriteBatch));
        // boss name
        if (bossNames.contains(getCurrentMenuButtonKey())) {
            bossName.setText(getCurrentMenuButtonKey().toUpperCase());
            bossName.draw(spriteBatch);
        }
        // end spritebatch
        spriteBatch.end();
        // if outro is finished, setBounds screen
        if (outroTimer.isFinished()) {
            gameContext.setScreen(bossSelection.getGameScreen());
        }
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        Map<String, MenuButton> menuButtons = new HashMap<>();
        menuButtons.put(MEGAMAN_FACE, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                return false;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP -> setMenuButton(findByPos(1, 2).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(1, 0).getBossName());
                    case DIR_LEFT -> setMenuButton(findByPos(0, 1).getBossName());
                    case DIR_RIGHT -> setMenuButton(findByPos(2, 1).getBossName());
                }
            }

        });
        menuButtons.put(BACK, new MenuButton() {

            @Override
            public boolean onSelect(float delta) {
                gameContext.setScreen(MAIN_MENU);
                return true;
            }

            @Override
            public void onNavigate(Direction direction, float delta) {
                switch (direction) {
                    case DIR_UP, DIR_LEFT, DIR_RIGHT -> setMenuButton(findByPos(2, 0).getBossName());
                    case DIR_DOWN -> setMenuButton(findByPos(2, 2).getBossName());
                }
            }

        });
        for (Boss boss : Boss.values()) {
            menuButtons.put(boss.getBossName(), new MenuButton() {

                @Override
                public boolean onSelect(float delta) {
                    gameContext.getAsset(BEAM_OUT_SOUND.getSrc(), Sound.class).play();
                    bossSelection = boss;
                    outro = true;
                    music.stop();
                    return true;
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    int x = boss.getPosition().getX();
                    int y = boss.getPosition().getY();
                    switch (direction) {
                        case DIR_UP -> y += 1;
                        case DIR_DOWN -> y -= 1;
                        case DIR_LEFT -> x -= 1;
                        case DIR_RIGHT -> x += 1;
                    }
                    if (y < 0 || y > 2) {
                        setMenuButton(BACK);
                        return;
                    }
                    if (x < 0) {
                        x = 2;
                    }
                    if (x > 2) {
                        x = 0;
                    }
                    Position position = getByGridIndex(x, y);
                    if (position == null) {
                        throw new IllegalStateException();
                    } else if (position.equals(CENTER)) {
                        setMenuButton(MEGAMAN_FACE);
                    } else {
                        setMenuButton(findByPos(x, y).getBossName());
                    }
                }

            });
        }
        return menuButtons;
    }

}
