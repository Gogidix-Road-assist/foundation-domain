package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudPattern;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudPatternRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FraudPatternRepositoryAdapter implements FraudPatternRepositoryPort {

    private final List<FraudPattern> patterns = new ArrayList<>();

    @Override
    public FraudPattern save(String tenantId, FraudPattern pattern) {
        // Remove existing pattern with same ID if it exists (for updates)
        if (pattern.getId() != null) {
            patterns.removeIf(p -> pattern.getId().equals(p.getId()));
        }
        patterns.add(pattern);
        return pattern;
    }

    @Override
    public Optional<FraudPattern> findById(String tenantId, UUID id) {
        return patterns.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public List<FraudPattern> findByTenantId(String tenantId) {
        return patterns.stream().filter(p -> p.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public List<FraudPattern> findByPatternType(String tenantId, String patternType) {
        return patterns.stream().filter(p -> p.getTenantId().equals(tenantId) && p.getPatternType().equals(patternType)).toList();
    }

    @Override
    public List<FraudPattern> findActivePatterns(String tenantId) {
        return patterns.stream().filter(p -> p.getTenantId().equals(tenantId) && p.isActive()).toList();
    }

    @Override
    public void delete(String tenantId, UUID id) {
        patterns.removeIf(p -> p.getId().equals(id));
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return patterns.stream().anyMatch(p -> p.getId().equals(id));
    }
}
