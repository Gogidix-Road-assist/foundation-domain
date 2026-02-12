package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.CampaignEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for CampaignEntity.
 */
@Repository
public interface SpringDataCampaignRepository extends MongoRepository<CampaignEntity, String> {

    Optional<CampaignEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<CampaignEntity> findByCampaignCodeAndTenantId(String campaignCode, String tenantId);

    List<CampaignEntity> findByTenantId(String tenantId);

    List<CampaignEntity> findByCampaignTypeAndTenantId(String campaignType, String tenantId);

    List<CampaignEntity> findByStatusAndTenantId(String status, String tenantId);

    List<CampaignEntity> findByCategoryAndTenantId(String category, String tenantId);

    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE', 'startDate': { $lte: ?1 }, '$or': [ { 'endDate': null }, { 'endDate': { $gte: ?1 } } ] }")
    List<CampaignEntity> findActiveCampaigns(String tenantId, LocalDateTime dateTime);

    @Query("{ 'tenantId': ?0, 'startDate': { $lte: ?1 }, 'endDate': { $gte: ?1 } }")
    List<CampaignEntity> findScheduledCampaigns(String tenantId, LocalDateTime dateTime);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    boolean existsByCampaignCodeAndTenantId(String campaignCode, String tenantId);

    long countByTenantId(String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
