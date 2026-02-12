package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudCase;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudCaseRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FraudCaseRepositoryAdapter implements FraudCaseRepositoryPort {

    private final List<FraudCase> cases = new ArrayList<>();

    @Override
    public FraudCase save(String tenantId, FraudCase caseEntity) {
        cases.add(caseEntity);
        return caseEntity;
    }

    @Override
    public Optional<FraudCase> findById(String tenantId, UUID id) {
        return cases.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<FraudCase> findByCaseNumber(String tenantId, String caseNumber) {
        return cases.stream().filter(c -> c.getCaseNumber().equals(caseNumber)).findFirst();
    }

    @Override
    public List<FraudCase> findByTenantId(String tenantId) {
        return cases.stream().filter(c -> c.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public List<FraudCase> findByStatus(String tenantId, String status) {
        return cases.stream().filter(c -> c.getTenantId().equals(tenantId) && c.getStatus().equals(status)).toList();
    }

    @Override
    public List<FraudCase> findByAssignedTo(String tenantId, String assignedTo) {
        return cases.stream().filter(c -> c.getTenantId().equals(tenantId) && assignedTo.equals(c.getAssignedTo())).toList();
    }

    @Override
    public List<FraudCase> findByPriority(String tenantId, String priority) {
        return cases.stream().filter(c -> c.getTenantId().equals(tenantId) && c.getPriority().equals(priority)).toList();
    }

    @Override
    public List<FraudCase> findOpenCases(String tenantId) {
        return cases.stream().filter(c -> c.getTenantId().equals(tenantId) && c.isOpen()).toList();
    }

    @Override
    public void delete(String tenantId, UUID id) {
        cases.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return cases.stream().anyMatch(c -> c.getId().equals(id));
    }
}
