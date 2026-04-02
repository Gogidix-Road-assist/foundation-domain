package com.gogidix.rapidassist.ai.summization.domain.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.SummaryConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SummaryConfig.
 */
public interface SummaryConfigRepositoryPort {

    SummaryConfig save(String tenantId, SummaryConfig config);

    Optional<SummaryConfig> findById(String tenantId, UUID configId);

    List<SummaryConfig> findByTenantId(String tenantId);

    List<SummaryConfig> findAllActiveByTenantId(String tenantId);

    void delete(String tenantId, UUID configId);

    boolean exists(String tenantId, UUID configId);

    Optional<SummaryConfig> findDefaultConfig(String tenantId);
}
