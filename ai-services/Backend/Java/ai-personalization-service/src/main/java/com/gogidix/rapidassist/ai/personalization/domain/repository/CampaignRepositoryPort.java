package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.Campaign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving Campaign entities.
 */
public interface CampaignRepositoryPort {

    /**
     * Save a campaign (create or update).
     */
    Campaign save(String tenantId, Campaign campaign);

    /**
     * Find a campaign by ID and tenant.
     */
    Optional<Campaign> findById(String tenantId, UUID campaignId);

    /**
     * Find a campaign by code and tenant.
     */
    Optional<Campaign> findByCampaignCode(String tenantId, String campaignCode);

    /**
     * Find all campaigns for a tenant.
     */
    List<Campaign> findByTenantId(String tenantId);

    /**
     * Find campaigns by type and tenant.
     */
    List<Campaign> findByCampaignType(String tenantId, String campaignType);

    /**
     * Find campaigns by status and tenant.
     */
    List<Campaign> findByStatus(String tenantId, String status);

    /**
     * Find campaigns by category and tenant.
     */
    List<Campaign> findByCategory(String tenantId, String category);

    /**
     * Find active campaigns at a given datetime.
     */
    List<Campaign> findActiveCampaigns(String tenantId, LocalDateTime dateTime);

    /**
     * Find scheduled campaigns at a given datetime.
     */
    List<Campaign> findScheduledCampaigns(String tenantId, LocalDateTime dateTime);

    /**
     * Delete a campaign by ID and tenant.
     */
    void delete(String tenantId, UUID campaignId);

    /**
     * Check if a campaign exists.
     */
    boolean exists(String tenantId, UUID campaignId);

    /**
     * Check if a campaign exists by code.
     */
    boolean existsByCampaignCode(String tenantId, String campaignCode);

    /**
     * Count campaigns by tenant.
     */
    long countByTenantId(String tenantId);
}
