package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document for Alert persistence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alerts")
public class AlertDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String alertId;

    @Indexed
    private String requestId;

    @Indexed
    private String tenantId;

    @Indexed
    private Alert.AlertType type;

    @Indexed
    private Alert.AlertSeverity severity;

    private String title;
    private String description;
    private String source;

    @Indexed
    private Alert.AlertStatus status;

    @Indexed
    private String assignedTo;

    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;

    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private String resolutionNotes;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    @Indexed
    private Integer escalationLevel;

    @Indexed
    private Boolean escalationRequired;

    private List<Alert.AlertAction> actions;

    private Alert.Location location;
    private Alert.VehicleInfo vehicleInfo;
    private Alert.CustomerInfo customerInfo;

    /**
     * Convert to domain model
     */
    public Alert toDomain() {
        return Alert.builder()
            .id(this.id)
            .alertId(this.alertId)
            .requestId(this.requestId)
            .tenantId(this.tenantId)
            .type(this.type)
            .severity(this.severity)
            .title(this.title)
            .description(this.description)
            .source(this.source)
            .status(this.status)
            .assignedTo(this.assignedTo)
            .acknowledgedAt(this.acknowledgedAt)
            .acknowledgedBy(this.acknowledgedBy)
            .resolvedAt(this.resolvedAt)
            .resolvedBy(this.resolvedBy)
            .resolutionNotes(this.resolutionNotes)
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .escalationLevel(this.escalationLevel)
            .escalationRequired(this.escalationRequired)
            .actions(this.actions)
            .location(this.location)
            .vehicleInfo(this.vehicleInfo)
            .customerInfo(this.customerInfo)
            .build();
    }

    /**
     * Convert from domain model
     */
    public static AlertDocument fromDomain(Alert alert) {
        return AlertDocument.builder()
            .id(alert.getId())
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .type(alert.getType())
            .severity(alert.getSeverity())
            .title(alert.getTitle())
            .description(alert.getDescription())
            .source(alert.getSource())
            .status(alert.getStatus())
            .assignedTo(alert.getAssignedTo())
            .acknowledgedAt(alert.getAcknowledgedAt())
            .acknowledgedBy(alert.getAcknowledgedBy())
            .resolvedAt(alert.getResolvedAt())
            .resolvedBy(alert.getResolvedBy())
            .resolutionNotes(alert.getResolutionNotes())
            .createdAt(alert.getCreatedAt())
            .updatedAt(alert.getUpdatedAt())
            .escalationLevel(alert.getEscalationLevel())
            .escalationRequired(alert.getEscalationRequired())
            .actions(alert.getActions())
            .location(alert.getLocation())
            .vehicleInfo(alert.getVehicleInfo())
            .customerInfo(alert.getCustomerInfo())
            .build();
    }
}
