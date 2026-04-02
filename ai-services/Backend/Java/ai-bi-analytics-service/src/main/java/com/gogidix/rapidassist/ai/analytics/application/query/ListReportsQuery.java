package com.gogidix.rapidassist.ai.analytics.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list reports with pagination and filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListReportsQuery {

    private String tenantId;
    private String status;
    private String reportType;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
