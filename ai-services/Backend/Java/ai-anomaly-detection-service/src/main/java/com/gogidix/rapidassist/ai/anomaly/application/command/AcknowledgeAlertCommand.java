package com.gogidix.rapidassist.ai.anomaly.application.command;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Alert ID is required")
    private java.util.UUID alertId;

    @NotNull(message = "Acknowledged by is required")
    private String acknowledgedBy;

    private String tenantId;
}
