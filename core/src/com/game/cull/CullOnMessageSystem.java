package com.game.cull;

import com.game.entities.Entity;
import com.game.GameContext2d;
import com.game.System;
import com.game.messages.Message;
import com.game.messages.MessageListener;

import java.util.*;

public class CullOnMessageSystem extends System implements MessageListener {

    private final List<Message> messages = new ArrayList<>();
    private final List<Message> messagesToAdd = new ArrayList<>();

    public CullOnMessageSystem(GameContext2d gameContext) {
        super(CullOnMessageComponent.class);
        gameContext.addMessageListener(this);
    }

    @Override
    public void listenToMessage(Message message) {
        if (isUpdating()) {
            messagesToAdd.add(message);
        } else {
            messages.add(message);
        }
    }

    @Override
    protected void preProcess(float delta) {
        messages.addAll(messagesToAdd);
        messagesToAdd.clear();
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        CullOnMessageComponent cullOnMessageComponent = entity.getComponent(CullOnMessageComponent.class);
        if (messages.stream().anyMatch(cullOnMessageComponent::isCullMessage)) {
            entity.setDead(true);
        }
    }

    @Override
    protected void postProcess(float delta) {
        messages.clear();
    }

}
