package com.gogidix.rapidassist.crossdomain.gps.repository;

import com.gogidix.rapidassist.crossdomain.gps.entity.GpsLocationEntity;
import com.gogidix.rapidassist.crossdomain.gps.entity.GpsLocationEntity.LocationStatus;
import com.gogidix.rapidassist.shared.persistence.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for GPS location entities.
 * Extends BaseRepository for soft delete support and common queries.
 */
@Repository
public interface GpsLocationRepository extends BaseRepository<GpsLocationEntity> {

    /**
     * Find latest location for a mechanic
     */
    @Query("SELECT g FROM GpsLocationEntity g WHERE g.mechanicId = :mechanicId AND g.deleted = false ORDER BY g.timestamp DESC")
    Optional<GpsLocationEntity> findLatestByMechanicId(@Param("mechanicId") String mechanicId);

    /**
     * Find active mechanics within a radius of a location
     * Uses Haversine formula for distance calculation
     */
    @Query(value = """
        SELECT * FROM gps_locations
        WHERE deleted = false
          AND status IN ('AVAILABLE', 'RETURNING')
          AND timestamp > :since
          AND earth_distance(ll_to_earth(:latitude, :longitude), ll_to_earth(latitude, longitude)) <= :radiusMeters
        ORDER BY timestamp DESC
        """, nativeQuery = true)
    List<GpsLocationEntity> findNearbyMechanics(
        @Param("latitude") double latitude,
        @Param("longitude") double longitude,
        @Param("radiusMeters") double radiusMeters,
        @Param("since") LocalDateTime since
    );

    /**
     * Find all active mechanics (not OFF_DUTY)
     */
    @Query("SELECT g FROM GpsLocationEntity g WHERE g.status <> 'OFF_DUTY' AND g.deleted = false ORDER BY g.timestamp DESC")
    List<GpsLocationEntity> findAllActive();

    /**
     * Find locations by batch ID
     */
    @Query("SELECT g FROM GpsLocationEntity g WHERE g.batchId = :batchId AND g.deleted = false")
    List<GpsLocationEntity> findByBatchId(@Param("batchId") String batchId);

    /**
     * Find mechanics on a specific job
     */
    @Query("SELECT g FROM GpsLocationEntity g WHERE g.currentJobId = :jobId AND g.deleted = false")
    List<GpsLocationEntity> findByJobId(@Param("jobId") String jobId);

    /**
     * Count locations for a mechanic in a date range
     */
    @Query("SELECT COUNT(g) FROM GpsLocationEntity g WHERE g.mechanicId = :mechanicId AND g.timestamp BETWEEN :start AND :end AND g.deleted = false")
    Long countByMechanicIdAndDateRange(
        @Param("mechanicId") String mechanicId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Delete old location records (cleanup)
     */
    @Query("UPDATE GpsLocationEntity g SET g.deleted = true, g.updatedAt = :now WHERE g.timestamp < :cutoff AND g.deleted = false")
    void softDeleteOldLocations(@Param("cutoff") LocalDateTime cutoff, @Param("now") LocalDateTime now);
}
