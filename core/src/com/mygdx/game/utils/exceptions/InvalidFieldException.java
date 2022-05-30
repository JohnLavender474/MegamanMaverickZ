package com.mygdx.game.utils.exceptions;

public class InvalidFieldException extends AbstractRuntimeException {

    private static final String MESSAGE = "Value %s for field %s of object %s is invalid";

    public InvalidFieldException(String valueStr, String fieldName, String objectName) {
        super(String.format(MESSAGE, valueStr, fieldName, objectName));
    }

}
