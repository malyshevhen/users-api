package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception that is thrown when an entity is not found.
 * </p>
 * This exception is typically used to indicate that a requested resource
 * or entity could not be found in the system.
 * 
 * @author Evhen Malysh
 */
public class EntityNotFoundException extends BaseApplicationException {

    public EntityNotFoundException(String errorMessage) {
        super(errorMessage, HttpStatus.NOT_FOUND);
    }
}
