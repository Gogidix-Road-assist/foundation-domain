package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence;

import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationRuleRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationRuleDocument;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository.SpringDataModerationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of ModerationRuleRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ModerationRuleRepositoryImpl implements ModerationRuleRepositoryPort {

    private final SpringDataModerationRuleRepository springRepository;

    @Override
    public ModerationRule save(ModerationRule rule) {
        ModerationRuleDocument document = toDocument(rule);
        ModerationRuleDocument saved = springRepository.save(document);
        return toDomain(saved);
    }

    @Override
    public Optional<ModerationRule> findById(String id) {
        return springRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public List<ModerationRule> findActiveByTenantId(String tenantId) {
        return springRepository.findByTenantIdAndActiveTrue(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationRule> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationRule> findByTenantIdAndType(String tenantId, ModerationRule.RuleType type) {
        return springRepository.findByTenantIdAndRuleType(tenantId, type).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String id) {
        springRepository.deleteById(id);
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return springRepository.existsByTenantIdAndName(tenantId, name);
    }

    private ModerationRuleDocument toDocument(ModerationRule domain) {
        return ModerationRuleDocument.builder()
            .id(domain.getId())
            .tenantId(domain.getTenantId())
            .name(domain.getName())
            .description(domain.getDescription())
            .ruleType(domain.getRuleType())
            .severity(domain.getSeverity())
            .keywords(domain.getKeywords())
            .patterns(domain.getPatterns())
            .metadata(domain.getMetadata())
            .active(domain.isActive())
            .priority(domain.getPriority())
            .createdBy(domain.getCreatedBy())
            .createdAt(domain.getCreatedAt())
            .updatedBy(domain.getUpdatedBy())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    private ModerationRule toDomain(ModerationRuleDocument document) {
        return ModerationRule.builder()
            .id(document.getId())
            .tenantId(document.getTenantId())
            .name(document.getName())
            .description(document.getDescription())
            .ruleType(document.getRuleType())
            .severity(document.getSeverity())
            .keywords(document.getKeywords())
            .patterns(document.getPatterns())
            .metadata(document.getMetadata())
            .active(document.isActive())
            .priority(document.getPriority())
            .createdBy(document.getCreatedBy())
            .createdAt(document.getCreatedAt())
            .updatedBy(document.getUpdatedBy())
            .updatedAt(document.getUpdatedAt())
            .build();
    }
}
