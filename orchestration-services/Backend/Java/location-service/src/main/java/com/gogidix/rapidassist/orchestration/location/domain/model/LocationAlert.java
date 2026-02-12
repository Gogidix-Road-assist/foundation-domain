package com.gogidix.rapidassist.orchestration.location.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Geofence breach alerts and notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "location_alerts")
public class LocationAlert {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String geofenceId;
    private String geofenceName;

    @Indexed
    private com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType;

    @Indexed
    private String entityId;

    private Double latitude;
    private Double longitude;
    private Map<String, Object> coordinates;

    @Indexed
    private AlertEventType eventType;

    @Indexed
    private AlertStatus status;

    private LocalDateTime timestamp;

    private LocalDateTime acknowledgedAt;
    private String acknowledgedBy;

    private Integer dwellDuration; // milliseconds
    private Integer dwellThreshold; // milliseconds

    private String message;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    private List<String> recipients;
    private Map<String, Object> notificationStatus;

    private Map<String, Object> metadata;
    private String sessionId; // Link to tracking session

    public enum AlertEventType {
        ENTER,
        EXIT,
        DWELL
    }

    public enum AlertStatus {
        PENDING,
        SENT,
        ACKNOWLEDGED,
        RESOLVED,
        FAILED
    }

    /**
     * Mark alert as acknowledged
     */
    public void acknowledge(String acknowledgedBy) {
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedAt = LocalDateTime.now();
        this.acknowledgedBy = acknowledgedBy;
    }

    /**
     * Mark alert as resolved
     */
    public void resolve() {
        this.status = AlertStatus.RESOLVED;
    }

    /**
     * Mark alert as sent
     */
    public void markAsSent() {
        this.status = AlertStatus.SENT;
    }

    /**
     * Mark alert as failed
     */
    public void markAsFailed() {
        this.status = AlertStatus.FAILED;
    }
}
