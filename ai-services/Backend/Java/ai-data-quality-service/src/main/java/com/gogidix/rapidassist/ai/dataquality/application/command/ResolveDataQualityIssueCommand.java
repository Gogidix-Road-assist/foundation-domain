package com.gogidix.rapidassist.ai.dataquality.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to resolve a data quality issue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveDataQualityIssueCommand {

    private String tenantId;
    private UUID issueId;
    private String resolvedBy;
    private String resolutionNotes;
}
