package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entities")
public class Entity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String entityId;

    @Indexed
    private String requestId;

    @Indexed
    private EntityStatus status;

    @Indexed
    private EntityPriority priority;

    private String serviceType;

    private Location location;

    @Indexed
    private String assignedProviderId;

    @Indexed
    private String assignedVehicleId;

    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private LocalDateTime completionTime;

    @Indexed
    private AssignmentMethod assignmentMethod;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Double latitude;
        private Double longitude;
        private String address;
    }

    public enum EntityStatus {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED, ON_HOLD
    }

    public enum EntityPriority {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    public enum AssignmentMethod {
        AUTOMATIC, MANUAL, ALERTING
    }
}
