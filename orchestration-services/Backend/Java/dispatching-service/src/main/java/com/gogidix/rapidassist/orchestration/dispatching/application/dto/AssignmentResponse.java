package com.gogidix.rapidassist.orchestration.dispatching.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {

    private String assignmentId;
    private String dispatchId;
    private String providerId;
    private String vehicleId;
    private String driverId;
    private AssignmentStatusDTO status;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private Double assignmentScore;
    private Double estimatedDistance;
    private Integer estimatedDuration;

    public enum AssignmentStatusDTO {
        PENDING, ACCEPTED, REJECTED, CANCELLED, IN_TRANSIT, ON_SCENE, COMPLETED
    }
}
