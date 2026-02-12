package com.gogidix.rapidassist.orchestration.alerting_service.application.dto;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Light-weight DTO for alert summary in list views
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSummaryDTO {

    private String alertId;
    private String requestId;
    private String tenantId;

    private Alert.AlertType type;
    private Alert.AlertSeverity severity;
    private String title;

    private Alert.AlertStatus status;
    private String assignedTo;

    private LocalDateTime createdAt;
    private Integer escalationLevel;
    private Boolean escalationRequired;
}
