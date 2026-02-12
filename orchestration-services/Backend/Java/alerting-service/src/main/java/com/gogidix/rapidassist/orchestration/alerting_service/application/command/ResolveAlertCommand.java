package com.gogidix.rapidassist.orchestration.alerting_service.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to resolve an alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveAlertCommand {

    @NotBlank(message = "Resolved by is required")
    private String resolvedBy;

    @NotBlank(message = "Resolution notes are required")
    private String resolutionNotes;
}
