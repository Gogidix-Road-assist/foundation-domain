package com.gogidix.rapidassist.geo.location.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record Geofence(
    String geofenceId,
    String tenantId,
    String name,
    GeofenceType type,
    GeofenceGeometry geometry,
    GeofenceTransition[] transitions,
    Map<String, Object> metadata,
    boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {

    public boolean containsPoint(Coordinates point) {
        return switch (type) {
            case CIRCLE -> containsCircle(point, (GeofenceCircle) geometry);
            case POLYGON -> containsPolygon(point, (GeofencePolygon) geometry);
            case RECTANGLE -> containsRectangle(point, (GeofenceRectangle) geometry);
        };
    }

    public double distanceToPoint(Coordinates point) {
        return switch (type) {
            case CIRCLE -> distanceToCircle(point, (GeofenceCircle) geometry);
            case POLYGON -> distanceToPolygon(point, (GeofencePolygon) geometry);
            case RECTANGLE -> distanceToRectangle(point, (GeofenceRectangle) geometry);
        };
    }

    private boolean containsCircle(Coordinates point, GeofenceCircle circle) {
        double distance = calculateDistance(point, circle.center());
        return distance <= circle.radiusKm();
    }

    private boolean containsPolygon(Coordinates point, GeofencePolygon polygon) {
        // Ray casting algorithm for point in polygon
        boolean inside = false;
        Coordinates[] vertices = polygon.vertices();

        for (int i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
            if (((vertices[i].latitude() > point.latitude()) != (vertices[j].latitude() > point.latitude())) &&
                (point.longitude() < (vertices[j].longitude() - vertices[i].longitude()) *
                (point.latitude() - vertices[i].latitude()) /
                (vertices[j].latitude() - vertices[i].latitude()) + vertices[i].longitude())) {
                inside = !inside;
            }
        }
        return inside;
    }

    private boolean containsRectangle(Coordinates point, GeofenceRectangle rectangle) {
        return point.latitude() >= rectangle.southWest().latitude() &&
               point.latitude() <= rectangle.northEast().latitude() &&
               point.longitude() >= rectangle.southWest().longitude() &&
               point.longitude() <= rectangle.northEast().longitude();
    }

    private double distanceToCircle(Coordinates point, GeofenceCircle circle) {
        double distance = calculateDistance(point, circle.center());
        return Math.max(0, distance - circle.radiusKm());
    }

    private double distanceToPolygon(Coordinates point, GeofencePolygon polygon) {
        if (containsPolygon(point, polygon)) {
            return 0;
        }

        double minDistance = Double.MAX_VALUE;
        Coordinates[] vertices = polygon.vertices();

        for (int i = 0; i < vertices.length; i++) {
            Coordinates v1 = vertices[i];
            Coordinates v2 = vertices[(i + 1) % vertices.length];
            double distance = distanceToLineSegment(point, v1, v2);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }

    private double distanceToRectangle(Coordinates point, GeofenceRectangle rectangle) {
        double lat = point.latitude();
        double lon = point.longitude();
        double minLat = rectangle.southWest().latitude();
        double maxLat = rectangle.northEast().latitude();
        double minLon = rectangle.southWest().longitude();
        double maxLon = rectangle.northEast().longitude();

        if (lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon) {
            return 0;
        }

        double dx = Math.max(minLon - lon, Math.max(0, lon - maxLon));
        double dy = Math.max(minLat - lat, Math.max(0, lat - maxLat));

        return Math.sqrt(dx * dx + dy * dy);
    }

    private double distanceToLineSegment(Coordinates point, Coordinates v1, Coordinates v2) {
        double A = point.latitude() - v1.latitude();
        double B = point.longitude() - v1.longitude();
        double C = v2.latitude() - v1.latitude();
        double D = v2.longitude() - v1.longitude();

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = -1;

        if (lenSq != 0) {
            param = dot / lenSq;
        }

        Coordinates closest;
        if (param < 0) {
            closest = v1;
        } else if (param > 1) {
            closest = v2;
        } else {
            closest = Coordinates.of(
                v1.latitude() + param * C,
                v1.longitude() + param * D
            );
        }

        return calculateDistance(point, closest);
    }

    private double calculateDistance(Coordinates coord1, Coordinates coord2) {
        double lat1 = Math.toRadians(coord1.latitude());
        double lon1 = Math.toRadians(coord1.longitude());
        double lat2 = Math.toRadians(coord2.latitude());
        double lon2 = Math.toRadians(coord2.longitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1) * Math.cos(lat2) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Earth's radius in kilometers
    }

    public enum GeofenceType {
        CIRCLE,
        POLYGON,
        RECTANGLE
    }

    public sealed interface GeofenceGeometry {}

    public record GeofenceCircle(
        Coordinates center,
        double radiusKm
    ) implements GeofenceGeometry {}

    public record GeofencePolygon(
        Coordinates[] vertices
    ) implements GeofenceGeometry {}

    public record GeofenceRectangle(
        Coordinates southWest,
        Coordinates northEast
    ) implements GeofenceGeometry {}

    public enum GeofenceTransition {
        ENTER,
        EXIT,
        DWELL
    }
}