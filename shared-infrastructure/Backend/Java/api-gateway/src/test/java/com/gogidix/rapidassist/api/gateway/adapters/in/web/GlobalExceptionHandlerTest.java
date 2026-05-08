package com.gogidix.rapidassist.api.gateway.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

/**
 * Unit tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleServerWebInputException_Returns400BadRequest() {
        // Given
        ServerWebInputException exception =
            new ServerWebInputException("Invalid request parameter");

        // When
        var response = exceptionHandler.handleValidationException(exception);

        // Then
        assert response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert response.getBody() != null;
        assert response.getBody().status() == 400;
        assert response.getBody().error().equals("VALIDATION_ERROR");
    }

    @Test
    void handleResponseStatusException_ReturnsSpecifiedStatus() {
        // Given
        ResponseStatusException exception =
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found");

        // When
        var response = exceptionHandler.handleResponseStatusException(exception);

        // Then
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert response.getBody() != null;
        assert response.getBody().status() == 404;
    }

    @Test
    void handleIllegalArgumentException_Returns400BadRequest() {
        // Given
        IllegalArgumentException exception =
            new IllegalArgumentException("Invalid route ID format");

        // When
        var response = exceptionHandler.handleIllegalArgument(exception);

        // Then
        assert response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert response.getBody() != null;
        assert response.getBody().status() == 400;
        assert response.getBody().error().equals("ILLEGAL_ARGUMENT");
    }

    @Test
    void handleIllegalStateException_Returns409Conflict() {
        // Given
        IllegalStateException exception =
            new IllegalStateException("Route already exists");

        // When
        var response = exceptionHandler.handleIllegalState(exception);

        // Then
        assert response.getStatusCode().equals(HttpStatus.CONFLICT);
        assert response.getBody() != null;
        assert response.getBody().status() == 409;
        assert response.getBody().error().equals("ILLEGAL_STATE");
    }

    @Test
    void handleGenericException_Returns500InternalServerError() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        var response = exceptionHandler.handleGenericException(exception);

        // Then
        assert response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR);
        assert response.getBody() != null;
        assert response.getBody().status() == 500;
        assert response.getBody().error().equals("INTERNAL_SERVER_ERROR");
    }
}
