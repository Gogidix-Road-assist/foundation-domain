package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntityRequest {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotNull(message = "Priority is required")
    private EntityPriorityDTO priority;

    @NotNull(message = "Location is required")
    private LocationDTO location;

    private String preferredProviderId;
    private AssignmentMethodDTO assignmentMethod;
    private Integer timeoutSeconds;
    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        @NotNull(message = "Latitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;

        private String address;
    }

    public enum EntityPriorityDTO {
        LOW, MEDIUM, HIGH, EMERGENCY
    }

    public enum AssignmentMethodDTO {
        AUTOMATIC, MANUAL, ALERTING
    }
}
