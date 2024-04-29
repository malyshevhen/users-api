package com.github.malyshevhen.controllers;

import com.github.malyshevhen.exceptions.BaseApplicationException;
import com.github.malyshevhen.domain.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Provides a centralized exception handling mechanism for the application.
 *
 * @author Evhen Malysh
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {

    /**
     * Handles custom application exceptions and returns a response with the
     * appropriate HTTP status code.
     * </p>
     * This method handles exceptions of type {@link BaseApplicationException},
     * which represent custom application-specific exceptions.
     * It creates an {@link ErrorResponse} object with the exception message
     * and returns it with the HTTP status code specified in the exception.
     *
     * @param ex The {@link BaseApplicationException} to handle.
     * @return A `ResponseEntity` containing an `ErrorResponse` with the exception
     * message and the HTTP status code specified in the exception.
     */
    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final BaseApplicationException ex) {
        var message = ex.getMessage();
        var response = new ErrorResponse(message);
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * Handles various types of bad request exceptions and returns a 400 Bad Request
     * response with the exception message.
     * </p>
     * This method handles the following exception types:
     * - {@link ConstraintViolationException}
     * - {@link IllegalArgumentException}
     * - {@link MissingServletRequestParameterException}
     * - {@link MethodArgumentTypeMismatchException}
     * - {@link MethodArgumentNotValidException}
     * - {@link HttpMessageNotReadableException}
     * - {@link PropertyReferenceException}
     *
     * @param ex The exception to handle.
     * @return A ResponseEntity containing an ErrorResponse with the exception
     * message and a 400 Bad Request status.
     */
    @ExceptionHandler({
        BeanInstantiationException.class,
        ConstraintViolationException.class,
        IllegalArgumentException.class,
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class,
        MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class,
        PropertyReferenceException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(final Exception ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles server exceptions and logs the error
     * before returning an internal server error response.
     *
     * @param ex The server exception to handle.
     * @return A ResponseEntity containing an error response
     * with the exception message and timestamp.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleServerException(final Exception ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
