package com.gogidix.rapidassist.orchestration.fleetorganization.interfaces.rest;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.ApiErrorResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception.ConflictException;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception.ValidationException;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleNotFound(
            NotFoundException ex,
            WebRequest request) {
        log.warn("Not found: {} - {}", ex.getMessage(), request.getDescription(false));

        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidation(
            ValidationException ex,
            WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponseDto> handleConflict(
            ConflictException ex,
            WebRequest request) {
        log.warn("Conflict: {}", ex.getMessage());

        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .correlationId(getCorrelationId())
                .tenantId(getTenantId())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGeneric(
            Exception ex,
            WebRequest request) {
        log.error("Unexpected error: ", ex);

        ApiErrorResponseDto error = ApiErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getDescription(false).replace("uri=", ""))
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
}
