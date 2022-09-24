package com.game.sounds;

import com.game.core.Component;
import com.game.constants.SoundAsset;
import com.game.utils.interfaces.Updatable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter(AccessLevel.PACKAGE)
public class SoundComponent extends Component {

    private final Queue<SoundRequest> soundRequests = new LinkedList<>();
    private final Queue<SoundAsset> stopLoopingSoundRequests = new LinkedList<>();

    private Updatable updatable;

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
