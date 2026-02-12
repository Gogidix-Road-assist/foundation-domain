package com.gogidix.rapidassist.orchestration.dispatching.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDispatchQuery {

    @NotBlank(message = "Dispatch ID is required")
    private String dispatchId;

    private String tenantId;
}
