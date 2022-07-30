package com.game.sounds;

import com.game.Component;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;

@Getter(AccessLevel.PACKAGE)
public class SoundComponent implements Component {

    private final Queue<SoundRequest> soundRequests = new LinkedList<>();
    private final Queue<String> stopLoopingSoundRequests = new LinkedList<>();

    public void requestSound(String sound) {
        requestSound(sound, false);
    }

    public void requestSound(String sound, boolean loop) {
        requestSound(sound, loop, 1f);
    }

    public void requestSound(String sound, boolean loop, float volume) {
        soundRequests.add(new SoundRequest(sound, loop, volume));
    }

    public void stopLoopingSound(String sound) {
        stopLoopingSoundRequests.add(sound);
    }

}
