package com.github.malyshevhen.exceptions;

/**
 * Represents an error response containing a message and a timestamp.
 */
public record ErrorResponse(String message, long timestamp) {

    /**
     * Constructs an ErrorResponse object with the given message and automatically
     * sets the timestamp.
     *
     * @param message the error message
     */
    public ErrorResponse(String message) {
        this(message, System.currentTimeMillis());
    }
}
