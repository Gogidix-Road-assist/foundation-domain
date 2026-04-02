package com.gogidix.rapidassist.ai.dataquality.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a data quality check by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDataQualityCheckQuery {

    private String tenantId;
    private UUID checkId;
}
