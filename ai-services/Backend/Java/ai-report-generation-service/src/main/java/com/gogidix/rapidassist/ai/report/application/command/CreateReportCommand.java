package com.gogidix.rapidassist.ai.report.application.command;

import com.gogidix.rapidassist.ai.report.domain.model.ReportFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to create a new report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportCommand {

    private String tenantId;
    private String name;
    private String description;
    private ReportFormat format;
    private Map<String, Object> parameters;
    private UUID templateId;
    private UUID scheduleId;
    private String requestedBy;
}
