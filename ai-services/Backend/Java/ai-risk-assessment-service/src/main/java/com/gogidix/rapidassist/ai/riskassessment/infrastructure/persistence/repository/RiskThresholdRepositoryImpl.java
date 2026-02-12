package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskThreshold;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskThresholdRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskThresholdEntity;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper.RiskThresholdPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RiskThresholdRepositoryPort
 * Bridges domain layer with infrastructure persistence layer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskThresholdRepositoryImpl implements RiskThresholdRepositoryPort {

    private final SpringDataRiskThresholdRepository springDataRepository;
    private final RiskThresholdPersistenceMapper mapper;

    @Override
    public RiskThreshold save(RiskThreshold threshold) {
        log.debug("Saving risk threshold: {}", threshold.getId());
        RiskThresholdEntity entity = mapper.toEntity(threshold);
        RiskThresholdEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RiskThreshold> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding risk threshold by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RiskThreshold> findByNameAndTenantId(String name, String tenantId) {
        log.debug("Finding risk threshold by name: {} for tenant: {}", name, tenantId);
        return springDataRepository.findByNameAndTenantId(name, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RiskThreshold> findByTenantId(String tenantId) {
        log.debug("Finding all risk thresholds for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskThreshold> findActiveByTenantId(String tenantId) {
        log.debug("Finding active risk thresholds for tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndActive(tenantId, true).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RiskThreshold> findByCategoryAndTenantId(RiskCategory category, String tenantId) {
        log.debug("Finding risk threshold by category: {} for tenant: {}", category, tenantId);
        return springDataRepository.findByCategoryAndTenantId(category, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RiskThreshold> findDefaultByTenantId(String tenantId) {
        log.debug("Finding default risk threshold for tenant: {}", tenantId);
        return springDataRepository.findFirstByTenantIdOrderByCreatedAtAsc(tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting risk threshold: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
