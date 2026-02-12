package com.gogidix.rapidassist.ai.report.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a report by ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetReportQuery {

    private String tenantId;
    private UUID reportId;
    private Boolean includeDistributions;
}
