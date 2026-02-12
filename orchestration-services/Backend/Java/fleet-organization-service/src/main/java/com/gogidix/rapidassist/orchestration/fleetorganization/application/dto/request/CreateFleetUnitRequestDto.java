package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for creating a new fleet unit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFleetUnitRequestDto {

    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Unit name is required")
    private String unitName;

    @NotNull(message = "Unit type is required")
    private UnitType unitType;

    private String unitCode;

    private String vin;

    private String make;

    private String model;

    private Integer year;

    private String licensePlate;

    private String description;

    private LocationDto currentLocation;

    private Double mileage;

    private Map<String, Object> specifications;

    private Map<String, Object> metadata;

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
