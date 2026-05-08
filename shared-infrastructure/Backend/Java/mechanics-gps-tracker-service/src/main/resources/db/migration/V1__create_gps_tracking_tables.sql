-- ============================================================================
-- Mechanics GPS Tracker Service Database Migration
-- Creates tables for GPS location tracking
-- Version: V1
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "earthdistance";

CREATE TABLE IF NOT EXISTS gps_tracking_points (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vehicle_id UUID NOT NULL,
    driver_id UUID,

    -- Location
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    altitude DECIMAL(10,2),
    accuracy DECIMAL(10,2),
    heading DECIMAL(6,2),
    speed DECIMAL(8,2),

    -- Vehicle State
    engine_status VARCHAR(20),
    ignition_on BOOLEAN,
    odometer_reading INTEGER,

    -- Context
    job_id UUID,
    job_status VARCHAR(50),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_engine_status CHECK (engine_status IN ('RUNNING', 'IDLE', 'OFF', 'UNKNOWN'))
);

CREATE INDEX IF NOT EXISTS idx_gps_vehicle ON gps_tracking_points(vehicle_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_gps_driver ON gps_tracking_points(driver_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_gps_job ON gps_tracking_points(job_id, timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_gps_location ON gps_tracking_points(ll_to_earth(latitude, longitude));
CREATE INDEX IF NOT EXISTS idx_gps_timestamp ON gps_tracking_points(timestamp DESC);

---
-- TABLE: gps_trip_summaries
-- Stores trip summaries for billing and analytics
---
CREATE TABLE IF NOT EXISTS gps_trip_summaries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_number VARCHAR(50) NOT NULL UNIQUE,
    vehicle_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    job_id UUID,

    -- Trip Details
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    start_latitude DECIMAL(10,8),
    start_longitude DECIMAL(11,8),
    end_latitude DECIMAL(10,8),
    end_longitude DECIMAL(11,8),
    start_address TEXT,
    end_address TEXT,

    -- Metrics
    total_distance_km DECIMAL(10,2),
    total_duration_seconds INTEGER,
    average_speed_kmh DECIMAL(8,2),
    max_speed_kmh DECIMAL(8,2),

    -- Status
    trip_status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_trip_status CHECK (trip_status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_gps_trip_vehicle ON gps_trip_summaries(vehicle_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_gps_trip_driver ON gps_trip_summaries(driver_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_gps_trip_status ON gps_trip_summaries(trip_status);
CREATE INDEX IF NOT EXISTS idx_gps_trip_job ON gps_trip_summaries(job_id);

---
-- VIEW: Latest GPS Positions
---
CREATE OR REPLACE VIEW v_latest_gps_positions AS
SELECT
    vehicle_id,
    driver_id,
    job_id,
    latitude,
    longitude,
    heading,
    speed,
    engine_status,
    timestamp
FROM gps_tracking_points
WHERE timestamp = (
    SELECT MAX(timestamp)
    FROM gps_tracking_points g2
    WHERE g2.vehicle_id = gps_tracking_points.vehicle_id
);

---
-- FUNCTION: Get vehicle path for time range
---
CREATE OR REPLACE FUNCTION get_vehicle_path(
    p_vehicle_id UUID,
    p_start_time TIMESTAMP,
    p_end_time TIMESTAMP
) RETURNS TABLE (
    point_id UUID,
    latitude DECIMAL,
    longitude DECIMAL,
    heading DECIMAL,
    speed DECIMAL,
    reading_time TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        gp.id,
        gp.latitude,
        gp.longitude,
        gp.heading,
        gp.speed,
        gp.timestamp
    FROM gps_tracking_points gp
    WHERE gp.vehicle_id = p_vehicle_id
      AND gp.timestamp BETWEEN p_start_time AND p_end_time
    ORDER BY gp.timestamp ASC;
END;
$$ LANGUAGE plpgsql;

---
-- FUNCTION: Calculate trip distance
---
CREATE OR REPLACE FUNCTION calculate_trip_distance(p_trip_id UUID) RETURNS DECIMAL AS $$
DECLARE
    v_distance DECIMAL;
BEGIN
    SELECT COALESCE(SUM(
        earth_distance(
            ll_to_earth(g1.latitude, g1.longitude),
            ll_to_earth(g2.latitude, g2.longitude)
        ) / 1000
    ), 0)
    INTO v_distance
    FROM gps_tracking_points g1
    JOIN gps_tracking_points g2 ON g2.timestamp = (
        SELECT MIN(timestamp)
        FROM gps_tracking_points
        WHERE timestamp > g1.timestamp
          AND vehicle_id = (SELECT vehicle_id FROM gps_trip_summaries WHERE id = p_trip_id)
    )
    WHERE g1.vehicle_id = (SELECT vehicle_id FROM gps_trip_summaries WHERE id = p_trip_id)
      AND g1.job_id = p_trip_id
      AND g1.timestamp BETWEEN (
          SELECT start_time FROM gps_trip_summaries WHERE id = p_trip_id,
          COALESCE((SELECT end_time FROM gps_trip_summaries WHERE id = p_trip_id), CURRENT_TIMESTAMP)
      );

    RETURN v_distance;
END;
$$ LANGUAGE plpgsql;

COMMENT ON TABLE gps_tracking_points IS 'GPS tracking point records';
COMMENT ON TABLE gps_trip_summaries IS 'GPS trip summaries for billing';
ANALYZE gps_tracking_points;
ANALYZE gps_trip_summaries;
