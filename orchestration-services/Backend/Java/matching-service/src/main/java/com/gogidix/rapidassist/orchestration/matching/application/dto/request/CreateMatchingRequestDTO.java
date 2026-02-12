package com.gogidix.rapidassist.orchestration.matching.application.dto.request;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingAlgorithm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchingRequestDTO {

    @NotBlank(message = "Incident ID is required")
    private String incidentId;

    private MatchingAlgorithm algorithm;

    @NotNull(message = "Incident location is required")
    private LocationDTO incidentLocation;

    private String serviceType;

    private List<String> requiredCapabilities;

    private Integer priority;

    private Double maxDistanceKm;

    private Double maxCost;

    private Integer minProviderRating;

    private Boolean requireExactCapabilities;

    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private Double[] coordinates; // [longitude, latitude]

        private String address;

        private String city;

        private String state;

        private String postalCode;

        private String country;
    }
}
