package com.gogidix.rapidassist.rate.limit.policy.service.application.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Error response DTO.
 *
 * <p>This DTO is used to return error information through the API layer.
 */
public record ErrorResponseDto(
    int status,
    String error,
    String message,
    List<String> errors,
    String path,
    Instant timestamp,
    Map<String, Object> metadata
) {
    public ErrorResponseDto(int status, String error, String message, String path) {
        this(status, error, message, null, path, Instant.now(), null);
    }

    public ErrorResponseDto(int status, String error, String message, List<String> errors, String path) {
        this(status, error, message, errors, path, Instant.now(), null);
    }
}
