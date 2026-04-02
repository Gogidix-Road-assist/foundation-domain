package com.gogidix.rapidassist.ai.report.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to generate a report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportCommand {

    private String tenantId;
    private UUID reportId;
    private Map<String, Object> parameters;
    private String requestedBy;
}
