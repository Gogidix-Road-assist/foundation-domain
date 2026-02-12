package com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new fleet assistance request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFleetRequestDto {

    @NotBlank(message = "Fleet ID is required")
    private String fleetId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Priority is required")
    private FleetRequest.Priority priority;

    private String vehicleId;

    @NotNull(message = "Location is required")
    private LocationDto location;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        @NotNull(message = "Latitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;

        private String address;
    }
}
