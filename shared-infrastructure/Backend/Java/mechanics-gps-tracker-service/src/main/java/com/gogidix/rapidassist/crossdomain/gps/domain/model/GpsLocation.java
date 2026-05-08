package com.gogidix.rapidassist.crossdomain.gps.domain.model;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GpsLocation {
    private String mechanicId;
    private String mechanicName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double speed; // km/h
    private Double heading; // degrees
    private Double altitude; // meters
    private LocalDateTime timestamp;
    private LocationStatus status;
    private String currentJobId; // If on a job
    private String vehicleRegistration;

    public enum LocationStatus {
        AVAILABLE,
        EN_ROUTE_TO_JOB,
        AT_JOB_SITE,
        RETURNING,
        OFF_DUTY
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationBatch {
        private String batchId;
        private LocalDateTime timestamp;
        private java.util.List<GpsLocation> locations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingSummary {
        private String mechanicId;
        private String mechanicName;
        private GpsLocation lastKnownLocation;
        private LocalDateTime lastUpdate;
        private String currentStatus;
        private String currentJobId;
        private Double distanceTraveledToday; // km
        private Integer jobsCompletedToday;
    }
}
