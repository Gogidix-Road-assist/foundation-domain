package com.gogidix.rapidassist.ai.anomaly.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Query to list anomaly detections with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAnomalyDetectionsQuery {

    private String tenantId;

    private String dataSource;

    private String severity;

    private String status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double minAnomalyScore;

    private Integer page;

    private Integer size;

    private String sortBy;

    private String sortDirection;
}
