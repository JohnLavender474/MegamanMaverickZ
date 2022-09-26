package com.game.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class Message {

    @Getter
    private final MessageType messageType;
    private final Map<String, Object> content;

    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.content = new HashMap<>();
    }

    public Message(MessageType messageType, String key, Object object) {
        this(messageType);
        content.put(key, object);
    }

    public void putContent(String key, Object o) {
        content.put(key, o);
    }

    public <T> T getContent(String key, Class<T> tClass) {
        return tClass.cast(getContent(key));
    }

    public Object getContent(String key) {
        return content.get(key);
    }

}
