package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsExeption extends BaseApplicationException {

    public EntityAlreadyExistsExeption(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
