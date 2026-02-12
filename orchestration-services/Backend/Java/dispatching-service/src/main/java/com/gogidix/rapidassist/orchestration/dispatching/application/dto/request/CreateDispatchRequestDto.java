package com.gogidix.rapidassist.orchestration.dispatching.application.dto.request;

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
public class CreateDispatchRequestDto {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Priority is required")
    private String priority;

    @NotNull(message = "Location is required")
    private LocationDto location;

    private List<String> requiredCapabilities;

    private String preferredProviderId;

    private String assignmentMethod;

    private Integer timeoutSeconds;

    private String notes;

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
