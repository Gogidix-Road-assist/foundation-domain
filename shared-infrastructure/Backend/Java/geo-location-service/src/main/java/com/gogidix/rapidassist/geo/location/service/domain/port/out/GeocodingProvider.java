package com.gogidix.rapidassist.geo.location.service.domain.port.out;

import com.gogidix.rapidassist.geo.location.service.domain.model.Address;
import com.gogidix.rapidassist.geo.location.service.domain.model.Coordinates;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GeocodingProvider {

    CompletableFuture<List<Address>> geocode(String address);

    CompletableFuture<List<Address>> reverseGeocode(Coordinates coordinates);

    CompletableFuture<Coordinates> geocodeToCoordinates(String address);

    boolean isAvailable();
}