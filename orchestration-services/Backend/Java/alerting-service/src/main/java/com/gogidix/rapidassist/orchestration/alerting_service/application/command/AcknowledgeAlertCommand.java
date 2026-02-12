package com.gogidix.rapidassist.orchestration.alerting_service.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to acknowledge an alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcknowledgeAlertCommand {

    @NotBlank(message = "Acknowledged by is required")
    private String acknowledgedBy;

    @NotBlank(message = "Assigned to is required")
    private String assignedTo;
}
