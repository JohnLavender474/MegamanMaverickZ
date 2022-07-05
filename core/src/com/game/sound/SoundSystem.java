package com.game.sound;

import com.badlogic.gdx.audio.Sound;
import com.game.Component;
import com.game.System;
import com.game.core.IAssetLoader;
import com.game.core.IEntity;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor
public class SoundSystem extends System {

    private final IAssetLoader assetLoader;
    private final Map<String, Sound> loopingSounds = new HashMap<>();

    @Override
    public Set<Class<? extends Component>> getComponentMask() {
        return Set.of(SoundComponent.class);
    }

    @Override
    protected void processEntity(IEntity entity, float delta) {
        SoundComponent soundComponent = entity.getComponent(SoundComponent.class);
        // Handle requests to play sounds
        Queue<SoundRequest> soundRequests = soundComponent.getSoundRequests();
        while (!soundRequests.isEmpty()) {
            SoundRequest soundRequest = soundRequests.poll();
            if (loopingSounds.containsKey(soundRequest.key())) {
                continue;
            }
            Float volume = soundRequest.volume().getAsDecimal();
            Sound sound = assetLoader.getAsset(soundRequest.key(), Sound.class);
            if (soundRequest.looping()) {
                sound.loop(volume);
                loopingSounds.put(soundRequest.key(), sound);
            } else {
                sound.play(volume);
            }
        }
        // Handle requests to stop looping sounds
        Queue<String> loopsToStop = soundComponent.getLoopsToStop();
        while (!loopsToStop.isEmpty()) {
            String key = loopsToStop.poll();
            Sound sound = loopingSounds.get(key);
            if (sound != null) {
                sound.stop();
            }
        }
    }

}
