package com.gogidix.rapidassist.tenancy.configuration.service.application.dto.response;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.model.TenantConfig;

import java.time.Instant;
import java.util.Map;

/**
 * Response DTO for Tenant Configuration entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record TenantConfigResponseDto(
    String id,
    String tenantId,
    String name,
    String domain,
    String environment,
    Integer version,
    boolean active,
    String timezone,
    String locale,
    String currency,
    String dateFormat,
    String timeFormat,
    Map<String, String> customSettings,
    int maxUsers,
    long maxStorageGB,
    int maxRequestsPerMinute,
    String subscriptionExpiry,
    java.util.Set<String> enabledFeatures,
    java.util.Set<String> disabledFeatures,
    Map<String, String> featureConfig,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {
    /**
     * Converts a domain TenantConfig entity to a Response DTO.
     *
     * @param tenantConfig the domain entity
     * @return the response DTO
     */
    public static TenantConfigResponseDto fromDomain(TenantConfig tenantConfig) {
        return new TenantConfigResponseDto(
            tenantConfig.id(),
            tenantConfig.tenantId(),
            tenantConfig.name(),
            tenantConfig.domain(),
            tenantConfig.environment(),
            tenantConfig.version(),
            tenantConfig.active(),
            tenantConfig.settings().timezone(),
            tenantConfig.settings().locale(),
            tenantConfig.settings().currency(),
            tenantConfig.settings().dateFormat(),
            tenantConfig.settings().timeFormat(),
            tenantConfig.settings().customSettings(),
            tenantConfig.limits().maxUsers(),
            tenantConfig.limits().maxStorageGB(),
            tenantConfig.limits().maxRequestsPerMinute(),
            tenantConfig.limits().subscriptionExpiry() != null
                ? tenantConfig.limits().subscriptionExpiry().toString()
                : null,
            tenantConfig.features().enabledFeatures(),
            tenantConfig.features().disabledFeatures(),
            tenantConfig.features().featureConfig(),
            tenantConfig.createdBy(),
            tenantConfig.createdAt(),
            tenantConfig.updatedBy(),
            tenantConfig.updatedAt()
        );
    }
}
