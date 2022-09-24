package com.game.cull;

import com.game.Entity;
import com.game.GameContext2d;
import com.game.System;
import com.game.events.Event;
import com.game.events.EventListener;

import static com.game.events.EventType.PLAYER_DEAD;

public class CullOnPlayerDeathSystem extends System implements EventListener {

    private final GameContext2d gameContext;

    private boolean cull;

    public CullOnPlayerDeathSystem(GameContext2d gameContext) {
        super(CullOnPlayerDeathComponent.class);
        gameContext.addEventListener(this);
        this.gameContext = gameContext;
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        if (cull) {
            gameContext.removeEntity(entity);
        }
    }

    @Override
    protected void postProcess(float delta) {
        cull = false;
    }

    @Override
    public void listenToEvent(Event event, float delta) {
        if (event.is(PLAYER_DEAD)) {
            cull = true;
        }
    }

}
