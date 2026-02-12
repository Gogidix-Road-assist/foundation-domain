package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.entity.FraudDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for FraudDetectionEntity
 */
@Repository
public interface SpringDataFraudDetectionRepository extends MongoRepository<FraudDetectionEntity, UUID> {

    List<FraudDetectionEntity> findByTenantId(String tenantId);
    List<FraudDetectionEntity> findByTenantIdAndEntityId(String tenantId, String entityId);
    List<FraudDetectionEntity> findByTenantIdAndRiskLevel(String tenantId, FraudRiskLevel riskLevel);
    List<FraudDetectionEntity> findByTenantIdAndStatus(String tenantId, String status);
    List<FraudDetectionEntity> findByTenantIdAndRequiresReviewTrue(String tenantId);
}
