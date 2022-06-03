package com.mygdx.game;

public class ConstVals {

    public enum GameState {
        READY,
        PAUSED,
        RUNNING,
        IN_MENU,
        GAME_OVER,
        PLAYER_DEAD,
        LEVEL_COMPLETE,
        CAMERA_TRANSITION
    }

    public enum RenderingGround {
        UI,
        FOREGROUND,
        PLAYGROUND,
        BACKGROUND
    }

    public static class TextureAssets {
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
        public static final String HEALTH_WEAPON_STATS_TEXTURE_ATLAS = "sprites/SpriteSheets/HealthWeaponStats.txt";
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
