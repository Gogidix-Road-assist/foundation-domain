package com.gogidix.rapidassist.orchestration.alerting_service.application.command;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to create a new Alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertCommand {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Alert type is required")
    private Alert.AlertType type;

    @NotNull(message = "Alert severity is required")
    private Alert.AlertSeverity severity;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Source is required")
    private String source;

    private Alert.Location location;

    private Alert.VehicleInfo vehicleInfo;

    private Alert.CustomerInfo customerInfo;
}
