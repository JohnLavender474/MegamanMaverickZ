package com.game.messages;

/** Functional interface allowing object to listen to messages. */
public interface MessageListener {

    /**
     * Listen to message.
     *
     * @param message the message
     */
    void listenToMessage(Message message);

}
