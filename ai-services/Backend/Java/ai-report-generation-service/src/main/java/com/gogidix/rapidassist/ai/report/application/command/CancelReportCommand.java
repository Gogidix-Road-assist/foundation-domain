package com.gogidix.rapidassist.ai.report.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to cancel a report generation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReportCommand {

    private String tenantId;
    private UUID reportId;
    private String reason;
}
