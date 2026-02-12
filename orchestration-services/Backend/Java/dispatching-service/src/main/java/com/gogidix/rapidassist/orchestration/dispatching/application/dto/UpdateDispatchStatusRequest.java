package com.gogidix.rapidassist.orchestration.dispatching.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchStatusRequest {

    @NotNull(message = "Status is required")
    private DispatchStatusDTO status;

    private String performedBy;

    private String notes;

    private LocalDateTime actualArrival;

    private LocalDateTime completionTime;

    private String rejectionReason;

    public enum DispatchStatusDTO {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED, ON_HOLD
    }
}
