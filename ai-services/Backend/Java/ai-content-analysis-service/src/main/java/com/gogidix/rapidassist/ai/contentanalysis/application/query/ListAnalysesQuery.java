package com.gogidix.rapidassist.ai.contentanalysis.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to list content analyses with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAnalysesQuery {

    private String tenantId;
    private String status;
    private String contentType;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
