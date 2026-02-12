package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.application.dto;

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
public class UpdateEntityStatusRequest {

    @NotNull(message = "Status is required")
    private EntityStatusDTO status;

    private String reason;
    private String notes;
    private String performedBy;
    private LocalDateTime actualArrival;
    private LocalDateTime completionTime;

    public enum EntityStatusDTO {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED, ON_HOLD
    }
}
