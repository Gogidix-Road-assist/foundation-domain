package com.gogidix.rapidassist.ai.forecasting.infrastructure.security;

import com.gogidix.rapidassist.ai.forecasting.infrastructure.tenant.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler for REST controllers
 * Provides consistent error responses across the service
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
            .path(request.getDescription(false).replace("uri=", ""))
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
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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
            .path(request.getDescription(false).replace("uri=", ""))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @lombok.Data
    @lombok.Builder
    public static class ErrorResponseDto {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        @lombok.Builder.Default
        private Map<String, Object> details = Map.of();
    }
}
