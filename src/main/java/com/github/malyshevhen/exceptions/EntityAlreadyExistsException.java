package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseApplicationException {

    public EntityAlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
