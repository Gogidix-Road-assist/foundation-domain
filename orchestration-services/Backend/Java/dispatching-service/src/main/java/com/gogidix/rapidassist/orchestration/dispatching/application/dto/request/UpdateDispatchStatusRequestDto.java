package com.gogidix.rapidassist.orchestration.dispatching.application.dto.request;

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
public class UpdateDispatchStatusRequestDto {

    @NotNull(message = "Status is required")
    private String status;

    private String performedBy;

    private String notes;

    private LocalDateTime actualArrival;

    private LocalDateTime completionTime;
}
