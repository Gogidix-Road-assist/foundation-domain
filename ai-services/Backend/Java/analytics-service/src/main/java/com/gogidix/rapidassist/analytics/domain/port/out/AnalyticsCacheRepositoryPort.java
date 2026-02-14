package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.AnalyticsCache;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for AnalyticsCache operations.
 * Following hexagonal architecture principles.
 */
public interface AnalyticsCacheRepositoryPort {

    AnalyticsCache save(String tenantId, AnalyticsCache cache);

    Optional<AnalyticsCache> findByCacheKey(String tenantId, String cacheKey);

    List<AnalyticsCache> findByTenantId(String tenantId);

    List<AnalyticsCache> findByTenantIdAndCacheType(String tenantId, String cacheType);

    List<AnalyticsCache> findByTenantIdAndStatus(String tenantId, String status);

    List<AnalyticsCache> findExpiredCache(String tenantId, LocalDateTime before);

    boolean existsByCacheKey(String tenantId, String cacheKey);

    void deleteByCacheKey(String tenantId, String cacheKey);

    void deleteExpiredCache(String tenantId, LocalDateTime before);

    void clearCache(String tenantId);
}
