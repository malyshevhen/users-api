package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception that occurs when user input validation fails.
 * </p>
 * This exception is used to indicate that the user has provided invalid or
 * missing data, and the application should respond with a 400 Bad Request HTTP
 * status code.
 * 
 * @author Evhen Malysh
 */
public class UserValidationException extends BaseApplicationException {

    public UserValidationException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
