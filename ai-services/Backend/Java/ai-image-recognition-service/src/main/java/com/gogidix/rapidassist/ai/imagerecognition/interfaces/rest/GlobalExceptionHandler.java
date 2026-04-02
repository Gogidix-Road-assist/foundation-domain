package com.gogidix.rapidassist.ai.imagerecognition.interfaces.rest;

import com.gogidix.rapidassist.ai.imagerecognition.domain.exception.ImageRecognitionNotFoundException;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles all exceptions and returns consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImageRecognitionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            ImageRecognitionNotFoundException ex,
            WebRequest request) {

        log.warn("Not found: {} - {}", ex.getMessage(), request.getContextPath());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getContextPath())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Validation failed for one or more fields")
                .path(request.getContextPath())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getContextPath())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(
            IllegalStateException ex,
            WebRequest request) {

        log.warn("Illegal state: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getContextPath())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error: ", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getContextPath())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getCorrelationId() {
        try {
            return RequestContextHolder.getCorrelationId();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getTenantId() {
        try {
            return RequestContextHolder.getTenantId();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Error response DTO.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponseDto {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private String correlationId;
        private String tenantId;
        @lombok.Builder.Default
        private Map<String, String> fieldErrors = new HashMap<>();
    }
}
