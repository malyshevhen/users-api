package com.github.malyshevhen.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.github.malyshevhen.exceptions.EntityAlreadyExistsException;

public class ApplicationExceptionHandlerTest {

    private final ApplicationExceptionHandler exceptionHandler = new ApplicationExceptionHandler();

    @Test
    @SuppressWarnings("null")
    public void testHandleCustomException() {
        var ex = new EntityAlreadyExistsException("Custom exception");
        var response = exceptionHandler.handleCustomException(ex);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().timestamp());
        assertEquals("Custom exception", response.getBody().message());
    }

    @Test
    @SuppressWarnings("null")
    public void testHandleBadRequestMethodArgumentNotValidException() {
        var ex = mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn("Invalid request body");
        var response = exceptionHandler.handleBadRequest(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", response.getBody().message());
    }

    @Test
    @SuppressWarnings("null")
    public void testHandleBadRequestMethodArgumentTypeMismatchException() {
        var ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getMessage()).thenReturn("Invalid argument type");
        var response = exceptionHandler.handleBadRequest(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument type", response.getBody().message());
    }

    @Test
    @SuppressWarnings("null")
    public void testHandleServerException() {
        var ex = new Exception("Internal server error");
        var response = exceptionHandler.handleServerException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().message());
    }
}
