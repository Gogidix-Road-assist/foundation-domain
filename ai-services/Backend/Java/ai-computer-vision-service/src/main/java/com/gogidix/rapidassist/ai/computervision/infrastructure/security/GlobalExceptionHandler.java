package com.gogidix.rapidassist.ai.computervision.infrastructure.security;

import com.gogidix.rapidassist.ai.computervision.infrastructure.tenant.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the AI Computer Vision Service
 * Handles all exceptions and returns consistent error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        logger.warn("Illegal argument: {} - {}", ex.getMessage(), request.getRequestURI());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        logger.warn("Illegal state: {} - {}", ex.getMessage(), request.getRequestURI());

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error: ", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
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
     * Error Response DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class ErrorResponseDto {
        private Instant timestamp;
        private Integer status;
        private String error;
        private String message;
        private String path;
        private String correlationId;
        private String tenantId;
    }
}
