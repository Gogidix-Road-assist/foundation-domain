package com.gogidix.rapidassist.database.management.service.adapters.in.web;

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

/**
 * Global exception handler for the Database Management Service.
 * <p>
 * This class handles exceptions across all controllers and provides
 * consistent error responses with proper HTTP status codes.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @param ex the validation exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed for request {}: {}", request.getDescription(false), errors);

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            errors.toString(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles illegal argument exceptions.
     *
     * @param ex the illegal argument exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.warn("Illegal argument in request {}: {}", request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles illegal state exceptions.
     *
     * @param ex the illegal state exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {

        logger.error("Illegal state in request {}: {}", request.getDescription(false), ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param ex the runtime exception for not found
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Check for specific "not found" messages to return 404
        if (message != null && message.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            logger.warn("Resource not found in request {}: {}", request.getDescription(false), message);
        } else {
            logger.error("Runtime error in request {}: {}", request.getDescription(false), message, ex);
        }

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Fallback handler for all other exceptions.
     *
     * @param ex the exception
     * @param request the web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Unexpected error in request {}: {}", request.getDescription(false), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Error response record.
     *
     * @param timestamp the time the error occurred
     * @param status the HTTP status code
     * @param error the error type
     * @param message the error message
     * @param path the request path
     */
    public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
    ) {}
}
