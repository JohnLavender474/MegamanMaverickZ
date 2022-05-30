package com.mygdx.game.utils.exceptions;

import java.util.Collection;

public class InvalidActionException extends AbstractRuntimeException {

    public InvalidActionException(String... errors) {
        super(errors);
    }

    public InvalidActionException(Collection<String> errors) {
        super(errors);
    }

}
