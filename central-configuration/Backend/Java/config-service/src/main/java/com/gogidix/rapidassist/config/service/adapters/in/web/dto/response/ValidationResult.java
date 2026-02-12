package com.gogidix.rapidassist.config.service.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response DTO for configuration validation results.
 * Indicates whether a configuration set is valid and when it was validated.
 */
@Schema(description = "Result of configuration validation")
public record ValidationResult(

    @Schema(description = "Whether the configuration is valid", example = "true")
    boolean valid,

    @Schema(description = "Timestamp of validation", example = "2026-01-12T10:30:00Z")
    Instant validatedAt
) {}
