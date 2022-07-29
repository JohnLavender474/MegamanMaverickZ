package com.game.sounds;

import com.game.Component;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter(AccessLevel.PACKAGE)
public class SoundComponent implements Component {

    private final Map<String, Boolean> soundRequests = new HashMap<>();
    private final Set<String> stopLoopingSoundRequests = new HashSet<>();

    public void requestSound(String sound, boolean loop) {
        soundRequests.put(sound, loop);
    }

    public void stopLoopingSound(String sound) {
        stopLoopingSoundRequests.add(sound);
    }

}
