package com.gogidix.rapidassist.orchestration.alerting_service.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get a single alert by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAlertQuery {

    @NotBlank(message = "Alert ID is required")
    private String alertId;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;
}
