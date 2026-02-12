package com.gogidix.rapidassist.orchestration.dispatching.application.command;

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
public class CreateDispatchCommand {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Priority is required")
    private DispatchPriority priority;

    @NotNull(message = "Location is required")
    private LocationCommand location;

    private List<String> requiredCapabilities;

    private String preferredProviderId;

    private AssignmentMethod assignmentMethod;

    private Integer timeoutSeconds;

    private String notes;

    public enum DispatchPriority {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    public enum AssignmentMethod {
        AUTOMATIC, MANUAL, ALERTING
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationCommand {
        @NotNull(message = "Latitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;

        private String address;
    }
}
