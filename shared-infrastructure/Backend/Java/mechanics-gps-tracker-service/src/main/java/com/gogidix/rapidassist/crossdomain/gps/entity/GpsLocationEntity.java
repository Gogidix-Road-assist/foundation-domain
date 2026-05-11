package com.gogidix.rapidassist.crossdomain.gps.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import com.gogidix.rapidassist.shared.persistence.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for storing GPS location history of mechanics.
 * Extends BaseEntity for common fields including UUID ID, audit fields, and soft delete support.
 */
@Entity
@Table(name = "gps_locations", indexes = {
    @Index(name = "idx_gps_mechanic_id", columnList = "mechanic_id"),
    @Index(name = "idx_gps_timestamp", columnList = "timestamp"),
    @Index(name = "idx_gps_status", columnList = "status"),
    @Index(name = "idx_gps_current_job", columnList = "current_job_id"),
    @Index(name = "idx_gps_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GpsLocationEntity extends BaseEntity {

    @Column(name = "mechanic_id", nullable = false, length = 100)
    private String mechanicId;

    @Column(name = "mechanic_name", length = 255)
    private String mechanicName;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "speed")
    private Double speed; // km/h

    @Column(name = "heading")
    private Double heading; // degrees

    @Column(name = "altitude")
    private Double altitude; // meters

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private LocationStatus status;

    @Column(name = "current_job_id", length = 100)
    private String currentJobId;

    @Column(name = "vehicle_registration", length = 50)
    private String vehicleRegistration;

    @Column(name = "batch_id", length = 100)
    private String batchId;

    /**
     * Location status enum
     */
    public enum LocationStatus {
        AVAILABLE,
        EN_ROUTE_TO_JOB,
        AT_JOB_SITE,
        RETURNING,
        OFF_DUTY
    }

    /**
     * Checks if this location is recent (within last 5 minutes)
     */
    public boolean isRecent() {
        return timestamp != null &&
               timestamp.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    /**
     * Checks if mechanic is available for new jobs
     */
    public boolean isAvailable() {
        return status == LocationStatus.AVAILABLE &&
               (currentJobId == null || currentJobId.isEmpty());
    }
}
