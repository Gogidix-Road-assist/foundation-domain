package com.gogidix.rapidassist.geo.location.service.infrastructure.provider;

import com.gogidix.rapidassist.geo.location.service.domain.model.Address;
import com.gogidix.rapidassist.geo.location.service.domain.model.Coordinates;
import com.gogidix.rapidassist.geo.location.service.domain.model.Location;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class MaxMindGeoLocationProvider {

    private static final Logger logger = LoggerFactory.getLogger(MaxMindGeoLocationProvider.class);

    @Value("${gogidix.geolocation.maxmind.database-path:classpath:GeoLite2-City.mmdb}")
    private Resource databaseResource;

    @Value("${gogidix.geolocation.maxmind.cache-size:4096}")
    private int cacheSize;

    private DatabaseReader reader;
    private final ResourceLoader resourceLoader;

    public MaxMindGeoLocationProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:GeoLite2-City.mmdb");

            if (resource.exists()) {
                try (InputStream database = resource.getInputStream()) {
                    this.reader = new DatabaseReader.Builder(database)
                        .withCache(new CHMCache(cacheSize))
                        .build();
                    logger.info("MaxMind GeoIP2 database loaded successfully");
                }
            } else {
                // Try to load from file system
                logger.info("GeoLite2-City.mmdb not found in classpath, checking file system...");
                // Could load from external path if configured
            }
        } catch (IOException e) {
            logger.error("Failed to load MaxMind GeoIP2 database", e);
        }
    }

    public Optional<Location> getLocationFromIp(String tenantId, String ipAddress) {
        if (reader == null) {
            logger.debug("MaxMind reader not initialized");
            return Optional.empty();
        }

        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(address);

            if (response != null && response.getLocation() != null) {
                Coordinates coordinates = Coordinates.of(
                    response.getLocation().getLatitude(),
                    response.getLocation().getLongitude(),
                    response.getLocation().getAccuracyRadius().doubleValue() / 1000.0 // Convert to km
                );

                Address addressDetails = Address.builder()
                    .city(response.getCity() != null ? response.getCity().getName() : null)
                    .state(response.getSubdivisions().size() > 0 ? response.getSubdivisions().get(0).getName() : null)
                    .postalCode(response.getPostal() != null ? response.getPostal().getCode() : null)
                    .country(response.getCountry() != null ? response.getCountry().getName() : null)
                    .countryCode(response.getCountry() != null ? response.getCountry().getIsoCode() : null)
                    .build();

                Location.LocationAccuracy accuracy = Location.LocationAccuracy.CITY;
                if (response.getLocation().getAccuracyRadius() != null) {
                    double radiusMeters = response.getLocation().getAccuracyRadius().doubleValue();
                    if (radiusMeters < 100) {
                        accuracy = Location.LocationAccuracy.PRECISE;
                    } else if (radiusMeters < 1000) {
                        accuracy = Location.LocationAccuracy.APPROXIMATE;
                    } else if (radiusMeters < 10000) {
                        accuracy = Location.LocationAccuracy.NEIGHBORHOOD;
                    }
                }

                // Create Location with the calculated accuracy
                Location location = new Location(
                    generateLocationId(),
                    tenantId,
                    coordinates,
                    addressDetails,
                    accuracy,
                    Location.LocationSource.IP_GEOLOCATION,
                    Instant.now(),
                    null,
                    null,
                    ipAddress,
                    Map.of(),
                    Location.GeofenceStatus.UNKNOWN,
                    null
                );
                return Optional.of(location);
            }

        } catch (GeoIp2Exception e) {
            logger.debug("GeoIP2 lookup failed for IP {}: {}", ipAddress, e.getMessage());
        } catch (Exception e) {
            logger.error("Error looking up IP address: {}", ipAddress, e);
        }

        return Optional.empty();
    }

    public boolean isAvailable() {
        return reader != null;
    }

    private static String generateLocationId() {
        return "loc_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }
}