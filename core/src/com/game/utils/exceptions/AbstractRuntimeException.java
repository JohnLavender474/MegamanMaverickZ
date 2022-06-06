package com.game.utils.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Generic runtime exception with nested list of errors.
 */
@Getter
public class AbstractRuntimeException extends RuntimeException {

    private final List<String> errors = new ArrayList<>();

    /**
     * Instantiates a new Abstract Runtime Exception.
     *
     * @param errors the errors
     */
    public AbstractRuntimeException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    /**
     * Instantiates a new Abstract Runtime Exception.
     *
     * @param errors the errors
     */
    public AbstractRuntimeException(String... errors) {
        this.errors.addAll(Arrays.asList(errors));
    }

}
