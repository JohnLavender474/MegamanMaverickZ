package com.game.utils.exceptions;

/**
 * Exception for invalid argument.
 */
public class InvalidArgumentException extends AbstractRuntimeException {

    private static final String MESSAGE = "Argument %s for field %s is invalid";

    /**
     * Instantiates a new Invalid Argument exception.
     *
     * @param argument the argument
     * @param field    the field
     */
    public InvalidArgumentException(String argument, String field) {
        super(String.format(MESSAGE, argument, field));
    }

}
