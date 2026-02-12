package com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.geo.location.service.domain.model.Coordinates;
import com.gogidix.rapidassist.geo.location.service.domain.model.Geofence;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Document(collection = "geofences")
public record GeofenceDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("geofence_id")
    String geofenceId,

    @Field("name")
    String name,

    @Field("type")
    Geofence.GeofenceType type,

    @Field("geometry")
    GeofenceGeometryEmbedded geometry,

    @Field("transitions")
    Geofence.GeofenceTransition[] transitions,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("is_active")
    boolean isActive,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_by")
    String createdBy,

    @Field("version")
    Long version
) {

    public static GeofenceDocument fromDomain(Geofence geofence) {
        return new GeofenceDocument(
            null, // MongoDB will generate ID
            geofence.tenantId(),
            geofence.geofenceId(),
            geofence.name(),
            geofence.type(),
            GeofenceGeometryEmbedded.fromDomain(geofence.geometry()),
            geofence.transitions(),
            geofence.metadata(),
            geofence.isActive(),
            geofence.createdAt(),
            geofence.updatedAt(),
            geofence.createdBy(),
            1L
        );
    }

    public static GeofenceDocument updateFromDomain(GeofenceDocument existing, Geofence geofence) {
        return new GeofenceDocument(
            existing.id(),
            geofence.tenantId(),
            geofence.geofenceId(),
            geofence.name(),
            geofence.type(),
            GeofenceGeometryEmbedded.fromDomain(geofence.geometry()),
            geofence.transitions(),
            geofence.metadata(),
            geofence.isActive(),
            existing.createdAt(),
            geofence.updatedAt(),
            existing.createdBy(),
            existing.version() + 1
        );
    }

    public Geofence toDomain() {
        return new Geofence(
            tenantId(),
            geofenceId(),
            name(),
            type(),
            geometry().toDomain(),
            transitions(),
            metadata(),
            isActive(),
            createdAt(),
            updatedAt(),
            createdBy()
        );
    }

    public record GeofenceGeometryEmbedded(
        String geometryType,
        double[] center,
        Double radiusKm,
        double[][] polygonVertices,
        double[] southWest,
        double[] northEast
    ) {

        public static GeofenceGeometryEmbedded fromDomain(Geofence.GeofenceGeometry geometry) {
            if (geometry instanceof Geofence.GeofenceCircle circle) {
                return new GeofenceGeometryEmbedded(
                    "circle",
                    new double[]{circle.center().latitude(), circle.center().longitude()},
                    circle.radiusKm(),
                    null,
                    null,
                    null
                );
            } else if (geometry instanceof Geofence.GeofencePolygon polygon) {
                double[][] vertices = new double[polygon.vertices().length][2];
                for (int i = 0; i < polygon.vertices().length; i++) {
                    vertices[i][0] = polygon.vertices()[i].latitude();
                    vertices[i][1] = polygon.vertices()[i].longitude();
                }
                return new GeofenceGeometryEmbedded(
                    "polygon",
                    null,
                    null,
                    vertices,
                    null,
                    null
                );
            } else if (geometry instanceof Geofence.GeofenceRectangle rectangle) {
                return new GeofenceGeometryEmbedded(
                    "rectangle",
                    null,
                    null,
                    null,
                    new double[]{rectangle.southWest().latitude(), rectangle.southWest().longitude()},
                    new double[]{rectangle.northEast().latitude(), rectangle.northEast().longitude()}
                );
            } else {
                throw new IllegalArgumentException("Unknown geometry type: " + geometry.getClass());
            }
        }

        public Geofence.GeofenceGeometry toDomain() {
            return switch (geometryType) {
                case "circle" -> new Geofence.GeofenceCircle(
                    Coordinates.of(center[0], center[1]),
                    radiusKm
                );
                case "polygon" -> {
                    Coordinates[] vertices = new Coordinates[polygonVertices.length];
                    for (int i = 0; i < polygonVertices.length; i++) {
                        vertices[i] = Coordinates.of(polygonVertices[i][0], polygonVertices[i][1]);
                    }
                    yield new Geofence.GeofencePolygon(vertices);
                }
                case "rectangle" -> new Geofence.GeofenceRectangle(
                    Coordinates.of(southWest[0], southWest[1]),
                    Coordinates.of(northEast[0], northEast[1])
                );
                default -> throw new IllegalStateException("Unknown geometry type: " + geometryType);
            };
        }
    }
}