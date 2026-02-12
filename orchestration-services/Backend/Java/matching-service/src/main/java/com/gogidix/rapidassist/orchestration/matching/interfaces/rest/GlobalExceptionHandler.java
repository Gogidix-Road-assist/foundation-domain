package com.gogidix.rapidassist.orchestration.matching.interfaces.rest;

import com.gogidix.rapidassist.orchestration.matching.shared.exception.InvalidMatchingRequestException;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.MatchingException;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.NoProvidersFoundException;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.ProviderNotFoundException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProviderNotFoundException(
        ProviderNotFoundException ex,
        WebRequest request
    ) {
        log.error("Provider not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(NoProvidersFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoProvidersFoundException(
        NoProvidersFoundException ex,
        WebRequest request
    ) {
        log.error("No providers found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidMatchingRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMatchingRequestException(
        InvalidMatchingRequestException ex,
        WebRequest request
    ) {
        log.error("Invalid matching request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MatchingException.class)
    public ResponseEntity<ErrorResponse> handleMatchingException(
        MatchingException ex,
        WebRequest request
    ) {
        log.error("Matching error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation error: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse("VALIDATION_ERROR", "Validation failed", HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
        Exception ex,
        WebRequest request
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(buildErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponse buildErrorResponse(
        String code,
        String message,
        HttpStatus status
    ) {
        return ErrorResponse.builder()
            .code(code)
            .message(message)
            .status(status.value())
            .timestamp(LocalDateTime.now())
            .build();
    }

    private ErrorResponse buildErrorResponse(
        String code,
        String message,
        HttpStatus status,
        Map<String, String> details
    ) {
        ErrorResponse response = buildErrorResponse(code, message, status);
        response.setDetails(details);
        return response;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ErrorResponse {
        private String code;
        private String message;
        private int status;
        private LocalDateTime timestamp;
        private Map<String, String> details;
    }
}
