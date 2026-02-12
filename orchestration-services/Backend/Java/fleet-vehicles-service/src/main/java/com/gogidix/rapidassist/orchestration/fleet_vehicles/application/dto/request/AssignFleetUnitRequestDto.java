package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a fleet unit to a driver/assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignFleetUnitRequestDto {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotBlank(message = "Assignment ID is required")
    private String assignmentId;
}
