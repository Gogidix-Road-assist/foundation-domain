package com.gogidix.rapidassist.orchestration.dispatching.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDispatchesQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private List<String> statuses;

    private String serviceType;

    private String providerId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer page = 0;

    private Integer size = 20;
}
