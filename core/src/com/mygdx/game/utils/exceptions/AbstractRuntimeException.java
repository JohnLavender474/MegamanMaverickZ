package com.mygdx.game.utils.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
public class AbstractRuntimeException extends RuntimeException {

    private final List<String> errors = new ArrayList<>();

    public AbstractRuntimeException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public AbstractRuntimeException(String... errors) {
        this.errors.addAll(Arrays.asList(errors));
    }

}
