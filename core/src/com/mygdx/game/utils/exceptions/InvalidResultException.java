package com.mygdx.game.utils.exceptions;

import java.util.Collection;

public class InvalidResultException extends AbstractRuntimeException {

    public InvalidResultException(String... errors) {
        super(errors);
    }

    public InvalidResultException(Collection<String> errors) {
        super(errors);
    }

}
