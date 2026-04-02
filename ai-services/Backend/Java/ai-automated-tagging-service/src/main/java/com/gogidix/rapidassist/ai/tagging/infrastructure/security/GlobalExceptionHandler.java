package com.gogidix.rapidassist.ai.tagging.infrastructure.security;

import com.gogidix.rapidassist.ai.tagging.infrastructure.tenant.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for REST API
 * Handles all exceptions and returns consistent error responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String PATH = "path";
    private static final String CORRELATION_ID = "correlationId";
    private static final String TENANT_ID = "tenantId";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Illegal argument: {} - {}", ex.getMessage(), request.getContextPath());

        Map<String, Object> error = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage(),
            request.getContextPath()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(
            IllegalStateException ex,
            WebRequest request) {

        log.warn("Illegal state: {} - {}", ex.getMessage(), request.getContextPath());

        Map<String, Object> error = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage(),
            request.getContextPath()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        log.error("Runtime error: ", ex);

        Map<String, Object> error = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred",
            request.getContextPath()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error: ", ex);

        Map<String, Object> error = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred",
            request.getContextPath()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(TIMESTAMP, Instant.now().toString());
        errorResponse.put(STATUS, status.value());
        errorResponse.put(ERROR, error);
        errorResponse.put(MESSAGE, message);
        errorResponse.put(PATH, path);

        // Add tenant and correlation context if available
        try {
            errorResponse.put(TENANT_ID, RequestContextHolder.getTenantId());
            errorResponse.put(CORRELATION_ID, RequestContextHolder.getCorrelationId());
        } catch (Exception e) {
            // Context not available (request failed before interceptor)
            errorResponse.put(TENANT_ID, "unknown");
            errorResponse.put(CORRELATION_ID, "unknown");
        }

        return errorResponse;
    }
}
