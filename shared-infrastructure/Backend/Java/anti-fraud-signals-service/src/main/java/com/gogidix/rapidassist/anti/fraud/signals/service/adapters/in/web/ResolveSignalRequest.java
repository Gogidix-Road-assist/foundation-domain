package com.gogidix.rapidassist.anti.fraud.signals.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request DTO for resolving an anti-fraud signal.
 */
@Schema(description = "Request to resolve an anti-fraud signal")
@JsonInclude(Include.NON_NULL)
public record ResolveSignalRequest(

        @Schema(
                description = "Whether the signal is resolved",
                example = "true",
                required = true
        )
        boolean resolved,

        @Schema(
                description = "User who resolved the signal",
                example = "fraud-analyst@example.com",
                required = true
        )
        @NotBlank(message = "resolvedBy cannot be blank")
        String resolvedBy,

        @Schema(
                description = "Notes about the resolution",
                example = "Verified with customer - legitimate transaction"
        )
        String resolutionNotes
) {}
