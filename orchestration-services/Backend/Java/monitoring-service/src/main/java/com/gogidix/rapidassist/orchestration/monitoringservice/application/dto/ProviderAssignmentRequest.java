package com.gogidix.rapidassist.orchestration.monitoringservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAssignmentRequest {

    @NotBlank(message = "Provider ID is required")
    private String providerId;

    private String vehicleId;
    private String driverId;

    private Double assignmentScore;
    private Double estimatedDistance;
    private Integer estimatedDuration;

    private String notes;
}
