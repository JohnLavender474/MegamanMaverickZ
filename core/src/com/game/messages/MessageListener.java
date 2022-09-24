package com.game.messages;

public interface MessageListener {

    void listenToMessage(String key, Object owner, Object message, float delta);

}
