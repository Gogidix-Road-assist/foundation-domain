package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * Global exception handler for Identity and Access Service.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Response status error: {} - {}", ex.getStatusCode(), ex.getReason());
        ErrorResponse error = new ErrorResponse(
            Instant.now(), ex.getStatusCode().value(),
            ex.getStatusCode().toString(),
            ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            Instant.now(), HttpStatus.BAD_REQUEST.value(),
            "ILLEGAL_ARGUMENT", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        logger.error("Illegal state: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            Instant.now(), HttpStatus.CONFLICT.value(),
            "ILLEGAL_STATE", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
            Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message
    ) {}
}
