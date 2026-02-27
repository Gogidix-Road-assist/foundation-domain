package com.gogidix.rapidassist.geo.location.service.domain.port.out;

import com.gogidix.rapidassist.geo.location.service.domain.model.Geofence;

import java.util.List;
import java.util.Optional;

public interface GeofenceStore {

    Optional<Geofence> find(String tenantId, String geofenceId);

    List<Geofence> findByTenantId(String tenantId);

    List<Geofence> findActiveByTenantId(String tenantId);

    List<Geofence> findContainingPoint(String tenantId, double latitude, double longitude);

    Geofence save(Geofence geofence);

    void delete(String tenantId, String geofenceId);

    List<Geofence> findNearby(String tenantId, double latitude, double longitude, double radiusKm);
}