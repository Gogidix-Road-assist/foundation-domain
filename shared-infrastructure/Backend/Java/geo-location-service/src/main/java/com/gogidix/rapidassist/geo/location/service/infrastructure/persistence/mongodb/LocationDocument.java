package com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.geo.location.service.domain.model.Address;
import com.gogidix.rapidassist.geo.location.service.domain.model.Coordinates;
import com.gogidix.rapidassist.geo.location.service.domain.model.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "locations")
public record LocationDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("location_id")
    String locationId,

    @Field("coordinates")
    CoordinatesEmbedded coordinates,

    @Field("address")
    AddressEmbedded address,

    @Field("accuracy")
    Location.LocationAccuracy accuracy,

    @Field("source")
    Location.LocationSource source,

    @Field("timestamp")
    Instant timestamp,

    @Field("device_id")
    String deviceId,

    @Field("user_id")
    String userId,

    @Field("ip_address")
    String ipAddress,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("geofence_status")
    Location.GeofenceStatus geofenceStatus,

    @Field("session_id")
    String sessionId,

    @Field("created_at")
    Instant createdAt,

    @Field("version")
    Long version
) {

    public static LocationDocument fromDomain(Location location) {
        return new LocationDocument(
            null, // MongoDB will generate ID
            location.tenantId(),
            location.locationId(),
            new CoordinatesEmbedded(location.coordinates()),
            location.address() != null ? new AddressEmbedded(location.address()) : null,
            location.accuracy(),
            location.source(),
            location.timestamp(),
            location.deviceId(),
            location.userId(),
            location.ipAddress(),
            location.metadata(),
            location.geofenceStatus(),
            location.sessionId(),
            Instant.now(),
            1L
        );
    }

    public Location toDomain() {
        return new Location(
            tenantId(),
            locationId(),
            coordinates().toDomain(),
            address() != null ? address().toDomain() : null,
            accuracy(),
            source(),
            timestamp(),
            deviceId(),
            userId(),
            ipAddress(),
            metadata(),
            geofenceStatus(),
            sessionId()
        );
    }

    public record CoordinatesEmbedded(
        double latitude,
        double longitude,
        Double altitude,
        Double accuracy,
        Double altitudeAccuracy,
        Double heading,
        Double speed
    ) {

        public CoordinatesEmbedded(Coordinates coordinates) {
            this(
                coordinates.latitude(),
                coordinates.longitude(),
                coordinates.altitude(),
                coordinates.accuracy(),
                coordinates.altitudeAccuracy(),
                coordinates.heading(),
                coordinates.speed()
            );
        }

        public Coordinates toDomain() {
            return new Coordinates(
                latitude,
                longitude,
                altitude,
                accuracy,
                altitudeAccuracy,
                heading,
                speed
            );
        }
    }

    public record AddressEmbedded(
        String streetAddress,
        String city,
        String state,
        String postalCode,
        String country,
        String countryCode,
        String administrativeAreaLevel1,
        String administrativeAreaLevel2,
        String locality,
        String sublocality,
        String premise,
        String subpremise,
        String formattedAddress
    ) {

        public AddressEmbedded(Address address) {
            this(
                address.streetAddress(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.country(),
                address.countryCode(),
                address.administrativeAreaLevel1(),
                address.administrativeAreaLevel2(),
                address.locality(),
                address.sublocality(),
                address.premise(),
                address.subpremise(),
                address.formattedAddress()
            );
        }

        public Address toDomain() {
            return new Address(
                streetAddress,
                city,
                state,
                postalCode,
                country,
                countryCode,
                administrativeAreaLevel1,
                administrativeAreaLevel2,
                locality,
                sublocality,
                premise,
                subpremise,
                formattedAddress
            );
        }
    }
}