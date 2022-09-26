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

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

import static com.game.levels.BossIntroScreen.BOSS_DROP_DOWN;
import static com.game.utils.enums.Position.BOTTOM_LEFT;
import static com.game.utils.enums.Position.getByGridIndex;
import static lombok.AccessLevel.PRIVATE;

/**
 * DESCRIPTIONS:
 * -Timber Woman:
 * <p>
 * Timber Woman beats Histrionic Man,
 * Histrionic Man beats Sales Man,
 * Sales Man beats Maniac Man,
 * Maniac Man beats Lighter Man,
 * Lighter Man beats Weed Man,
 * Weed Man beats Beacon Man,
 * Beacon Man beats Tsunami Man,
 * Tsunami Man beats Timber Woman
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BossEnum {

    TIMBER_WOMAN("Timber Woman", BOTTOM_LEFT, TextureAsset.TIMBER_WOMAN.getSrc(),
            GameScreen.TIMBER_WOMAN) {

        @Override
        public Vector2 getSize() {
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
            return new ArrayDeque<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("JustLand"), new Timer(.6f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1.75f)));
                add(KeyValuePair.of(anims.get("AboutToSwing"), new Timer(.375f)));
                add(KeyValuePair.of(anims.get("Swing"), new Timer(4f)));
            }};
        }

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
    },
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

    public abstract String getBio();

    public abstract Vector2 getSize();

    public abstract Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas);

    public abstract Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas);

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

    public static BossEnum findByPos(int x, int y) {
        return findByPos(getByGridIndex(x, y));
    }

}
