package com.game.menus.impl.bosses;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.game.GameContext2d;
import com.game.entities.bosses.BossEnum;
import com.game.menus.MenuButton;
import com.game.menus.MenuScreen;
import com.game.menus.utils.BlinkingArrow;
import com.game.levels.LevelIntroScreen;
import com.game.utils.objects.TimeMarkedRunnable;
import com.game.animations.TimedAnimation;
import com.game.menus.utils.ScreenSlide;
import com.game.utils.enums.Direction;
import com.game.utils.enums.Position;
import com.game.text.MegaTextHandle;
import com.game.utils.objects.Timer;

import java.util.*;
import java.util.function.Supplier;

import static com.game.ConstFuncs.*;
import static com.game.entities.bosses.BossEnum.*;
import static com.game.GameScreen.*;
import static com.game.assets.MusicAsset.*;
import static com.game.menus.impl.bosses.BossPaneStatus.*;
import static com.game.sprites.RenderingGround.*;
import static com.game.assets.SoundAsset.*;
import static com.game.assets.TextureAsset.*;
import static com.game.ViewVals.*;
import static com.game.utils.UtilMethods.drawFiltered;
import static com.game.utils.enums.Position.*;
import static java.lang.Math.round;

public class BossSelectScreen extends MenuScreen {

    private static final Vector3 INTRO_BLOCKS_TRANS = new Vector3(15f, 0f, 0f).scl(PPM);
    private static final Set<String> bossNames = new HashSet<>() {{
       for (BossEnum bossEnum : BossEnum.values()) {
           add(bossEnum.getBossName());
       }
    }};
    private static final String MEGA_MAN = "MEGA MAN";
    private static final String BACK = "BACK";

    private final Camera camera;
    private final Sound bloopSound;
    private final MegaTextHandle bossName;
    private final ScreenSlide screenSlide;

    private final Sprite blackBar1 = new Sprite();
    private final Sprite blackBar2 = new Sprite();

    private final List<Sprite> backgroundSprites = new ArrayList<>();
    private final List<MegaTextHandle> texts = new ArrayList<>();
    private final List<BossPane> bossPanes = new ArrayList<>();

    private final Map<Sprite, TimedAnimation> bars = new HashMap<>();
    private final Map<String, BlinkingArrow> blinkingArrows = new HashMap<>();

