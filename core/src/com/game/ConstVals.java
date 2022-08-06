package com.game;

import com.badlogic.gdx.math.Vector2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ConstVals {

    public enum GameScreen {
        TEST_LEVEL_1, TEST_LEVEL_2, MAIN_MENU, PASSWORD, SETTINGS, BOSS_SELECT
    }

    public enum RenderingGround {
        UI, PLAYGROUND, BACKGROUND
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum LevelTiledMapLayer {

        GAME_ROOMS("GameRooms"), ENEMY_SPAWNS("EnemySpawns"), PLAYER_SPAWNS("PlayerSpawns"),
        STATIC_BLOCKS("StaticBlocks"), DEATH_BLOCKS("DeathBlocks");

        private final String layerName;

    }

    public enum Events {
        PLAYER_DEAD, LEVEL_FINISHED
    }

    public static class MegamanVals {
        public static final String MEGAMAN_STATS = "MegamanStats";
        public static final Integer MEGAMAN_MAX_HEALTH = 30;
    }

    public static class WorldVals {
        public static final Vector2 AIR_RESISTANCE = new Vector2(1.035f, 1.025f);
        public static final float FIXED_TIME_STEP = 1f / 150f;
    }

    public static class ViewVals {
        public static final float VIEW_HEIGHT = 14f;
        public static final float VIEW_WIDTH = 16f;
        public static final float PPM = 16f;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum TextureAsset {

        MET_TEXTURE_ATLAS("Met.txt"),
        FIRE_TEXTURE_ATLAS("Fire.txt"),
        ITEMS_TEXTURE_ATLAS("Items.txt"),
        ENEMIES_TEXTURE_ATLAS("Enemies.txt"),
        OBJECTS_TEXTURE_ATLAS("Objects.txt"),
        MEGAMAN_TEXTURE_ATLAS("Megaman.txt"),
        BITS_ATLAS("HealthAndWeaponBits.txt"),
        CHARGE_ORBS_TEXTURE_ATLAS("ChargeOrbs.txt"),
        DECORATIONS_TEXTURE_ATLAS("Decorations.txt"),
        MEGAMAN_FIRE_TEXTURE_ATLAS("MegamanFire.txt"),
        CUSTOM_TILES_TEXTURE_ATLAS("CustomTiles.txt"),
        MEGAMAN_FACES_TEXTURE_ATLAS("MegamanFaces.txt"),
        BACKGROUNDS_1_TEXTURE_ATLAS("Backgrounds1.txt"),
        ELECTRIC_BALL_TEXTURE_ATLAS("ElectricBall.txt"),
        MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS("MegamanChargedShot.txt");

        private static final String prefix = "sprites/SpriteSheets/";

        private final String src;

        public String getSrc() {
            return prefix + src;
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum MusicAsset {

        MMZ_NEO_ARCADIA_MUSIC("MMZ_NeoArcadia.mp3"),
        STAGE_SELECT_MM3_MUSIC("StageSelectMM3.mp3"),
        MMX3_INTRO_STAGE_MUSIC("MMX3_IntroStage.ogg"),
        XENOBLADE_GAUR_PLAINS_MUSIC("Xenoblade_GaurPlains.ogg"),
        MMX_LEVEL_SELECT_SCREEN_MUSIC("MMX_LevelSelectScreen.ogg");

        private static final String prefix = "music/";

        private final String src;

        public String getSrc() {
            return prefix + src;
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum SoundAsset {

        DINK_SOUND("Dink.mp3"),
        THUMP_SOUND("Thump.mp3"),
        WHOOSH_SOUND("Whoosh.mp3"),
        PAUSE_SOUND("PauseMenu.mp3"),
        EXPLOSION_SOUND("Explosion.mp3"),
        ENERGY_FILL_SOUND("EnergyFill.mp3"),
        SELECT_PING_SOUND("SelectPing.mp3"),
        ENEMY_BULLET_SOUND("EnemyShoot.mp3"),
        ENEMY_DAMAGE_SOUND("EnemyDamage.mp3"),
        MEGAMAN_LAND_SOUND("MegamanLand.mp3"),
        ACID_SOUND("Megaman_2_Sounds/acid.wav"),
        MEGAMAN_DAMAGE_SOUND("MegamanDamage.mp3"),
        MEGAMAN_DEFEAT_SOUND("MegamanDefeat.mp3"),
        CURSOR_MOVE_BLOOP_SOUND("CursorMoveBloop.mp3"),
        MEGA_BUSTER_CHARGING_SOUND("MegaBusterCharging.mp3"),
        AIR_SHOOTER_SOUND("Megaman_2_Sounds/air_shooter.wav"),
        ATOMIC_FIRE_SOUND("Megaman_2_Sounds/atomic_fire.wav"),
        CRASH_BOMBER_SOUND("Megaman_2_Sounds/crash_bomber.wav"),
        MEGA_BUSTER_BULLET_SHOT_SOUND("MegaBusterBulletShot.mp3"),
        MEGA_BUSTER_CHARGED_SHOT_SOUND("MegaBusterChargedShot.mp3");

        private static final String prefix = "sounds/";

        private final String src;

        public String getSrc() {
            return prefix + src;
        }

    }

}
