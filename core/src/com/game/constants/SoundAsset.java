package com.game.constants;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum SoundAsset {

    DINK_SOUND("Dink.mp3"), THUMP_SOUND("Thump.mp3"), WHOOSH_SOUND("Whoosh.mp3"), PAUSE_SOUND("PauseMenu.mp3"),
    EXPLOSION_SOUND("Explosion.mp3"), BEAM_OUT_SOUND("TeleportOut.mp3"), ENERGY_FILL_SOUND("EnergyFill.mp3"),
    SELECT_PING_SOUND("SelectPing.mp3"), ENEMY_BULLET_SOUND("EnemyShoot.mp3"), ENEMY_DAMAGE_SOUND("EnemyDamage.mp3"),
    MEGAMAN_LAND_SOUND("MegamanLand.mp3"), ACID_SOUND("Megaman_2_Sounds/acid.wav"),
    MEGAMAN_DAMAGE_SOUND("MegamanDamage.mp3"), MEGAMAN_DEFEAT_SOUND("MegamanDefeat.mp3"),
    CURSOR_MOVE_BLOOP_SOUND("CursorMoveBloop.mp3"), MEGA_BUSTER_CHARGING_SOUND("MegaBusterCharging.mp3"),
    AIR_SHOOTER_SOUND("Megaman_2_Sounds/air_shooter.wav"), ATOMIC_FIRE_SOUND("Megaman_2_Sounds/atomic_fire.wav"),
    CRASH_BOMBER_SOUND("Megaman_2_Sounds/crash_bomber.wav"), MEGA_BUSTER_BULLET_SHOT_SOUND("MegaBusterBulletShot.mp3"),
    MEGA_BUSTER_CHARGED_SHOT_SOUND("MegaBusterChargedShot.mp3");

    private static final String prefix = "sounds/";

    private final String src;

    public String getSrc() {
        return prefix + src;
    }

}
