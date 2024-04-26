package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseApplicationException {

    public EntityNotFoundException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}
