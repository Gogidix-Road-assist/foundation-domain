package com.gogidix.rapidassist.orchestration.alerting_service.application.mapper;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal request object for creating alerts (used by mapper)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String tenantId;

    @NotNull
    private Alert.AlertType type;

    @NotNull
    private Alert.AlertSeverity severity;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String source;

    private Alert.Location location;

    private Alert.VehicleInfo vehicleInfo;

    private Alert.CustomerInfo customerInfo;
}
