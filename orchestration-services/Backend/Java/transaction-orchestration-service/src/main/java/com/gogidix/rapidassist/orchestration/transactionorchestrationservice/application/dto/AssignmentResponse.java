package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.dto;

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
    private String entityId;
    private String providerId;
    private String vehicleId;
    private String driverId;

    private AssignmentStatusDTO status;

    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    private LocalDateTime completedAt;

    private Double assignmentScore;
    private Double estimatedDistance;
    private Integer estimatedDuration;
    private Double actualDistance;
    private Integer actualDuration;

    private String tenantId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum AssignmentStatusDTO {
        PENDING, ACCEPTED, REJECTED, CANCELLED, IN_TRANSIT, ON_SCENE, COMPLETED
    }
}
