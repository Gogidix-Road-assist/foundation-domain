package com.gogidix.rapidassist.orchestration.dispatching.application.command;

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
public class AssignProviderCommand {

    @NotBlank(message = "Dispatch ID is required")
    private String dispatchId;

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
