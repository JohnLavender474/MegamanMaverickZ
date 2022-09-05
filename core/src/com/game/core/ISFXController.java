package com.game.core;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public interface ISFXController {
    
    void setSoundEffectsVolume(int volume);
    
    int getSoundEffectsVolume();
    
    void setMusicVolume(int volume);
    
    int getMusicVolume();

    default void playSound(Sound sound) {
        sound.play(getSoundEffectsVolume() / 10f);
    }

    default void loopSound(Sound sound) {
        sound.loop(getSoundEffectsVolume() / 10f);
    }

    default void stopSound(Sound sound) {
        sound.stop();
    }

    default void playMusic(Music music, boolean loop) {
        music.setLooping(loop);
        music.setVolume(getMusicVolume() / 10f);
        music.play();
    }

    default void stopMusic(Music music) {
        music.stop();
    }
    
}
