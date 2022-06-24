package com.game.sound;

import com.game.Component;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * {@link Component} implementation for sound effects.
 */
@Getter
public class SoundComponent implements Component {

    private final Queue<SoundRequest> soundRequests = new ArrayDeque<>();
    private final Queue<String> loopsToStop = new ArrayDeque<>();

    public void request(SoundRequest soundRequest) {
        soundRequests.add(soundRequest);
    }

    public void stopLoop(String key) {
        loopsToStop.add(key);
    }

}
