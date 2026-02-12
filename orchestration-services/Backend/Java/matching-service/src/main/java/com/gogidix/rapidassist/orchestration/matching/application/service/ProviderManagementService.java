package com.gogidix.rapidassist.orchestration.matching.application.service;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingRequest;
import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.port.in.ProviderManagementPort;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.ProviderProfileRepositoryPort;
import com.gogidix.rapidassist.orchestration.matching.shared.exception.ProviderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application service for provider management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderManagementService implements ProviderManagementPort {

    private final ProviderProfileRepositoryPort providerRepository;

    @Override
    @Transactional
    public ProviderProfile registerProvider(ProviderProfile profile) {
        log.info("Registering provider: {}", profile.getProviderId());

        // TODO: Extract tenant ID from request context when shared library is available
        if (profile.getTenantId() == null) {
            profile.setTenantId("default-tenant");
        }
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        profile.setIsActive(true);
        profile.setVersion(1);

        return providerRepository.save(profile);
    }

    @Override
    @Transactional
    public ProviderProfile updateProvider(String providerId, ProviderProfile profile) {
        log.info("Updating provider: {}", providerId);

        ProviderProfile existing = providerRepository.findByProviderId(providerId)
            .orElseThrow(() -> new ProviderNotFoundException(providerId));

        profile.setId(existing.getId());
        profile.setProviderId(providerId);
        profile.setTenantId(existing.getTenantId());
        profile.setUpdatedAt(LocalDateTime.now());
        profile.setVersion(existing.getVersion() + 1);

        return providerRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateProviderLocation(String providerId, Double longitude, Double latitude) {
        log.info("Updating location for provider: {}", providerId);

        ProviderProfile provider = providerRepository.findByProviderId(providerId)
            .orElseThrow(() -> new ProviderNotFoundException(providerId));

        MatchingRequest.Location location = MatchingRequest.Location.builder()
            .coordinates(new Double[]{longitude, latitude})
            .type("Point")
            .build();

        provider.setCurrentLocation(location);
        provider.setLastActiveAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());

        providerRepository.save(provider);
    }

    @Override
    @Transactional
    public void updateProviderStatus(String providerId, ProviderProfile.ProviderStatus status) {
        log.info("Updating status for provider: {} to {}", providerId, status);

        ProviderProfile provider = providerRepository.findByProviderId(providerId)
            .orElseThrow(() -> new ProviderNotFoundException(providerId));

        provider.setStatus(status);
        provider.setLastActiveAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());

        providerRepository.save(provider);
    }

    @Override
    public ProviderProfile getProvider(String providerId) {
        return providerRepository.findByProviderId(providerId)
            .orElseThrow(() -> new ProviderNotFoundException(providerId));
    }

    @Override
    public List<ProviderProfile> findProvidersNearLocation(Double longitude, Double latitude, Double radiusKm) {
        return providerRepository.findProvidersNearLocation(longitude, latitude, radiusKm);
    }

    @Override
    public List<ProviderProfile> findProvidersByCapability(String capability) {
        return providerRepository.findByCapabilitiesContaining(capability);
    }

    @Override
    @Transactional
    public void deactivateProvider(String providerId) {
        log.info("Deactivating provider: {}", providerId);

        ProviderProfile provider = providerRepository.findByProviderId(providerId)
            .orElseThrow(() -> new ProviderNotFoundException(providerId));

        provider.setIsActive(false);
        provider.setUpdatedAt(LocalDateTime.now());

        providerRepository.save(provider);
    }

    @Override
    public ProviderProfile getProviderStatistics(String providerId) {
        return getProvider(providerId);
    }
}
