package com.mygdx.game.utils.exceptions;

/**
 * Exception for clash.
 */
public class ClashException extends AbstractRuntimeException {

    private static final String MESSAGE = "%s clashes with %s";

    /**
     * Instantiates a new Clash Exception.
     *
     * @param s1 the String representation of the first clashing object
     * @param s2 the String representation of the second clashing object
     */
    public ClashException(String s1, String s2) {
        super(String.format(MESSAGE, s1, s2));
    }

}
