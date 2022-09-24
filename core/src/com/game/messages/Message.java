package com.game.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Predicate;

/** A message with a sender and a string-to-object map of content. */
@RequiredArgsConstructor
public class Message {

    @Getter
    private final Object sender;
    @Getter
    private final MessageType messageType;
    private final Map<String, Object> contents;

    /**
     * If this message type is the same as the provided one.
     *
     * @param messageType the message type
     * @return if this message type is the same as the provided one
     */
    public boolean is(MessageType messageType) {
        return this.messageType.equals(messageType);
    }

    /**
     * If the sender matches the predicate.
     *
     * @param predicate the predicate
     * @return if the sender matches the predicate
     */
    public boolean senderMatches(Predicate<Object> predicate) {
        return predicate.test(sender);
    }

    /**
     * Get content from the message.
     *
     * @param key the content's key
     * @param tClass the class of the content
     * @return the content
     * @param <T> the data type of the content
     */
    public <T> T getContent(String key, Class<T> tClass) {
        return tClass.cast(getContent(key));
    }

    /**
     * Get content from the message
     *
     * @param key the content's key
     * @return the content
     */
    public Object getContent(String key) {
        return contents.get(key);
    }

}
