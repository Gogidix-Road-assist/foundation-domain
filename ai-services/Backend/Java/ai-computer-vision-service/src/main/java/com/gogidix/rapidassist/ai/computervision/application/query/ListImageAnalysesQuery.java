package com.gogidix.rapidassist.ai.computervision.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list image analyses with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListImageAnalysesQuery {

    private String tenantId;
    private String userId;
    private String status;
    private String analysisType;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
