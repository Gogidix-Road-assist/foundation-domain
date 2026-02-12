package com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO for fleet request response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetRequestResponseDto {

    private String id;
    private String requestId;
    private String fleetId;
    private FleetRequest.RequestStatus status;
    private String serviceType;
    private FleetRequest.Priority priority;
    private String vehicleId;
    private LocationDto location;
    private String assignedFleetProviderId;
    private Instant estimatedArrival;
    private Instant actualArrival;
    private Instant completionTime;
    private Map<String, Object> metadata;
    private String tenantId;
    private Instant createdAt;
    private Instant updatedAt;

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
