package com.gogidix.rapidassist.orchestration.dispatching.application.dto;

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
public class DispatchResponse {

    private String dispatchId;
    private String requestId;
    private String tenantId;
    private String serviceType;
    private DispatchStatusDTO status;
    private DispatchPriorityDTO priority;
    private LocationDTO location;
    private String assignedProviderId;
    private String assignedVehicleId;
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private LocalDateTime completionTime;
    private AssignmentMethodDTO assignmentMethod;
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

    public enum DispatchStatusDTO {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED, ON_HOLD
    }

    public enum DispatchPriorityDTO {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    public enum AssignmentMethodDTO {
        AUTOMATIC, MANUAL, ALERTING
    }
}
