package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.application.port.out.GatewayConfigRepositoryPort;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayConfig;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayStatus;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.GatewayConfigEntity;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper.GatewayConfigPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of GatewayConfigRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class GatewayConfigRepositoryImpl implements GatewayConfigRepositoryPort {

    private final SpringDataGatewayConfigRepository springDataRepository;
    private final GatewayConfigPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public GatewayConfig save(GatewayConfig config) {
        log.info("Saving gateway config: {} for tenant: {}", config.getId(), config.getTenantId());

        GatewayConfigEntity entity = persistenceMapper.toEntity(config);
        GatewayConfigEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GatewayConfig> findById(UUID id) {
        log.debug("Finding gateway config by ID: {}", id);

        return springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GatewayConfig> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding gateway config by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GatewayConfig> findByTenantId(String tenantId) {
        log.debug("Finding all gateway configs for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GatewayConfig> findByTenantIdAndStatus(String tenantId, GatewayStatus status) {
        log.debug("Finding gateway configs for tenant: {} with status: {}", tenantId, status);

        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GatewayConfig> findByTenantIdAndConfigName(String tenantId, String configName) {
        log.debug("Finding gateway config by name: {} for tenant: {}", configName, tenantId);

        return springDataRepository.findByTenantIdAndConfigName(tenantId, configName)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting gateway config by ID: {}", id);

        springDataRepository.findAll().stream()
                .filter(e -> e.getUuid().equals(id))
                .findFirst()
                .ifPresent(entity -> springDataRepository.delete(entity));
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting gateway config: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }
}
