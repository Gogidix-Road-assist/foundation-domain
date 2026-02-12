package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitStatus;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for fleet unit response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetUnitResponseDto {

    private String id;
    private String unitId;
    private String organizationId;
    private String unitName;
    private UnitType unitType;
    private String unitCode;
    private String vin;
    private String make;
    private String model;
    private Integer year;
    private UnitStatus status;
    private String currentDriverId;
    private String currentAssignmentId;
    private String licensePlate;
    private String description;
    private LocationDto currentLocation;
    private Double mileage;
    private Map<String, Object> specifications;
    private Boolean isActive;
    private Map<String, Object> metadata;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime timestamp;
    }
}
