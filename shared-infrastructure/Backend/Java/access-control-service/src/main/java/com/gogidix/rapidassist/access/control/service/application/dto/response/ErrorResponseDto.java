package com.gogidix.rapidassist.access.control.service.application.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * DTO: ErrorResponseDto
 *
 * Standard error response DTO.
 */
public record ErrorResponseDto(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        String tenantId,
        String correlationId,
        List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            String message,
            String rejectedValue
    ) {}

    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return new ErrorResponseDto(
                status,
                error,
                message,
                path,
                Instant.now(),
                null,
                null,
                List.of()
        );
    }

    public static ErrorResponseDto of(int status, String error, String message, String path,
                                      String tenantId, String correlationId) {
        return new ErrorResponseDto(
                status,
                error,
                message,
                path,
                Instant.now(),
                tenantId,
                correlationId,
                List.of()
        );
    }
}
