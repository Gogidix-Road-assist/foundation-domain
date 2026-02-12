package com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for updating an existing fleet unit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFleetUnitRequestDto {

    @NotBlank(message = "Unit name is required")
    private String unitName;

    private String unitCode;

    private String vin;

    private String make;

    private String model;

    private Integer year;

    private UnitStatus status;

    private String licensePlate;

    private String description;

    private CreateFleetUnitRequestDto.LocationDto currentLocation;

    private Double mileage;

    private Map<String, Object> specifications;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
