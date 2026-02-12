package com.gogidix.rapidassist.orchestration.matching.domain.port.in;

import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;

import java.util.List;

/**
 * Input port for provider management operations
 */
public interface ProviderManagementPort {

    /**
     * Register a provider profile
     */
    ProviderProfile registerProvider(ProviderProfile profile);

    /**
     * Update provider profile
     */
    ProviderProfile updateProvider(String providerId, ProviderProfile profile);

    /**
     * Update provider location
     */
    void updateProviderLocation(String providerId, Double longitude, Double latitude);

    /**
     * Update provider status
     */
    void updateProviderStatus(String providerId, ProviderProfile.ProviderStatus status);

    /**
     * Get provider by ID
     */
    ProviderProfile getProvider(String providerId);

    /**
     * Find providers by location
     */
    List<ProviderProfile> findProvidersNearLocation(Double longitude, Double latitude, Double radiusKm);

    /**
     * Find providers by capability
     */
    List<ProviderProfile> findProvidersByCapability(String capability);

    /**
     * Deactivate provider
     */
    void deactivateProvider(String providerId);

    /**
     * Get provider statistics
     */
    ProviderProfile getProviderStatistics(String providerId);
}
