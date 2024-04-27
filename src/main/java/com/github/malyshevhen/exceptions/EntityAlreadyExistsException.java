package com.github.malyshevhen.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Represents an exception that is thrown when an entity already exists in the
 * system.
 * </p>
 * This exception is typically used when attempting to create a new entity that
 * would conflict with an existing one.
 * 
 * @author Evhen Malysh
 */
public class EntityAlreadyExistsException extends BaseApplicationException {

    public EntityAlreadyExistsException(String errorMessage) {
        super(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
