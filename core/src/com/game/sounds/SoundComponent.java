package com.game.sounds;

import com.game.Component;
import com.game.ConstVals;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;

import static com.game.ConstVals.*;

@Getter(AccessLevel.PACKAGE)
public class SoundComponent implements Component {

    private final Queue<SoundRequest> soundRequests = new LinkedList<>();
    private final Queue<SoundAsset> stopLoopingSoundRequests = new LinkedList<>();

    public void requestSound(SoundAsset sound) {
        requestSound(sound, false);
    }

    public void requestSound(SoundAsset sound, boolean loop) {
        requestSound(sound, loop, 1f);
    }

    public void requestSound(SoundAsset sound, boolean loop, float volume) {
        soundRequests.add(new SoundRequest(sound, loop, volume));
    }

    public void stopLoopingSound(SoundAsset sound) {
        stopLoopingSoundRequests.add(sound);
    }

}
