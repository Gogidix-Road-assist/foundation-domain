package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TaggingRuleRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TaggingRuleEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataTaggingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of TaggingRuleRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class TaggingRuleRepositoryImpl implements TaggingRuleRepositoryPort {

    private final SpringDataTaggingRuleRepository springDataRepository;

    @Override
    public TaggingRule save(TaggingRule rule) {
        TaggingRuleEntity entity = toEntity(rule);
        TaggingRuleEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<TaggingRule> findById(UUID id) {
        return springDataRepository.findByUuid(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<TaggingRule> findByTenantIdAndId(String tenantId, UUID id) {
        TaggingRuleEntity entity = springDataRepository.findByTenantIdAndUuid(tenantId, id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public List<TaggingRule> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaggingRule> findByTenantIdAndStatus(String tenantId, TaggingRule.RuleStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaggingRule> findByTenantIdAndStatusAndRuleType(String tenantId, TaggingRule.RuleStatus status, TaggingRule.RuleType ruleType) {
        return springDataRepository.findByTenantIdAndStatusAndRuleType(tenantId, status, ruleType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaggingRule> findByTenantIdAndPriority(String tenantId, TaggingRule.RulePriority priority) {
        return springDataRepository.findByTenantIdAndPriority(tenantId, priority).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaggingRule> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name) {
        return springDataRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return springDataRepository.existsByTenantIdAndName(tenantId, name);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public void deleteByTenantIdAndId(String tenantId, UUID id) {
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, TaggingRule.RuleStatus status) {
        return springDataRepository.countByTenantIdAndStatus(tenantId, status);
    }

    private TaggingRule toDomain(TaggingRuleEntity entity) {
        if (entity == null) return null;
        return TaggingRule.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .ruleType(entity.getRuleType())
                .condition(entity.getCondition())
                .tagIds(entity.getTagIds())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .confidenceThreshold(entity.getConfidenceThreshold())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .lastExecutedAt(entity.getLastExecutedAt())
                .executionCount(entity.getExecutionCount())
                .version(entity.getVersion())
                .build();
    }

    private TaggingRuleEntity toEntity(TaggingRule domain) {
        if (domain == null) return null;
        return TaggingRuleEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .ruleType(domain.getRuleType())
                .condition(domain.getCondition())
                .tagIds(domain.getTagIds())
                .priority(domain.getPriority())
                .status(domain.getStatus())
                .confidenceThreshold(domain.getConfidenceThreshold())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .lastExecutedAt(domain.getLastExecutedAt())
                .executionCount(domain.getExecutionCount())
                .version(domain.getVersion())
                .build();
    }
}
