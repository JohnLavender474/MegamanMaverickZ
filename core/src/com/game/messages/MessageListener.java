package com.game.messages;

public interface MessageListener {

    void listenToMessage(Object owner, Object message, float delta);

}
