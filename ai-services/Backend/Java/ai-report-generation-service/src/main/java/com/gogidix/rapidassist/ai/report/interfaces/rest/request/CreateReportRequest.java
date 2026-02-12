package com.gogidix.rapidassist.ai.report.interfaces.rest.request;

import com.gogidix.rapidassist.ai.report.domain.model.ReportFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * REST request to create a new report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {

    @NotBlank(message = "Report name is required")
    private String name;

    private String description;

    @NotNull(message = "Report format is required")
    private ReportFormat format;

    private Map<String, Object> parameters;

    private UUID templateId;

    private UUID scheduleId;

    @NotBlank(message = "Requested by is required")
    private String requestedBy;
}
