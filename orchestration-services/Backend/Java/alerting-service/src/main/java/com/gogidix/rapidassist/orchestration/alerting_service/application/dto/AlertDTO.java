package com.gogidix.rapidassist.orchestration.alerting_service.application.dto;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {

    private String id;
    private String alertId;
    private String requestId;
    private String tenantId;

    private Alert.AlertType type;
    private Alert.AlertSeverity severity;
    private String title;
    private String description;
    private String source;

    private Alert.AlertStatus status;

    private String assignedTo;
    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;

    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer escalationLevel;
    private Boolean escalationRequired;

    private List<Alert.AlertAction> actions;

    private Alert.Location location;
    private Alert.VehicleInfo vehicleInfo;
    private Alert.CustomerInfo customerInfo;
}
