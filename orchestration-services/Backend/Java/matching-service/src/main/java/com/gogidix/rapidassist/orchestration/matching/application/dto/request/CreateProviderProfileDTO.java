package com.gogidix.rapidassist.orchestration.matching.application.dto.request;

import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProviderProfileDTO {

    @NotBlank(message = "Provider ID is required")
    private String providerId;

    @NotBlank(message = "Provider name is required")
    private String providerName;

    @NotNull(message = "Current location is required")
    private LocationDTO currentLocation;

    private List<String> capabilities;

    private List<ServiceAreaDTO> serviceAreas;

    private ProviderProfile.ProviderStatus status;

    private Double baseRate;

    private Double ratePerKm;

    private Double rating;

    private Integer maxConcurrentJobs;

    private Double averageResponseTimeMinutes;

    private Map<String, Double> capabilitySpecializationScores;

    private Integer priorityLevel;

    private Boolean isVerified;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private Double[] coordinates;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceAreaDTO {
        private String areaId;
        private String name;
        private List<String> postalCodes;
        private Double radiusKm;
        private Double[] centerCoordinates;
    }
}
