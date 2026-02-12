package com.gogidix.rapidassist.orchestration.dispatching.application.dto.response;

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
public class DispatchResponseDto {

    private String dispatchId;
    private String requestId;
    private String tenantId;
    private String serviceType;
    private String status;
    private String priority;
    private LocationDto location;
    private String assignedProviderId;
    private String assignedVehicleId;
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private LocalDateTime completionTime;
    private String assignmentMethod;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private Double latitude;
        private Double longitude;
        private String address;
    }
}
