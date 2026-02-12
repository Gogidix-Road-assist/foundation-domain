package com.gogidix.rapidassist.orchestration.dispatching.application.dto.request;

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
public class AssignProviderRequestDto {

    @NotBlank(message = "Provider ID is required")
    private String providerId;

    private String vehicleId;

    private String driverId;

    @NotNull(message = "Assignment score is required")
    private Double assignmentScore;

    private Double estimatedDistance;

    private Integer estimatedDuration;

    private String notes;
}
