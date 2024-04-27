package com.github.malyshevhen.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
/**
 * A base exception class that represents an application-level error. It
 * provides an HTTP status code associated with the error.
 */
public class BaseApplicationException extends RuntimeException {

    private final HttpStatus status;

    public BaseApplicationException(String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.status = status;
    }
}
