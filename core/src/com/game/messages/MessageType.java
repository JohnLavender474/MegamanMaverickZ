package com.game.messages;

public enum MessageType {
    // Player state
    PLAYER_SPAWN,
    PLAYER_DEAD,

    // Level state
    LEVEL_PAUSED,
    LEVEL_UNPAUSED,
    LEVEL_FINISHED,

    // Sound
    SOUND_VOLUME_CHANGE,

    // Game room trans
    NEXT_GAME_ROOM_REQUEST,
    BEGIN_GAME_ROOM_TRANS,
    CONTINUE_GAME_ROOM_TRANS,
    END_GAME_ROOM_TRANS,

    // Room gate
    TOUCH_GATE,
    GATE_INIT_OPENING,
    GATE_FINISH_OPENING,
    GATE_INIT_CLOSING,
    GATE_FINISH_CLOSING,

    // Rooms
    ENTER_BOSS_ROOM

}
