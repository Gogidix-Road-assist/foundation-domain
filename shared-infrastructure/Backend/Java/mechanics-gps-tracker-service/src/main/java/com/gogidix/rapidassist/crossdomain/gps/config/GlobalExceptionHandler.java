package com.gogidix.rapidassist.crossdomain.gps.config;

import com.gogidix.rapidassist.shared.dto.library.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for GPS Tracker Service.
 * Handles validation errors and general exceptions with consistent API responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        log.warn("Validation error: {}", fieldErrors);

        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Validation failed")
                        .error(ApiResponse.ApiError.builder()
                                .code("VALIDATION_ERROR")
                                .message("One or more fields failed validation")
                                .fieldErrors(fieldErrors)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .build()
        );
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid argument")
                        .error(ApiResponse.ApiError.builder()
                                .code("INVALID_ARGUMENT")
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build())
                        .build()
        );
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .success(false)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("An unexpected error occurred")
                        .error(ApiResponse.ApiError.builder()
                                .code("INTERNAL_ERROR")
                                .message(ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build())
                        .build()
        );
    }
}
