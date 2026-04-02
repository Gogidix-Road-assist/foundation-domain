package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Global exception handler for the Dashboard Configuration Service.
 *
 * <p>This class handles all exceptions thrown by the application and converts them
 * into standardized error responses. It uses @ControllerAdvice to provide centralized
 * exception handling across all controllers.</p>
 *
 * <p>Handled exceptions include:</p>
 * <ul>
 *   <li>ResourceNotFoundException - 404 Not Found</li>
 *   <li>ValidationException - 400 Bad Request</li>
 *   <li>BusinessRuleException - 422 Unprocessable Entity</li>
 *   <li>TenantNotFoundException - 404 Not Found</li>
 *   <li>MethodArgumentNotValidException - 400 Bad Request (validation errors)</li>
 *   <li>IllegalArgumentException - 400 Bad Request</li>
 *   <li>IllegalStateException - 500 Internal Server Error</li>
 *   <li>Exception - 500 Internal Server Error (catch-all)</li>
 * </ul>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 404 Not Found response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex,
        HttpServletRequest request
    ) {
        logger.warn("Resource not found: {} - {}", ex.getResourceType(), ex.getResourceId());

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles ValidationException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 400 Bad Request response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        ValidationException ex,
        HttpServletRequest request
    ) {
        logger.warn("Validation error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles BusinessRuleException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 422 Unprocessable Entity response
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
        BusinessRuleException ex,
        HttpServletRequest request
    ) {
        logger.warn("Business rule violation [{}]: {}", ex.getRuleCode(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Business Rule Violation",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handles TenantNotFoundException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 404 Not Found response
     */
    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTenantNotFoundException(
        TenantNotFoundException ex,
        HttpServletRequest request
    ) {
        logger.warn("Tenant not found: {}", ex.getTenantId());

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "Tenant Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles MethodArgumentNotValidException (Bean Validation errors).
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 400 Bad Request response with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        logger.warn("Validation failed: {}", validationErrors);

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            validationErrors,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles MethodArgumentTypeMismatchException (type conversion errors).
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 400 Bad Request response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex,
        HttpServletRequest request
    ) {
        String message = String.format("Invalid parameter '%s': expected type %s",
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        logger.warn("Type mismatch: {}", message);

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Parameter",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 400 Bad Request response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        logger.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalStateException.
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
        IllegalStateException ex,
        HttpServletRequest request
    ) {
        logger.error("Illegal state: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "The server encountered an internal error. Please try again later.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles all other exceptions (catch-all).
     *
     * @param ex      the exception
     * @param request the HTTP request
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        Exception ex,
        HttpServletRequest request
    ) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please contact support if the issue persists.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
