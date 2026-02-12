package com.gogidix.rapidassist.config.service.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * Response DTO for value validation results.
 * Provides detailed validation errors if validation fails.
 */
@Schema(description = "Result of value validation")
public record ValueValidationResult(

    @Schema(description = "Whether the value is valid", example = "true")
    boolean valid,

    @Schema(description = "List of validation errors", example = "[\"Value must be positive\", \"Value exceeds maximum\"]")
    Set<String> errors
) {}
