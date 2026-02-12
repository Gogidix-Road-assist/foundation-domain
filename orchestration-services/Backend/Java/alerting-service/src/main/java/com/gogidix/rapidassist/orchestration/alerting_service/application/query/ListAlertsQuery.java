package com.gogidix.rapidassist.orchestration.alerting_service.application.query;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Query to list alerts with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAlertsQuery {

    private String tenantId;

    private Alert.AlertType type;

    private Alert.AlertSeverity severity;

    private List<Alert.AlertStatus> statuses;

    private String assignedTo;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer page = 0;

    private Integer size = 20;
}
