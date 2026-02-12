package com.gogidix.rapidassist.orchestration.dispatching.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDispatchStatusCommand {

    @NotBlank(message = "Dispatch ID is required")
    private String dispatchId;

    @NotNull(message = "Status is required")
    private DispatchStatus status;

    private String performedBy;

    private String notes;

    private LocalDateTime actualArrival;

    private LocalDateTime completionTime;

    public enum DispatchStatus {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED, ON_HOLD
    }
}
