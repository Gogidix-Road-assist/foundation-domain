package com.gogidix.rapidassist.ai.contentanalysis.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get detailed metrics for an analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMetricsQuery {

    private String tenantId;
    private UUID analysisId;
}
