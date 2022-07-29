package com.game;

import com.badlogic.gdx.math.Vector2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ConstVals {

    public enum GameScreen {
        TEST_LEVEL_1, MAIN_MENU, PASSWORD, SETTINGS, BOSS_SELECT
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

    public static class TextureAssets {
        public static final String CUSTOM_TILES_TEXTURE_ATLAS = "sprites/SpriteSheets/CustomTiles.txt";
        public static final String OBJECTS_TEXTURE_ATLAS = "sprites/SpriteSheets/Objects.txt";
        public static final String MET_TEXTURE_ATLAS = "sprites/SpriteSheets/Met.txt";
        public static final String ENEMIES_TEXTURE_ATLAS = "sprites/SpriteSheets/Enemies.txt";
        public static final String ITEMS_TEXTURE_ATLAS = "sprites/SpriteSheets/Items.txt";
        public static final String BACKGROUNDS_1_TEXTURE_ATLAS = "sprites/SpriteSheets/Backgrounds1.txt";
        public static final String MEGAMAN_TEXTURE_ATLAS = "sprites/SpriteSheets/Megaman.txt";
        public static final String CHARGE_ORBS_TEXTURE_ATLAS = "sprites/SpriteSheets/ChargeOrbs.txt";
        public static final String MEGAMAN_CHARGED_SHOT_TEXTURE_ATLAS = "sprites/SpriteSheets/MegamanChargedShot.txt";
        public static final String ELECTRIC_BALL_TEXTURE_ATLAS = "sprites/SpriteSheets/ElectricBall.txt";
        public static final String DECORATIONS_TEXTURE_ATLAS = "sprites/SpriteSheets/Decorations.txt";
        public static final String BITS_ATLAS = "sprites/SpriteSheets/HealthAndWeaponBits.txt";
    }

    public static class MusicAssets {
        public static final String MMX3_INTRO_STAGE_MUSIC = "music/MMX3_IntroStage.ogg";
        public static final String XENOBLADE_GAUR_PLAINS_MUSIC = "music/Xenoblade_GaurPlains.ogg";
        public static final String MMZ_NEO_ARCADIA_MUSIC = "music/MMZ_NeoArcadia.mp3";
        public static final String MMX_LEVEL_SELECT_SCREEN_MUSIC = "music/MMX_LevelSelectScreen.ogg";
        public static final String STAGE_SELECT_MM3_MUSIC = "music/StageSelectMM3.mp3";
    }

    public static class SoundAssets {
        public static final String CURSOR_MOVE_BLOOP_SOUND = "sounds/CursorMoveBloop.mp3";
        public static final String MARIO_JUMP_SOUND = "sounds/MarioJump.mp3";
        public static final String SELECT_PING_SOUND = "sounds/SelectPing.mp3";
        public static final String DINK_SOUND = "sounds/Dink.mp3";
        public static final String EXPLOSION_SOUND = "sounds/Explosion.mp3";
        public static final String ENEMY_DAMAGE_SOUND = "sounds/EnemyDamage.mp3";
        public static final String ENEMY_BULLET_SOUND = "sounds/EnemyShoot.mp3";
        public static final String MEGA_BUSTER_BULLET_SHOT_SOUND = "sounds/MegaBusterBulletShot.mp3";
        public static final String MEGA_BUSTER_CHARGING_SOUND = "sounds/MegaBusterCharging.mp3";
        public static final String MEGA_BUSTER_CHARGED_SHOT_SOUND = "sounds/MegaBusterChargedShot.mp3";
        public static final String MEGAMAN_DAMAGE_SOUND = "sounds/MegamanDamage.mp3";
        public static final String MEGAMAN_DEFEAT_SOUND = "sounds/MegamanDefeat.mp3";
        public static final String MEGAMAN_LAND_SOUND = "sounds/MegamanLand.mp3";
        public static final String WHOOSH_SOUND = "sounds/Whoosh.mp3";
        public static final String THUMP_SOUND = "sounds/Thump.mp3";
        public static final String ENERGY_FILL_SOUND = "sounds/EnergyFill.mp3";
        public static final String PAUSE_SOUND = "sounds/PauseMenu.mp3";
    }

}
