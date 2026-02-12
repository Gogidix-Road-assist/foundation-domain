package com.gogidix.rapidassist.ai.analytics.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get a report by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetReportQuery {

    private String reportId;
    private String tenantId;
}
