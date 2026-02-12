package com.gogidix.rapidassist.orchestration.alerting_service.application.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to escalate an alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalateAlertCommand {

    @NotNull(message = "Escalation level is required")
    @Min(value = 1, message = "Escalation level must be at least 1")
    private Integer escalationLevel;

    @NotBlank(message = "Assigned to is required")
    private String assignedTo;

    private String escalationNotes;
}
