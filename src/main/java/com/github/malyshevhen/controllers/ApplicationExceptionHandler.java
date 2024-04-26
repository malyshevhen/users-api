package com.github.malyshevhen.controllers;

import com.github.malyshevhen.exceptions.BaseApplicationException;
import com.github.malyshevhen.exceptions.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
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

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final BaseApplicationException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler({
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