    private boolean outro;
    private boolean blink;
    private BossEnum bossEnumSelection;

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
        super(gameContext, MEGA_MAN, STAGE_SELECT_MM3_MUSIC.getSrc());
        // camera
        this.camera = gameContext.getViewport(UI).getCamera();
        this.screenSlide = new ScreenSlide(camera, INTRO_BLOCKS_TRANS,
                getCamInitPos().sub(INTRO_BLOCKS_TRANS), getCamInitPos(), .5f);
        // sound
        this.bloopSound = gameContext.getAsset(CURSOR_MOVE_BLOOP_SOUND.getSrc(), Sound.class);
        // Megaman faces
        TextureAtlas megamanFacesAtlas = gameContext.getAsset(MEGAMAN_FACES.getSrc(), TextureAtlas.class);
        Map<Position, TextureRegion> megamanFaces = new EnumMap<>(Position.class);
        for (Position position : Position.values()) {
            TextureRegion faceRegion = megamanFacesAtlas.findRegion(position.name());
            megamanFaces.put(position, faceRegion);
        }
        Supplier<TextureRegion> megamanFaceSupplier = () -> {
            BossEnum bossEnum = findByName(getCurrentMenuButtonKey());
            if (bossEnum == null) {
                return megamanFaces.get(CENTER);
            }
            return megamanFaces.get(bossEnum.getPosition());
        };
        BossPane megamanPane = new BossPane(gameContext, megamanFaceSupplier, MEGA_MAN, CENTER);
        bossPanes.add(megamanPane);
        // boss bossPanes
        for (BossEnum boss : BossEnum.values()) {
            BossPane bossPane = new BossPane(gameContext, boss);
            bossPanes.add(bossPane);
        }
        // text and blinking arrows
        texts.add(new MegaTextHandle(round(PPM / 2f), new Vector2(5.35f * PPM, 13.85f * PPM), "PRESS START"));
        texts.add(new MegaTextHandle(round(PPM / 2f), new Vector2(12.35f * PPM, PPM), BACK));
        blinkingArrows.put(BACK, new BlinkingArrow(gameContext, new Vector2(12f * PPM, .75f * PPM)));
        // background bars
        TextureAtlas stageSelectAtlas = gameContext.getAsset(STAGE_SELECT.getSrc(), TextureAtlas.class);
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
        TextureAtlas decorationsAtlas = gameContext.getAsset(DECORATIONS.getSrc(), TextureAtlas.class);
        TextureRegion white = decorationsAtlas.findRegion("White");
        whiteSprite.setRegion(white);
        whiteSprite.setBounds(0f, 0f, VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        TextureRegion black = decorationsAtlas.findRegion("Black");
        blackBar1.setRegion(black);
        blackBar1.setBounds(-PPM, -PPM, (2f + VIEW_WIDTH) * PPM, 2f * PPM);
        blackBar2.setRegion(black);
        blackBar2.setBounds(0f, 0f, .25f * PPM, VIEW_HEIGHT * PPM);
        // background block sprites
        TextureAtlas tilesAtlas = gameContext.getAsset(CUSTOM_TILES.getSrc(), TextureAtlas.class);
        TextureRegion blueBlockRegion = tilesAtlas.findRegion("8bitBlueBlockTransBorder");
        final float halfPPM = PPM / 2f;
        for (int i = 0; i < VIEW_WIDTH; i++) {
            for (int j = 0; j < VIEW_HEIGHT - 1; j++) {
                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 2; y++) {
                        Sprite blueBlock = new Sprite(blueBlockRegion);
                        blueBlock.setBounds(i * PPM + (x * halfPPM), j * PPM + (y * halfPPM), halfPPM, halfPPM);
                        backgroundSprites.add(blueBlock);
                    }
                }
            }
        }
        /*
        for (int i = 0; i < VIEW_WIDTH; i++) {
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    final float topY = (VIEW_HEIGHT - 1) * PPM;
                    Sprite blueBlock = new Sprite(blueBlockRegion);
                    blueBlock.setBounds(i * PPM + (x * halfPPM), topY + (y * halfPPM), halfPPM, halfPPM);
                    backgroundSprites.add(blueBlock);
                }
            }
        }
         */
        // boss name
        bossName = new MegaTextHandle(round(PPM / 2f), new Vector2(PPM, PPM));
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
        // slide screen if intro
        screenSlide.update(delta);
        // begin spritebatch
        SpriteBatch spriteBatch = gameContext.getSpriteBatch();
        gameContext.setSpriteBatchProjectionMatrix(UI);
        spriteBatch.begin();
        // render white sprite on blink if outro
        if (outro) {
            outroTimer.update(delta);
            if (blink) {
                whiteSprite.draw(spriteBatch);
            }
        }
        // render background blocks
        backgroundSprites.forEach(s -> drawFiltered(s, spriteBatch));
        // render flashy bar sprites
        bars.forEach((sprite, animation) -> {
            animation.update(delta);
            sprite.setRegion(animation.getCurrentT());
            sprite.draw(spriteBatch);
        });
        // render boss panes
        bossPanes.forEach(bossPane -> {
            if (bossPane.getBossName().equals(getCurrentMenuButtonKey())) {
                bossPane.setBossPaneStatus(isSelectionMade() ? HIGHLIGHTED : BLINKING);
            } else {
                bossPane.setBossPaneStatus(UNHIGHLIGHTED);
            }
            bossPane.update(delta);
            bossPane.draw(spriteBatch);
        });
        // render black bars
        blackBar1.draw(spriteBatch);
        blackBar2.draw(spriteBatch);
        // render blinking arrow
        if (blinkingArrows.containsKey(getCurrentMenuButtonKey())) {
            BlinkingArrow blinkingArrow = blinkingArrows.get(getCurrentMenuButtonKey());
            blinkingArrow.update(delta);
            blinkingArrow.draw(spriteBatch);
        }
        // render text
        texts.forEach(text -> text.draw(spriteBatch));
        // render boss name
        if (MEGA_MAN.equals(getCurrentMenuButtonKey()) || bossNames.contains(getCurrentMenuButtonKey())) {
            bossName.setText(getCurrentMenuButtonKey().toUpperCase());
            bossName.draw(spriteBatch);
        }
        // end spritebatch
        spriteBatch.end();
        // if outro is finished, set screen to level selection
        if (outroTimer.isFinished()) {
            ((LevelIntroScreen) gameContext.getScreen(LEVEL_INTRO)).set(bossEnumSelection);
            gameContext.setScreen(LEVEL_INTRO);
        }
    }

    @Override
    protected Map<String, MenuButton> defineMenuButtons() {
        Map<String, MenuButton> menuButtons = new HashMap<>();
        menuButtons.put(MEGA_MAN, new MenuButton() {

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
        for (BossEnum bossEnum : BossEnum.values()) {
            menuButtons.put(bossEnum.getBossName(), new MenuButton() {

                @Override
                public boolean onSelect(float delta) {
                    gameContext.getAsset(BEAM_OUT_SOUND.getSrc(), Sound.class).play();
                    bossEnumSelection = bossEnum;
                    outro = true;
                    music.stop();
                    return true;
                }

                @Override
                public void onNavigate(Direction direction, float delta) {
                    int x = bossEnum.getPosition().getX();
                    int y = bossEnum.getPosition().getY();
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
                        setMenuButton(MEGA_MAN);
                    } else {
                        setMenuButton(findByPos(x, y).getBossName());
                    }
                }

            });
        }
        return menuButtons;
    }

}
