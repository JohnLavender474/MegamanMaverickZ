package com.game.entities.bosses;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.game.GameScreen;
import com.game.animations.TimedAnimation;
import com.game.assets.TextureAsset;
import com.game.utils.enums.Position;
import com.game.utils.objects.KeyValuePair;
import com.game.utils.objects.Timer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.game.levels.BossIntroScreen.BOSS_DROP_DOWN;
import static com.game.utils.enums.Position.*;
import static lombok.AccessLevel.PRIVATE;

/**
 * The bosses.
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BossEnum {

    // Timber Woman
    TIMBER_WOMAN("Timber Woman", TOP_LEFT,
            TextureAsset.TIMBER_WOMAN.getSrc(), GameScreen.TIMBER_WOMAN) {

        @Override
        public String getBio() {
            return "Originally designed to be 'Timbre Woman', she was \n" +
                    "planned to be the finest a cappella singer in the world. \n" +
                    "But one pairOf the programmers screwed up and typed \n" +
                    "'Timber' instead pairOf 'Timbre' into her firmware, and \n" +
                    "as a result, she became the world's strongest \n" +
                    "lumberjack. She is an advocate for responsible \n" +
                    "forestation practices and dreams pairOf building a \n" +
                    "'City Among the Trees' should she ever hold \n" +
                    "a position in government.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return Map.of(
                    "Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}),
                    "JustLand", new TimedAnimation(textureAtlas.findRegion("JustLand"), 6, .1f, false),
                    "AboutToSwing", new TimedAnimation(textureAtlas.findRegion("AboutToSwing"), 3, .15f, false),
                    "Swing", new TimedAnimation(textureAtlas.findRegion("Swing"), 4, .15f, false),
                    "Jump", new TimedAnimation(textureAtlas.findRegion("Jump"), 6, BOSS_DROP_DOWN / 12));
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("JustLand"), new Timer(.6f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1.75f)));
                add(KeyValuePair.of(anims.get("AboutToSwing"), new Timer(.375f)));
                add(KeyValuePair.of(anims.get("Swing"), new Timer(4f)));
            }};
        }

    },

    // Distributor Man
    DISTRIBUTOR_MAN("Distributor Man", TOP_CENTER,
            TextureAsset.DISTRIBUTOR_MAN.getSrc(), GameScreen.DISTRIBUTOR_MAN) {

        @Override
        public String getBio() {
            return "";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return Map.of();
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            return new LinkedList<>();
        }

    },

    // Roaster Man
    ROASTER_MAN("Roaster Man", TOP_RIGHT,
            TextureAsset.ROASTER_MAN.getSrc(), GameScreen.ROASTER_MAN) {

        @Override
        public String getBio() {
            return "";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3f, 2.85f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return Map.of();
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            return new LinkedList<>();
        }

    },

    // Mister Man
    MISTER_MAN("Mister Man", CENTER_LEFT,
            TextureAsset.MISTER_MAN.getSrc(), GameScreen.MISTER_MAN) {

        @Override
        public String getBio() {
            return "";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3.25f, 2.85f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return Map.of();
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            return new LinkedList<>();
        }

    },

    // Blunt Man
    BLUNT_MAN("Blunt Man", CENTER_RIGHT,
            TextureAsset.BLUNT_MAN.getSrc(), GameScreen.BLUNT_MAN) {

        @Override
        public String getBio() {
            return "";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2();
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return null;
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            return null;
        }

    }

    /*
    MANIAC_MAN("Maniac Man", BOTTOM_CENTER, null),
    TSUNAMI_MAN("Tsunami Man", BOTTOM_RIGHT, null),
    SALES_MAN("Sales Man", CENTER_LEFT, null),
    HISTRIONIC_MAN("Histrionic Man", CENTER_RIGHT, null),
    BEACON_MAN("Beacon Man", TOP_LEFT, null),
    WEED_MAN("Weed Man", TOP_CENTER, null),
    LIGHTER_MAN("Lighter Man", TOP_RIGHT, null)
     */;

    private final String bossName;
    private final Position position;
    private final String textureAtlas;
    private final GameScreen gameScreen;

    /**
     * Get the bio of the boss.
     *
     * @return the bio
     */
    public abstract String getBio();

    /**
     * Get the size of the boss's sprite.
     *
     * @return the sprite size
     */
    public abstract Vector2 getSpriteSize();

    /**
     * Get the boss's animations.
     *
     * @param textureAtlas the texture atlas for the boss
     * @return the boss's animations
     */
    public abstract Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas);

    /**
     * Get the intro animations for the boss
     *
     * @param textureAtlas the texture atlas for the boss
     * @return the intro animations for the boss
     */
    public abstract Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas);

    /**
     * Find boss enum value by name.
     *
     * @param name the name
     * @return the boss enum value
     */
    public static BossEnum findByName(String name) {
        /*
        for (BossEnum boss : values()) {
            if (name.equals(boss.getBossName())) {
                return boss;
            }
        }
        return null;
         */
        return TIMBER_WOMAN;
    }

    /**
     * Find boss enum value by position.
     *
     * @param position the position
     * @return the boss enum value
     */
    public static BossEnum findByPos(Position position) {
        /*
        for (BossEnum boss : values()) {
            if (boss.getPosition().equals(position)) {
                return boss;
            }
        }
        return null;
         */
        return TIMBER_WOMAN;
    }

    /**
     * Find the boss enum value by position
     *
     * @param x the x pos
     * @param y the y pos
     * @return the boss enum value
     */
    public static BossEnum findByPos(int x, int y) {
        return findByPos(getByGridIndex(x, y));
    }

}
