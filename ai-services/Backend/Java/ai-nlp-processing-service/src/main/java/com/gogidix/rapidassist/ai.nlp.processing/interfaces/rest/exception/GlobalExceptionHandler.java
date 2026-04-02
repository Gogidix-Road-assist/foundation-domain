package com.gogidix.rapidassist.ai.nlp.processing.interfaces.rest.exception;

import com.gogidix.rapidassist.ai.nlp.processing.interfaces.rest.dto.ErrorResponseDto;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.shared.security.library.exception.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for NLP Processing Service.
 * Handles all exceptions and returns standardized error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Validation failed for one or more fields")
            .details(errors)
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle resource not found
     */
    @ExceptionHandler({org.springframework.dao.EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception ex, WebRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Access Denied")
            .message("You do not have permission to access this resource")
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle invalid JWT token
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTokenException(
            InvalidTokenException ex,
            WebRequest request) {

        log.warn("Invalid token: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message("Invalid or expired token")
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Bad Request")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex, WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred. Please try again later.")
            .path(request.getDescription(false).replace("uri=", ""))
            .tenantId(getCurrentTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Get current tenant ID from request context
     */
    private String getCurrentTenantId() {
        return RequestContextHolder.get()
            .map(context -> context.tenantId())
            .orElse("unknown");
    }
}
