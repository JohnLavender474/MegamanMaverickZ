package com.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

        GAME_ROOMS("GameRooms"), ENEMY_SPAWNS("EnemySpawns"), PLAYER_SPAWNS("PlayerSpawns"), STATIC_BLOCKS(
                "StaticBlocks"), DEATH_BLOCKS("DeathBlocks");

        private final String layerName;

    }

    public enum Events {
        PLAYER_DEAD,
        LEVEL_FINISHED
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
        public static final float VIEW_WIDTH = 16f;
        public static final float VIEW_HEIGHT = 14f;
        public static final float PPM = 16f;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum TextureAsset {

        MET_TEXTURE_ATLAS("sprites/SpriteSheets/Met.txt"),
        FIRE_TEXTURE_ATLAS("sprites/SpriteSheets/Fire.txt"),
        ITEMS_TEXTURE_ATLAS("sprites/SpriteSheets/Items.txt"),
        ENEMIES_TEXTURE_ATLAS("sprites/SpriteSheets/Enemies.txt"),
        OBJECTS_TEXTURE_ATLAS("sprites/SpriteSheets/Objects.txt"),
        MEGAMAN_TEXTURE_ATLAS("sprites/SpriteSheets/Megaman.txt"),
        BITS_ATLAS("sprites/SpriteSheets/HealthAndWeaponBits.txt"),
        CHARGE_ORBS_TEXTURE_ATLAS("sprites/SpriteSheets/ChargeOrbs.txt"),
        DECORATIONS_TEXTURE_ATLAS("sprites/SpriteSheets/Decorations.txt"),
        MEGAMAN_FIRE_TEXTURE_ATLAS("sprites/SpriteSheets/MegamanFire.txt"),
        CUSTOM_TILES_TEXTURE_ATLAS("sprites/SpriteSheets/CustomTiles.txt"),
        BACKGROUNDS_1_TEXTURE_ATLAS("sprites/SpriteSheets/Backgrounds1.txt"),
        ELECTRIC_BALL_TEXTURE_ATLAS("sprites/SpriteSheets/ElectricBall.txt"),
        MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS("sprites/SpriteSheets/MegamanChargedShot.txt");

        private final String src;

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum MusicAsset {

        MMZ_NEO_ARCADIA_MUSIC("music/MMZ_NeoArcadia.mp3"),
        STAGE_SELECT_MM3_MUSIC("music/StageSelectMM3.mp3"),
        MMX3_INTRO_STAGE_MUSIC("music/MMX3_IntroStage.ogg"),
        XENOBLADE_GAUR_PLAINS_MUSIC("music/Xenoblade_GaurPlains.ogg"),
        MMX_LEVEL_SELECT_SCREEN_MUSIC("music/MMX_LevelSelectScreen.ogg");

        private final String src;

    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum SoundAsset {

        DINK_SOUND("sounds/Dink.mp3"),
        THUMP_SOUND("sounds/Thump.mp3"),
        WHOOSH_SOUND("sounds/Whoosh.mp3"),
        PAUSE_SOUND("sounds/PauseMenu.mp3"),
        EXPLOSION_SOUND("sounds/Explosion.mp3"),
        ENERGY_FILL_SOUND("sounds/EnergyFill.mp3"),
        SELECT_PING_SOUND("sounds/SelectPing.mp3"),
        ENEMY_BULLET_SOUND("sounds/EnemyShoot.mp3"),
        ENEMY_DAMAGE_SOUND("sounds/EnemyDamage.mp3"),
        MEGAMAN_LAND_SOUND("sounds/MegamanLand.mp3"),
        ACID_SOUND("sounds/Megaman_2_Sounds/acid.wav"),
        MEGAMAN_DAMAGE_SOUND("sounds/MegamanDamage.mp3"),
        MEGAMAN_DEFEAT_SOUND("sounds/MegamanDefeat.mp3"),
        CURSOR_MOVE_BLOOP_SOUND("sounds/CursorMoveBloop.mp3"),
        MEGA_BUSTER_CHARGING_SOUND("sounds/MegaBusterCharging.mp3"),
        AIR_SHOOTER_SOUND("sounds/Megaman_2_Sounds/air_shooter.wav"),
        ATOMIC_FIRE_SOUND("sounds/Megaman_2_Sounds/atomic_fire.wav"),
        CRASH_BOMBER_SOUND("sounds/Megaman_2_Sounds/crash_bomber.wav"),
        MEGA_BUSTER_BULLET_SHOT_SOUND("sounds/MegaBusterBulletShot.mp3"),
        MEGA_BUSTER_CHARGED_SHOT_SOUND("sounds/MegaBusterChargedShot.mp3");

        private final String src;

    }

}
