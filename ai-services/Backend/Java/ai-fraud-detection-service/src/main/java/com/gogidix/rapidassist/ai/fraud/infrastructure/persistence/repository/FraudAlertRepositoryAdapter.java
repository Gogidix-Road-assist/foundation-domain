package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudAlertRepositoryPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory adapter for FraudAlertRepositoryPort (for development)
 */
@Repository
@Profile("dev | default")
public class FraudAlertRepositoryAdapter implements FraudAlertRepositoryPort {

    private final List<FraudAlert> alerts = new ArrayList<>();

    @Override
    public FraudAlert save(String tenantId, FraudAlert alert) {
        alerts.add(alert);
        return alert;
    }

    @Override
    public Optional<FraudAlert> findById(String tenantId, UUID id) {
        return alerts.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    @Override
    public List<FraudAlert> findByTenantId(String tenantId) {
        return alerts.stream().filter(a -> a.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public List<FraudAlert> findByStatus(String tenantId, String status) {
        return alerts.stream().filter(a -> a.getTenantId().equals(tenantId) && a.getStatus().equals(status)).toList();
    }

    @Override
    public List<FraudAlert> findBySeverity(String tenantId, String severity) {
        return alerts.stream().filter(a -> a.getTenantId().equals(tenantId) && a.getSeverity().equals(severity)).toList();
    }

    @Override
    public List<FraudAlert> findByAssignedTo(String tenantId, String assignedTo) {
        return alerts.stream().filter(a -> a.getTenantId().equals(tenantId) && assignedTo.equals(a.getAssignedTo())).toList();
    }

    @Override
    public List<FraudAlert> findPendingAlerts(String tenantId) {
        return findByStatus(tenantId, "PENDING");
    }

    @Override
    public List<FraudAlert> findCriticalAlerts(String tenantId) {
        return findBySeverity(tenantId, "CRITICAL");
    }

    @Override
    public void delete(String tenantId, UUID id) {
        alerts.removeIf(a -> a.getId().equals(id));
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return alerts.stream().anyMatch(a -> a.getId().equals(id));
    }
}
