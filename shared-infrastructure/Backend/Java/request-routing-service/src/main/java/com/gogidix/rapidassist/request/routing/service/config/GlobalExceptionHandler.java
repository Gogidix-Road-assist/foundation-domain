package com.gogidix.rapidassist.request.routing.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        log.warn("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(Instant.now(), ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("IllegalStateException: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(Instant.now(), 500, "Internal server error"));
    }

    public record ErrorResponse(Instant timestamp, int status, String message) {}
}
