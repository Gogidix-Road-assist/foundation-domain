package com.gogidix.rapidassist.orchestration.matching.shared.utils;

import lombok.experimental.UtilityClass;

/**
 * Utility class for geospatial calculations
 */
@UtilityClass
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two points using Haversine formula
     */
    public double calculateDistance(
        double lon1,
        double lat1,
        double lon2,
        double lat2
    ) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Validate coordinates
     */
    public boolean isValidCoordinates(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return false;
        }
        return longitude >= -180 && longitude <= 180 &&
               latitude >= -90 && latitude <= 90;
    }

    /**
     * Convert miles to kilometers
     */
    public double milesToKm(double miles) {
        return miles * 1.60934;
    }

    /**
     * Convert kilometers to miles
     */
    public double kmToMiles(double km) {
        return km / 1.60934;
    }
}
