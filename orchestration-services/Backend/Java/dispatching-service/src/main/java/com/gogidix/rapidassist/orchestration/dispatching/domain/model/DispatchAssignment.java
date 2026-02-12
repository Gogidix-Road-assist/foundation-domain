package com.gogidix.rapidassist.orchestration.dispatching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispatch_assignments")
@CompoundIndex(name = "idx_dispatch_provider", def = "{'dispatchId': 1, 'providerId': 1}", unique = true)
public class DispatchAssignment {

    @Id
    private String id;

    @Indexed
    private String dispatchId;

    @Indexed
    private String providerId;

    @Indexed
    private String vehicleId;

    @Indexed
    private String driverId;

    @Indexed
    private AssignmentStatus status;

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

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    public enum AssignmentStatus {
        PENDING, ACCEPTED, REJECTED, CANCELLED, IN_TRANSIT, ON_SCENE, COMPLETED
    }
}
