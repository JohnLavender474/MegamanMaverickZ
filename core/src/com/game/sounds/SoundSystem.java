package com.game.sounds;


import com.badlogic.gdx.audio.Sound;
import com.game.System;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;

import java.util.*;

public class SoundSystem extends System {

    private final IAssetLoader assetLoader;
    private final Map<String, Sound> loopingSounds = new HashMap<>();

    private boolean stopAllLoopingSounds;

    public SoundSystem(IAssetLoader assetLoader) {
        super(Set.of(SoundComponent.class));
        this.assetLoader = assetLoader;
    }

    public void requestToStopAllLoopingSounds() {
        stopAllLoopingSounds = true;
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        SoundComponent soundComponent = entity.getComponent(SoundComponent.class);
        Queue<SoundRequest> soundRequests = soundComponent.getSoundRequests();
        while (!soundRequests.isEmpty()) {
            SoundRequest soundRequest = soundRequests.poll();
            Sound sound = assetLoader.getAsset(soundRequest.request(), Sound.class);
            long id;
            if (soundRequest.loop() && !loopingSounds.containsKey(soundRequest.request())) {
                id = sound.loop();
                loopingSounds.put(soundRequest.request(), sound);
            } else {
                id = sound.play();
            }
            sound.setVolume(id, soundRequest.volume());
        }
        Queue<String> stopLoopingSoundRequests = soundComponent.getStopLoopingSoundRequests();
        while (!stopLoopingSoundRequests.isEmpty()) {
            String stopLoopingSoundRequest = stopLoopingSoundRequests.poll();
            Sound sound = loopingSounds.remove(stopLoopingSoundRequest);
            if (sound != null) {
                sound.stop();
            }
        }
        if (stopAllLoopingSounds) {
            loopingSounds.values().forEach(Sound::stop);
            loopingSounds.clear();
            stopAllLoopingSounds = false;
        }
    }

}
