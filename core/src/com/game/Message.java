package com.game;

/**
 * A message has an owner and contents that are to be read by the listener.
 */
public interface Message {

    /**
     * Get message owner.
     *
     * @return the message owner
     */
    Object getOwner();

    /**
     * Get message contents.
     *
     * @return the message contents
     */
    Object getContents();

}
