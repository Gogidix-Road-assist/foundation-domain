package com.gogidix.rapidassist.orchestration.matching.interfaces.rest;

import com.gogidix.rapidassist.orchestration.matching.application.dto.request.CreateProviderProfileDTO;
import com.gogidix.rapidassist.orchestration.matching.application.service.ProviderManagementService;
import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for provider management operations
 */
@Slf4j
@RestController
@RequestMapping("/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderManagementService providerService;

    /**
     * Register a new provider
     */
    @PostMapping
    public ResponseEntity<ProviderProfile> registerProvider(
        @Valid @RequestBody CreateProviderProfileDTO providerDTO
    ) {
        log.info("Registering provider: {}", providerDTO.getProviderId());

        ProviderProfile profile = mapToProviderProfile(providerDTO);
        ProviderProfile savedProfile = providerService.registerProvider(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProfile);
    }

    /**
     * Get provider by ID
     */
    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderProfile> getProvider(@PathVariable String providerId) {
        ProviderProfile profile = providerService.getProvider(providerId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update provider location
     */
    @PutMapping("/{providerId}/location")
    public ResponseEntity<Void> updateProviderLocation(
        @PathVariable String providerId,
        @RequestBody LocationUpdateDTO locationDTO
    ) {
        log.info("Updating location for provider: {}", providerId);
        providerService.updateProviderLocation(
            providerId,
            locationDTO.getLongitude(),
            locationDTO.getLatitude()
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * Update provider status
     */
    @PutMapping("/{providerId}/status")
    public ResponseEntity<Void> updateProviderStatus(
        @PathVariable String providerId,
        @RequestParam ProviderProfile.ProviderStatus status
    ) {
        log.info("Updating status for provider: {} to {}", providerId, status);
        providerService.updateProviderStatus(providerId, status);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find providers near location
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<ProviderProfile>> findProvidersNearby(
        @RequestParam Double longitude,
        @RequestParam Double latitude,
        @RequestParam(defaultValue = "50.0") Double radiusKm
    ) {
        List<ProviderProfile> providers = providerService.findProvidersNearLocation(
            longitude, latitude, radiusKm
        );
        return ResponseEntity.ok(providers);
    }

    /**
     * Find providers by capability
     */
    @GetMapping("/capability/{capability}")
    public ResponseEntity<List<ProviderProfile>> findProvidersByCapability(
        @PathVariable String capability
    ) {
        List<ProviderProfile> providers = providerService.findProvidersByCapability(capability);
        return ResponseEntity.ok(providers);
    }

    /**
     * Deactivate provider
     */
    @PostMapping("/{providerId}/deactivate")
    public ResponseEntity<Void> deactivateProvider(@PathVariable String providerId) {
        log.info("Deactivating provider: {}", providerId);
        providerService.deactivateProvider(providerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get provider statistics
     */
    @GetMapping("/{providerId}/statistics")
    public ResponseEntity<ProviderProfile> getProviderStatistics(@PathVariable String providerId) {
        ProviderProfile stats = providerService.getProviderStatistics(providerId);
        return ResponseEntity.ok(stats);
    }

    private ProviderProfile mapToProviderProfile(CreateProviderProfileDTO dto) {
        MatchingRequest.Location location = MatchingRequest.Location.builder()
            .coordinates(dto.getCurrentLocation().getCoordinates())
            .type("Point")
            .address(dto.getCurrentLocation().getAddress())
            .city(dto.getCurrentLocation().getCity())
            .state(dto.getCurrentLocation().getState())
            .postalCode(dto.getCurrentLocation().getPostalCode())
            .country(dto.getCurrentLocation().getCountry())
            .build();

        return ProviderProfile.builder()
            .providerId(dto.getProviderId())
            .providerName(dto.getProviderName())
            .currentLocation(location)
            .capabilities(dto.getCapabilities())
            .status(dto.getStatus() != null ? dto.getStatus() : ProviderProfile.ProviderStatus.AVAILABLE)
            .baseRate(dto.getBaseRate())
            .ratePerKm(dto.getRatePerKm())
            .rating(dto.getRating())
            .maxConcurrentJobs(dto.getMaxConcurrentJobs())
            .averageResponseTimeMinutes(dto.getAverageResponseTimeMinutes())
            .capabilitySpecializationScores(dto.getCapabilitySpecializationScores())
            .priorityLevel(dto.getPriorityLevel())
            .isVerified(dto.getIsVerified())
            .isActive(true)
            .currentJobsCount(0)
            .build();
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    private static class LocationUpdateDTO {
        private Double longitude;
        private Double latitude;
    }
}
