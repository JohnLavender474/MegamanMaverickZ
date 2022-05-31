package com.mygdx.game.utils.exceptions;

/**
 * Exception for invalid field.
 */
public class InvalidFieldException extends AbstractRuntimeException {

    private static final String MESSAGE = "Value %s for field %s of object %s is invalid";

    /**
     * Instantiates a new Invalid Field Exception.
     *
     * @param valueStr   the value of the field
     * @param fieldName  the name of the field
     * @param objectName the name of the object (that would have been) containing the field
     */
    public InvalidFieldException(String valueStr, String fieldName, String objectName) {
        super(String.format(MESSAGE, valueStr, fieldName, objectName));
    }

}
