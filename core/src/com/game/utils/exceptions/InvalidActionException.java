package com.game.utils.exceptions;

import java.util.Collection;

/**
 * Exception for invalid action.
 */
public class InvalidActionException extends AbstractRuntimeException {

    /**
     * Instantiates a new Invalid Action exception.
     *
     * @param errors the errors
     */
    public InvalidActionException(String... errors) {
        super(errors);
    }

    /**
     * Instantiates a new Invalid Action exception.
     *
     * @param errors the errors
     */
    public InvalidActionException(Collection<String> errors) {
        super(errors);
    }

}
