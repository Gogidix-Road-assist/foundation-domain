package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.DetectionRule;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.DetectionRuleRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.DetectionRuleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of DetectionRuleRepositoryPort.
 */
@Repository
@RequiredArgsConstructor
public class DetectionRuleRepositoryImpl implements DetectionRuleRepositoryPort {

    private final DetectionRuleMongoRepository mongoRepository;

    @Override
    public DetectionRule save(String tenantId, DetectionRule rule) {
        DetectionRuleEntity entity = toEntity(rule);
        entity.setTenantId(tenantId);
        DetectionRuleEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<DetectionRule> findById(String tenantId, UUID ruleId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(ruleId))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<DetectionRule> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectionRule> findActiveRules(String tenantId) {
        return mongoRepository.findByTenantIdAndIsActive(tenantId, true).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectionRule> findByCategory(String tenantId, String category) {
        return mongoRepository.findByTenantIdAndCategory(tenantId, category).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectionRule> findByDataSource(String tenantId, String dataSource) {
        return mongoRepository.findByTenantIdAndDataSource(tenantId, dataSource).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectionRule> findByPriorityBetween(String tenantId, Integer minPriority, Integer maxPriority) {
        return mongoRepository.findByTenantIdAndPriorityBetween(tenantId, minPriority, maxPriority).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DetectionRule> findHighPriorityRules(String tenantId) {
        return mongoRepository.findHighPriorityRules(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID ruleId) {
        mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(ruleId))
                .findFirst()
                .ifPresent(mongoRepository::delete);
    }

    @Override
    public boolean exists(String tenantId, UUID ruleId) {
        return mongoRepository.existsByTenantIdAndUuid(tenantId, ruleId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countActiveRules(String tenantId) {
        return mongoRepository.countByTenantIdAndIsActive(tenantId, true);
    }

    private DetectionRuleEntity toEntity(DetectionRule domain) {
        return DetectionRuleEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .ruleType(domain.getRuleType())
                .patternIds(domain.getPatternIds())
                .conditions(domain.getConditions())
                .dataSource(domain.getDataSource())
                .priority(domain.getPriority())
                .isActive(domain.getIsActive())
                .createAlert(domain.getCreateAlert())
                .alertSeverity(domain.getAlertSeverity())
                .notificationChannels(domain.getNotificationChannels())
                .category(domain.getCategory())
                .metadata(domain.getMetadata())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
    }

    private DetectionRule toDomain(DetectionRuleEntity entity) {
        return DetectionRule.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .ruleType(entity.getRuleType())
                .patternIds(entity.getPatternIds())
                .conditions(entity.getConditions())
                .dataSource(entity.getDataSource())
                .priority(entity.getPriority())
                .isActive(entity.getIsActive())
                .createAlert(entity.getCreateAlert())
                .alertSeverity(entity.getAlertSeverity())
                .notificationChannels(entity.getNotificationChannels())
                .category(entity.getCategory())
                .metadata(entity.getMetadata())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
}
