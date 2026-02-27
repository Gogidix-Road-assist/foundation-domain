package com.gogidix.rapidassist.integration.adapters.service.domain.port.out;

import com.gogidix.rapidassist.integration.adapters.service.domain.model.IntegrationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntegrationConfigRepository extends JpaRepository<IntegrationConfig, String> {

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId")
    List<IntegrationConfig> findByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.id = :id")
    Optional<IntegrationConfig> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") String id);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.provider = :provider")
    Optional<IntegrationConfig> findByTenantIdAndProvider(@Param("tenantId") String tenantId, @Param("provider") String provider);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.enabled = :enabled")
    List<IntegrationConfig> findByTenantIdAndEnabled(@Param("tenantId") String tenantId, @Param("enabled") Boolean enabled);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND (i.provider LIKE %:search% OR i.providerName LIKE %:search%)")
    List<IntegrationConfig> searchByTenantId(@Param("tenantId") String tenantId, @Param("search") String search);

    @Query("SELECT COUNT(i) FROM IntegrationConfig i WHERE i.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.provider = :provider AND i.id != :id")
    Optional<IntegrationConfig> findByTenantIdAndProviderExcludingId(@Param("tenantId") String tenantId, @Param("provider") String provider, @Param("id") String id);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.syncStatus = :status")
    List<IntegrationConfig> findByTenantIdAndSyncStatus(@Param("tenantId") String tenantId, @Param("status") String status);

    @Query("SELECT i FROM IntegrationConfig i WHERE i.tenantId = :tenantId AND i.enabled = true ORDER BY i.lastSyncAt NULLS LAST")
    List<IntegrationConfig> findEnabledByTenantIdOrderByLastSync(@Param("tenantId") String tenantId);

    @Override
    default List<IntegrationConfig> findAll() {
        throw new UnsupportedOperationException("Use findByTenantId(String tenantId) - tenant filtering required");
    }

    @Override
    default Optional<IntegrationConfig> findById(String id) {
        throw new UnsupportedOperationException("Use findByTenantIdAndId(String tenantId, String id) - tenant filtering required");
    }
}
