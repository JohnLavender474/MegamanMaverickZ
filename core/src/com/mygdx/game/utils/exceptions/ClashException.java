package com.mygdx.game.utils.exceptions;

public class ClashException extends AbstractRuntimeException {

    private static final String MESSAGE = "%s clashes with %s";

    public ClashException(String s1, String s2) {
        super(String.format(MESSAGE, s1, s2));
    }

}
