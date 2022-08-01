package com.game.tests.core;

import com.game.Message;
import com.game.MessageListener;
import com.game.core.IMessageDispatcher;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class TestMessageDispatcher implements IMessageDispatcher {

    private final Set<MessageListener> messageListeners = new HashSet<>();
    private final Queue<Message> messageQueue = new ArrayDeque<>();

    @Override
    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    @Override
    public void addMessage(Message message) {
        messageQueue.add(message);
    }

    @Override
    public void updateMessageDispatcher(float delta) {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.poll();
            messageListeners.forEach(listener -> listener.listenToMessage(message.owner(), message.contents(), delta));
        }
    }

}
