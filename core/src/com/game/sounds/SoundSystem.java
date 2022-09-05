package com.game.sounds;

import com.badlogic.gdx.audio.Sound;
import com.game.core.Entity;
import com.game.core.GameContext2d;
import com.game.core.System;

import java.util.*;

import static com.game.core.ConstVals.*;

public class SoundSystem extends System {

    private final GameContext2d gameContext;
    private final Map<SoundAsset, Sound> loopingSounds = new HashMap<>();

    private boolean stopAllLoopingSounds;

    public SoundSystem(GameContext2d gameContext) {
        super(SoundComponent.class);
        this.gameContext = gameContext;
    }

    public void requestToStopAllLoopingSounds() {
        stopAllLoopingSounds = true;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        SoundComponent soundComponent = entity.getComponent(SoundComponent.class);
        Queue<SoundRequest> soundRequests = soundComponent.getSoundRequests();
        while (!soundRequests.isEmpty()) {
            SoundRequest soundRequest = soundRequests.poll();
            Sound sound = gameContext.getAsset(soundRequest.request().getSrc(), Sound.class);
            if (soundRequest.loop() && !loopingSounds.containsKey(soundRequest.request())) {
                gameContext.loopSound(sound);
                loopingSounds.put(soundRequest.request(), sound);
            } else {
                gameContext.playSound(sound);
            }
        }
        Queue<SoundAsset> stopLoopingSoundRequests = soundComponent.getStopLoopingSoundRequests();
        while (!stopLoopingSoundRequests.isEmpty()) {
            SoundAsset stopLoopingSoundRequest = stopLoopingSoundRequests.poll();
            Sound sound = loopingSounds.remove(stopLoopingSoundRequest);
            if (sound != null) {
                gameContext.stopSound(sound);
            }
        }
        if (stopAllLoopingSounds) {
            loopingSounds.values().forEach(gameContext::stopSound);
            loopingSounds.clear();
            stopAllLoopingSounds = false;
        }
    }

}
