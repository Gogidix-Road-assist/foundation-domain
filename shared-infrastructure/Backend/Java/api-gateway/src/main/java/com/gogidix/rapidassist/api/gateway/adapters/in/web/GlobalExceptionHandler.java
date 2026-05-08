package com.gogidix.rapidassist.api.gateway.adapters.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler for API Gateway.
 *
 * <p>This class handles all exceptions thrown by the gateway controllers
 * and returns consistent error responses to clients.</p>
 *
 * <p>Error responses follow a standard format with timestamp, status code,
 * error type, and message.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors for invalid input.
     *
     * @param ex the server web input exception
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ServerWebInputException ex) {
        logger.warn("Validation error: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            "Invalid request input: " + ex.getReason(),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles response status exceptions.
     *
     * @param ex the response status exception
     * @return error response with the specified status
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Response status error: {} - {}", ex.getStatusCode(), ex.getReason());

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            ex.getStatusCode().value(),
            ex.getStatusCode().toString(),
            ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString(),
            null
        );

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    /**
     * Handles illegal argument exceptions.
     *
     * @param ex the illegal argument exception
     * @return error response with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "ILLEGAL_ARGUMENT",
            ex.getMessage(),
            null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles illegal state exceptions.
     *
     * @param ex the illegal state exception
     * @return error response with CONFLICT status
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        logger.error("Illegal state: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            "ILLEGAL_STATE",
            ex.getMessage(),
            null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles all other unhandled exceptions.
     *
     * @param ex the throwable
     * @return error response with INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred. Please try again later.",
            null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Standard error response record.
     *
     * @param timestamp the time the error occurred
     * @param status the HTTP status code
     * @param error the error type/code
     * @param message the error message
     * @param path the request path that caused the error
     */
    public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
    ) {}
}
