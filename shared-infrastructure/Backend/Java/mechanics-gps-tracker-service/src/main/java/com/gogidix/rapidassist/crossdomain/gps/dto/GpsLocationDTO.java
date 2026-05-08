package com.gogidix.rapidassist.crossdomain.gps.dto;

import com.gogidix.rapidassist.crossdomain.gps.entity.GpsLocationEntity;
import com.gogidix.rapidassist.shared.dto.library.common.BaseDTO;
import com.gogidix.rapidassist.shared.validation.library.annotation.NotNullUUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for GPS location updates.
 * Extends BaseDTO for common fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "GPS location data for mechanic tracking")
public class GpsLocationDTO extends BaseDTO {

    @NotNullUUID
    @Schema(description = "Mechanic ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String mechanicId;

    @Schema(description = "Mechanic name", example = "John Doe")
    private String mechanicName;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    @Schema(description = "Latitude coordinate", example = "40.7128")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    @Schema(description = "Longitude coordinate", example = "-74.0060")
    private Double longitude;

    @DecimalMin(value = "0.0", message = "Speed cannot be negative")
    @Schema(description = "Speed in km/h", example = "45.5")
    private Double speed;

    @DecimalMin(value = "0.0", message = "Heading cannot be negative")
    @DecimalMax(value = "360.0", message = "Heading must be between 0 and 360")
    @Schema(description = "Heading in degrees", example = "180.0")
    private Double heading;

    @Schema(description = "Altitude in meters", example = "100.0")
    private Double altitude;

    @NotNull(message = "Timestamp is required")
    @Schema(description = "Location timestamp", example = "2026-01-04T12:30:00")
    private LocalDateTime timestamp;

    @NotNull(message = "Status is required")
    @Schema(description = "Mechanic status", example = "AVAILABLE")
    private GpsLocationEntity.LocationStatus status;

    @Schema(description = "Current job ID if on a job", example = "JOB-12345")
    private String currentJobId;

    @Schema(description = "Vehicle registration", example = "ABC-1234")
    private String vehicleRegistration;

    @Schema(description = "Batch ID for grouped updates", example = "BATCH-1704351600000")
    private String batchId;

    /**
     * Location batch for bulk updates
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Batch of GPS location updates")
    public static class LocationBatchDTO extends BaseDTO {
        @Schema(description = "Batch ID")
        private String batchId;

        @Schema(description = "Batch timestamp")
        private LocalDateTime timestamp;

        @NotNull(message = "Locations are required")
        @Size(min = 1, max = 1000, message = "Batch must contain 1-1000 locations")
        private List<GpsLocationDTO> locations;
    }

    /**
     * Tracking summary for a mechanic
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Mechanic tracking summary")
    public static class TrackingSummaryDTO extends BaseDTO {
        @NotNullUUID
        private String mechanicId;

        private String mechanicName;
        private GpsLocationDTO lastKnownLocation;
        private LocalDateTime lastUpdate;
        private String currentStatus;
        private String currentJobId;
        private Double distanceTraveledToday; // km
        private Integer jobsCompletedToday;
    }
}
