package com.gogidix.rapidassist.ai.summarization.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSummariesByTenantQuery {
    private String tenantId;
    private String status;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
