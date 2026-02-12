package com.gogidix.rapidassist.geo.location.service.infrastructure.provider;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.gogidix.rapidassist.geo.location.service.domain.model.Address;
import com.gogidix.rapidassist.geo.location.service.domain.model.Coordinates;
import com.gogidix.rapidassist.geo.location.service.domain.port.out.GeocodingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "gogidix.geocoding.provider", havingValue = "google", matchIfMissing = true)
public class GoogleGeocodingProvider implements GeocodingProvider {

    private static final Logger logger = LoggerFactory.getLogger(GoogleGeocodingProvider.class);

    @Value("${gogidix.geocoding.google.api-key:}")
    private String apiKey;

    @Value("${gogidix.geocoding.google.daily-limit:2500}")
    private int dailyLimit;

    private GeoApiContext context;
    private int queriesToday = 0;
    private long lastResetTime = System.currentTimeMillis();

    @PostConstruct
    public void init() {
        if (apiKey != null && !apiKey.isEmpty()) {
            context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .queryRateLimit(50)
                .build();
            logger.info("Google Geocoding provider initialized");
        } else {
            logger.warn("Google Maps API key not configured - geocoding provider will be disabled");
        }
    }

    @Override
    public CompletableFuture<List<Address>> geocode(String address) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isAvailable()) {
                    logger.warn("Google Geocoding not available - daily limit reached or API key missing");
                    return List.of();
                }

                incrementQueryCount();
                GeocodingResult[] results = GeocodingApi.geocode(context, address).await();

                return Arrays.stream(results)
                    .map(this::convertToAddress)
                    .collect(Collectors.toList());

            } catch (ApiException e) {
                logger.error("Google Geocoding API error: {}", e.getMessage(), e);
                return List.of();
            } catch (Exception e) {
                logger.error("Error geocoding address: {}", address, e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<List<Address>> reverseGeocode(Coordinates coordinates) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isAvailable()) {
                    logger.warn("Google Geocoding not available - daily limit reached or API key missing");
                    return List.of();
                }

                incrementQueryCount();
                LatLng latLng = new LatLng(coordinates.latitude(), coordinates.longitude());
                GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).await();

                return Arrays.stream(results)
                    .map(this::convertToAddress)
                    .collect(Collectors.toList());

            } catch (ApiException e) {
                logger.error("Google Geocoding API error: {}", e.getMessage(), e);
                return List.of();
            } catch (Exception e) {
                logger.error("Error reverse geocoding coordinates: {}", coordinates, e);
                return List.of();
            }
        });
    }

    @Override
    public CompletableFuture<Coordinates> geocodeToCoordinates(String address) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isAvailable()) {
                    logger.warn("Google Geocoding not available - daily limit reached or API key missing");
                    return null;
                }

                incrementQueryCount();
                GeocodingResult[] results = GeocodingApi.geocode(context, address).await();

                if (results.length > 0) {
                    GeocodingResult result = results[0];
                    return Coordinates.of(
                        result.geometry.location.lat,
                        result.geometry.location.lng
                    );
                }

                return null;

            } catch (ApiException e) {
                logger.error("Google Geocoding API error: {}", e.getMessage(), e);
                return null;
            } catch (Exception e) {
                logger.error("Error geocoding address to coordinates: {}", address, e);
                return null;
            }
        });
    }

    @Override
    public boolean isAvailable() {
        // Reset counter daily
        long now = System.currentTimeMillis();
        if (now - lastResetTime > 24 * 60 * 60 * 1000) {
            queriesToday = 0;
            lastResetTime = now;
        }

        return context != null && queriesToday < dailyLimit;
    }

    private Address convertToAddress(GeocodingResult result) {
        return Address.builder()
            .streetAddress(getComponent(result, "street_address"))
            .city(getComponent(result, "locality"))
            .state(getComponent(result, "administrative_area_level_1"))
            .postalCode(getComponent(result, "postal_code"))
            .country(getComponent(result, "country"))
            .countryCode(getComponent(result, "country_short"))
            .administrativeAreaLevel1(getComponent(result, "administrative_area_level_1"))
            .administrativeAreaLevel2(getComponent(result, "administrative_area_level_2"))
            .locality(getComponent(result, "locality"))
            .sublocality(getComponent(result, "sublocality"))
            .premise(getComponent(result, "premise"))
            .subpremise(getComponent(result, "subpremise"))
            .formattedAddress(result.formattedAddress)
            .build();
    }

    private String getComponent(GeocodingResult result, String type) {
        return Arrays.stream(result.addressComponents)
            .filter(comp -> Arrays.asList(comp.types).contains(type))
            .findFirst()
            .map(comp -> comp.longName)
            .orElse(null);
    }

    private void incrementQueryCount() {
        queriesToday++;
        if (queriesToday >= dailyLimit) {
            logger.warn("Google Geocoding daily limit reached: {}", dailyLimit);
        }
    }
}