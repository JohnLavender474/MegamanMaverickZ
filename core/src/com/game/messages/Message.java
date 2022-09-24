package com.game.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Message {

    private final Object owner;
    private final String key;
    private final Object content;

    public Message(Object owner, Object content) {
        this.key = "";
        this.owner = owner;
        this.content = content;
    }

}
