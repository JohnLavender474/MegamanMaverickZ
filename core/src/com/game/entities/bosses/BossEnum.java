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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static com.game.assets.MusicAsset.XENOBLADE_GAUR_PLAINS_MUSIC;
import static com.game.levels.LevelIntroScreen.BOSS_DROP_DOWN;
import static com.game.utils.enums.Position.*;
import static lombok.AccessLevel.PRIVATE;

/**
 * The game bosses, a motley bunch if you ask me.
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum BossEnum {

    // Timber Woman
    TIMBER_WOMAN("Timber Woman", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            TOP_LEFT, TextureAsset.TIMBER_WOMAN.getSrc(), GameScreen.TIMBER_WOMAN) {

        @Override
        public String getBio() {
            return "Originally named 'Timbre Woman', she was to be \n" +
                    "the prettiest singer in the world. But one of the \n" +
                    "senior programmers messed up and accidentally typed \n" +
                    "'Timber' instead of 'Timbre', and as a result she \n" +
                    "became the world's strongest lumberjack. She keeps \n" +
                    "water off her axe adamantly and has beheaded over \n" +
                    "a thousand chickens in her lifetime.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
                put("JustLand", new TimedAnimation(textureAtlas.findRegion("JustLand"), 6, .1f, false));
                put("AboutToSwing", new TimedAnimation(textureAtlas.findRegion("AboutToSwing"), 3, .15f, false));
                put("Swing", new TimedAnimation(textureAtlas.findRegion("Swing"), 4, .15f, false));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump"), 6, BOSS_DROP_DOWN / 12));
            }};
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
    DISTRIBUTOR_MAN("Distributor Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            TOP_CENTER, TextureAsset.DISTRIBUTOR_MAN.getSrc(), GameScreen.DISTRIBUTOR_MAN) {

        @Override
        public String getBio() {
            return "Distributor Man designs electrical components \n" +
                    "that require advanced timed signals. His \n" +
                    "favorite hobby is using his distribution \n" +
                    "power to spread awareness about the energetic \n" +
                    "benefits of good exercise and a healthy diet. \n" +
                    "He is best friends with Blunt Man even though \n" +
                    "too much of Blunt's hazy demeanor sometimes \n" +
                    "disorientates his calibrations.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(1.85f, 1.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("JumpShock", new TimedAnimation(textureAtlas.findRegion("JumpShock"), 2, .15f));
                put("JustLand", new TimedAnimation(textureAtlas.findRegion("JustLand"), 2, .15f, false));
                put("Shock", new TimedAnimation(textureAtlas.findRegion("Shock"), 2, .15f));
                put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 2, .15f));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("JustLand"), new Timer(.3f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(2.15f)));
                add(KeyValuePair.of(anims.get("Shock"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1.7f)));
            }};
        }

    },

    // Roaster Man
    ROASTER_MAN("Roaster Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            TOP_RIGHT, TextureAsset.ROASTER_MAN.getSrc(), GameScreen.ROASTER_MAN) {

        @Override
        public String getBio() {
            return "Roaster Man was designed as a chicken chef \n" +
                    "but has since renounced meat-eating, preferring \n" +
                    "instead to cook corn cuisines. He believes \n" +
                    "microwaves are a blight on culinary arts. He \n" +
                    "is an ardent Hindu and is described as being \n" +
                    "scrupulous and brooding. He greatly enjoys \n" +
                    "pecking things apart, getting to the kernel \n" +
                    "of things, and roasting others in debates.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Aim", new TimedAnimation(textureAtlas.findRegion("Aim")));
                put("CoolPose", new TimedAnimation(textureAtlas.findRegion("CoolPose"), 2, .3f, false));
                put("FallingWithStyle", new TimedAnimation(textureAtlas.findRegion("FallingWithStyle"), 2, .05f));
                put("FlyFlap", new TimedAnimation(textureAtlas.findRegion("FlyFlap"), 2, .2f));
                put("RetractWings", new TimedAnimation(textureAtlas.findRegion("RetractWings")));
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
                put("StandFlap", new TimedAnimation(textureAtlas.findRegion("StandFlap"), 2, .2f));
                put("SuaveCombSweep", new TimedAnimation(textureAtlas.findRegion("SuaveCombSweep"), 2, .2f));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("FlyFlap"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("StandFlap"), new Timer(1.5f)));
                add(KeyValuePair.of(anims.get("RetractWings"), new Timer(.2f)));
                add(KeyValuePair.of(anims.get("SuaveCombSweep"), new Timer(.8f)));
                add(KeyValuePair.of(anims.get("CoolPose"), new Timer(4.25f)));
            }};
        }

    },

    // Mister Man
    MISTER_MAN("Mister Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            CENTER_LEFT, TextureAsset.MISTER_MAN.getSrc(), GameScreen.MISTER_MAN) {

        @Override
        public String getBio() {
            return "Pump Man's brother, Mister Man waters delicate \n" +
                    "plants for a living. He loves misty mornings, \n" +
                    "fine scents, and isn't afraid to pull the trigger \n" +
                    "on spontaneous ideas. He believes that having a \n" +
                    "spray-and-pray attitude is the best thing one can \n" +
                    "do to handle the chaos and disappointments in life. \n" +
                    "Oh, and he is also very afraid of electricity.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(3.25f, 2.85f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("Flex", new TimedAnimation(textureAtlas.findRegion("Flex"), 2, .2f));
                put("Electrocuted", new TimedAnimation(textureAtlas.findRegion("Electrocuted"), 2, .1f));
                put("Squirt", new TimedAnimation(textureAtlas.findRegion("Squirt"), 2, .1f));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("Flex"), new Timer(1.5f)));
                add(KeyValuePair.of(anims.get("Squirt"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(3.25f)));
            }};
        }

    },

    // Blunt Man
    BLUNT_MAN("Blunt Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            CENTER_RIGHT, TextureAsset.BLUNT_MAN.getSrc(), GameScreen.BLUNT_MAN) {

        @Override
        public String getBio() {
            return "Blunt Man is designed to be an activist for the \n" +
                    "legalization of cannabis and recreational drugs. \n" +
                    "He likes to take things easy but isn't afraid of \n" +
                    "being blunt about his opinions. He has a joint \n" +
                    "venture in a recreational drug research company \n" +
                    "and spends his fortunes on profound trips. He not \n" +
                    "a fan of getting nuked.One criticism of him is he \n" +
                    "lobbies against independent distributors.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(1.65f, 1.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Damaged", new TimedAnimation(textureAtlas.findRegion("Damaged"), 2, .1f));
                put("Flaming", new TimedAnimation(textureAtlas.findRegion("Flaming"), 2, .15f));
                put("Flex", new TimedAnimation(textureAtlas.findRegion("Flex"), 2, .2f));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("Slide", new TimedAnimation(textureAtlas.findRegion("Slide")));
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("Flex"), new Timer(1.5f)));
                add(KeyValuePair.of(anims.get("Slide"), new Timer(.75f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(3.5f)));
            }};
        }

    },

    // Nuke Man
    NUKE_MAN("Nuke Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            BOTTOM_LEFT, TextureAsset.NUKE_MAN.getSrc(), GameScreen.NUKE_MAN) {

        @Override
        public String getBio() {
            return "Designed as a nuclear arms expert, Nuke Man is \n" +
                    "able to build a nuclear bomb out of common\n" +
                    "household items. Deemed too dangerous to be \n" +
                    "kept alive, he is now a fugitive on the run. \n" +
                    "He has vowed revenge on the world and must be \n" +
                    "stopped at once! There's few things he hates \n" +
                    "more than hippies, pacifists, and pot smokers.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(2.85f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("Attack", new TimedAnimation(textureAtlas.findRegion("Attack")));
                put("BendKnees", new TimedAnimation(textureAtlas.findRegion("BendKnees")));
                put("Charge", new TimedAnimation(textureAtlas.findRegion("Charge"), 2, .15f));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("Charge"), new Timer(1.25f)));
                add(KeyValuePair.of(anims.get("Attack"), new Timer(.25f)));
                add(KeyValuePair.of(anims.get("BendKnees"), new Timer(.75f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(3.5f)));
            }};
        }

    },

    // Fridge Man
    FRIDGE_MAN("Fridge Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            BOTTOM_CENTER, TextureAsset.FRIDGE_MAN.getSrc(), GameScreen.FRIDGE_MAN) {

        @Override
        public String getBio() {
            return "Fridge Man really enjoys putting leftovers inside \n" +
                    "himself. He specializes in following you around and \n" +
                    "storing your leftovers. He is made of a very strong \n" +
                    "metal that is resistant to nuclear blasts, a fact \n" +
                    "he loves to boast about. There's nothing he hates \n" +
                    "more than microwaving because it implies taking \n" +
                    "food out of him which leaves him feeling empty.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(4.5f, 4f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("GiveTheHand", new TimedAnimation(textureAtlas.findRegion("GiveTheHand")));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("JumpOpenFreezer", new TimedAnimation(textureAtlas.findRegion("JumpOpenFreezer")));
                put("StandLookDown", new TimedAnimation(textureAtlas.findRegion("StandLookDown"),
                        new float[]{1.5f, .15f}));
                put("StandLookUp", new TimedAnimation(textureAtlas.findRegion("StandLookUp"),
                        new float[]{1.5f, .15f}));
                put("StandOpenFridge", new TimedAnimation(textureAtlas.findRegion("StandOpenFridge")));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("StandLookUp"), new Timer(1.25f)));
                add(KeyValuePair.of(anims.get("StandLookDown"), new Timer(1f)));
                add(KeyValuePair.of(anims.get("GiveTheHand"), new Timer(.75f)));
                add(KeyValuePair.of(anims.get("StandOpenFridge"), new Timer(3.75f)));
            }};
        }

    },

    // Microwave Man
    MICROWAVE_MAN("Microwave Man", "tiledmaps/tmx/Test1.tmx", XENOBLADE_GAUR_PLAINS_MUSIC.getSrc(),
            BOTTOM_RIGHT, TextureAsset.MICROWAVE_MAN.getSrc(), GameScreen.MICROWAVE_MAN) {

        @Override
        public String getBio() {
            return "Microwave Man is a microwave. It's a fate that \n" +
                    "has lead him to question his life. Although at \n" +
                    "first he was severely depressed about being a \n" +
                    "microwave, he soon accepted it as a fact of life \n" +
                    "and nowadays tours giving motivational speeches. \n" +
                    "Although he's skilled at microwaving, he's too \n" +
                    "small for things like whole chickens to fit into.";
        }

        @Override
        public Vector2 getSpriteSize() {
            return new Vector2(2.85f, 2.5f);
        }

        @Override
        public Map<String, TimedAnimation> getAnimations(TextureAtlas textureAtlas) {
            return new HashMap<>() {{
                put("HeadlessJump", new TimedAnimation(textureAtlas.findRegion("HeadlessJump")));
                put("HeadlessOpenDoor", new TimedAnimation(textureAtlas.findRegion("HeadlessOpenDoor")));
                put("HeadlessShoot", new TimedAnimation(textureAtlas.findRegion("HeadlessShoot")));
                put("HeadlessStand", new TimedAnimation(textureAtlas.findRegion("HeadlessStand")));
                put("Jump", new TimedAnimation(textureAtlas.findRegion("Jump")));
                put("OpenDoor", new TimedAnimation(textureAtlas.findRegion("OpenDoor")));
                put("Shoot", new TimedAnimation(textureAtlas.findRegion("Shoot")));
                put("Stand", new TimedAnimation(textureAtlas.findRegion("Stand"), new float[]{1.5f, .15f}));
            }};
        }

        @Override
        public Queue<KeyValuePair<TimedAnimation, Timer>> getIntroAnimsQ(TextureAtlas textureAtlas) {
            Map<String, TimedAnimation> anims = getAnimations(textureAtlas);
            return new LinkedList<>() {{
                add(KeyValuePair.of(anims.get("Jump"), new Timer(BOSS_DROP_DOWN)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1.5f)));
                add(KeyValuePair.of(anims.get("Shoot"), new Timer(1.5f)));
                add(KeyValuePair.of(anims.get("OpenDoor"), new Timer(2.5f)));
                add(KeyValuePair.of(anims.get("Stand"), new Timer(1.25f)));
            }};
        }

    };

    private final String bossName;
    private final String tmxSrc;
    private final String musicSrc;
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
        for (BossEnum boss : values()) {
            if (name.equals(boss.getBossName())) {
                return boss;
            }
        }
        return null;
    }

    /**
     * Find boss enum value by position.
     *
     * @param position the position
     * @return the boss enum value
     */
    public static BossEnum findByPos(Position position) {
        for (BossEnum boss : values()) {
            if (boss.getPosition().equals(position)) {
                return boss;
            }
        }
        return null;
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
