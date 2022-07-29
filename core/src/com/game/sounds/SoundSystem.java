package com.game.sounds;


import com.badlogic.gdx.audio.Sound;
import com.game.System;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        Iterator<Map.Entry<String, Boolean>> soundRequestIter = soundComponent.getSoundRequests().entrySet().iterator();
        while (soundRequestIter.hasNext()) {
            Map.Entry<String, Boolean> soundRequest = soundRequestIter.next();
            Sound sound = assetLoader.getAsset(soundRequest.getKey(), Sound.class);
            if (soundRequest.getValue() && !loopingSounds.containsKey(soundRequest.getKey())) {
                sound.loop();
                loopingSounds.put(soundRequest.getKey(), sound);
            } else {
                sound.play();
            }
            soundRequestIter.remove();
        }
        Iterator<String> stopLoopingSoundRequestIter = soundComponent.getStopLoopingSoundRequests().iterator();
        while (stopLoopingSoundRequestIter.hasNext()) {
            String stopLoopingSoundRequest = stopLoopingSoundRequestIter.next();
            Sound sound = loopingSounds.remove(stopLoopingSoundRequest);
            if (sound != null) {
                sound.stop();
            }
            stopLoopingSoundRequestIter.remove();
        }
        if (stopAllLoopingSounds) {
            loopingSounds.values().forEach(Sound::stop);
            loopingSounds.clear();
            stopAllLoopingSounds = false;
        }
    }

}
