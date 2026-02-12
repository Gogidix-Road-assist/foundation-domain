package com.gogidix.rapidassist.ai.anomaly.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get an anomaly detection by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAnomalyDetectionQuery {

    private java.util.UUID detectionId;

    private String tenantId;
}
