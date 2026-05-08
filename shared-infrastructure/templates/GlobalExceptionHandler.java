package com.gogidix.shared.infrastructure.adapters.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for all AI Services
 * Provides consistent error responses across the application
 *
 * Usage: Copy this class to each service's infrastructure package
 * and update the package declaration accordingly.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors for @Valid annotated request bodies
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        String errorMessage = "Validation failed. Please check your request.";
        if (!fieldErrors.isEmpty()) {
            errorMessage = "Validation failed: " + String.join(", ", fieldErrors.values());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                errorMessage,
                request.getDescription(false).replace("uri=", ""),
                fieldErrors
        );

        log.warn("Validation error: {} - {}", errorMessage, fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles validation errors for request parameters and path variables
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {

        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "Request constraint violation: " + violations.values().stream().findFirst().orElse("Invalid value"),
                request.getDescription(false).replace("uri=", ""),
                violations
        );

        log.warn("Constraint violation: {}", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null
        );

        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "INVALID_STATE",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null
        );

        log.error("Illegal state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles all other exceptions not specifically handled
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        // Don't expose stack traces in production
        String userMessage = "An unexpected error occurred. Please try again later.";

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                userMessage,
                request.getDescription(false).replace("uri=", ""),
                null
        );

        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Standard error response structure
     */
    @Schema(description = "Standard error response structure")
    public record ErrorResponse(
            @Schema(description = "Timestamp when error occurred")
            LocalDateTime timestamp,

            @Schema(description = "HTTP status code")
            int status,

            @Schema(description = "Error code for programmatic handling")
            String errorCode,

            @Schema(description = "Human-readable error message")
            String message,

            @Schema(description = "Request path that caused the error")
            String path,

            @Schema(description = "Detailed field errors (for validation errors)")
            Map<String, String> fieldErrors
    ) {
        public ErrorResponse(LocalDateTime timestamp, int status, String errorCode,
                          String message, String path, Map<String, String> fieldErrors) {
            this.timestamp = timestamp;
            this.status = status;
            this.errorCode = errorCode;
            this.message = message;
            this.path = path;
            this.fieldErrors = fieldErrors;
        }
    }
}
