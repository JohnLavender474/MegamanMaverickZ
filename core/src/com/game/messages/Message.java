package com.game.messages;

/**
 * A message has an owner and contents that are to be read by the listener.
 */
public record Message(Object owner, Object contents) {
}
