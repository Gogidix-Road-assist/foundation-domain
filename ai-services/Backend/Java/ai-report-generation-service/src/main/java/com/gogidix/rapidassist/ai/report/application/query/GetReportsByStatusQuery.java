package com.gogidix.rapidassist.ai.report.application.query;

import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get reports by status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetReportsByStatusQuery {

    private String tenantId;
    private ReportStatus status;
    private Integer page;
    private Integer size;
}
