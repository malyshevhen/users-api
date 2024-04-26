package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

public class UserValidationException extends BaseApplicationException {

    public UserValidationException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
