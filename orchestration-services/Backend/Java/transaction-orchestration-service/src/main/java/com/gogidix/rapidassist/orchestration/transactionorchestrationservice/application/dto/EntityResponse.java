package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.dto;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse {

    private String id;
    private String entityId;
    private String requestId;
    private String tenantId;

    private Entity.EntityStatus status;
    private Entity.EntityPriority priority;
    private String serviceType;

    private Entity.Location location;

    private String assignedProviderId;
    private String assignedVehicleId;

    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private LocalDateTime completionTime;

    private Entity.AssignmentMethod assignmentMethod;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private Double latitude;
        private Double longitude;
        private String address;
    }

    public enum EntityStatusDTO { PENDING, ACTIVE, INACTIVE, SUSPENDED, TERMINATED }
    public enum EntityPriorityDTO { LOW, MEDIUM, HIGH, EMERGENCY }
    public enum AssignmentMethodDTO { AUTOMATIC, MANUAL, ALERTING }
    public enum AssignmentStatusDTO { PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED }
}
