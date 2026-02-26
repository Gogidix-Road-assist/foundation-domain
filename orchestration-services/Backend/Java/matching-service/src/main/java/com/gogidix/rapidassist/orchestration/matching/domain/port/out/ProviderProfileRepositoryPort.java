package com.gogidix.rapidassist.orchestration.matching.domain.port.out;

import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;

import java.util.List;
import java.util.Optional;

/**
 * Output port for ProviderProfile repository operations
 */
public interface ProviderProfileRepositoryPort {

    ProviderProfile save(ProviderProfile profile);

    Optional<ProviderProfile> findById(String id);

    Optional<ProviderProfile> findByProviderId(String providerId);

    List<ProviderProfile> findByTenantId(String tenantId);

    List<ProviderProfile> findByStatus(ProviderProfile.ProviderStatus status);

    List<ProviderProfile> findByStatusAndIsActive(
        ProviderProfile.ProviderStatus status,
        Boolean isActive
    );

    List<ProviderProfile> findProvidersNearLocation(
        Double longitude,
        Double latitude,
        Double radiusKm
    );

    List<ProviderProfile> findByCapabilitiesContaining(String capability);

    List<ProviderProfile> findActiveProviders();

    void deleteById(String id);

    void deleteByProviderId(String providerId);

    boolean existsByProviderId(String providerId);

    List<ProviderProfile> findByProviderIdIn(List<String> providerIds);

    List<ProviderProfile> findByTenantIdAndStatus(
        String tenantId,
        ProviderProfile.ProviderStatus status
    );
}
