package com.gogidix.rapidassist.access.control.service.interfaces.rest;

import com.gogidix.rapidassist.access.control.service.application.dto.response.ErrorResponseDto;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Global Exception Handler: GlobalExceptionHandler
 *
 * Handles all exceptions thrown by REST controllers
 * and returns standardized error responses.
 */
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TenantContextException.class)
    public ResponseEntity<ErrorResponseDto> handleTenantContextException(
            TenantContextException ex,
            WebRequest request) {

        log.warn("Tenant context error: {} - {}", ex.getMessage(), request.getDescription(false));

        ErrorResponseDto error = ErrorResponseDto.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        log.warn("Validation error: {} - {}", ex.getMessage(), request.getDescription(false));

        ErrorResponseDto error = ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(
            NoSuchElementException ex,
            WebRequest request) {

        log.warn("Not found: {} - {}", ex.getMessage(), request.getDescription(false));

        ErrorResponseDto error = ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error: ", ex);

        String tenantId = RequestContextHolder.get()
                .map(ctx -> ctx.tenantId())
                .orElse("unknown");

        String correlationId = RequestContextHolder.get()
                .map(ctx -> ctx.correlationId())
                .orElse("unknown");

        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getDescription(false).replace("uri=", ""),
                Instant.now(),
                tenantId,
                correlationId,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public static class TenantContextException extends RuntimeException {
        public TenantContextException(String message) {
            super(message);
        }
    }
}
