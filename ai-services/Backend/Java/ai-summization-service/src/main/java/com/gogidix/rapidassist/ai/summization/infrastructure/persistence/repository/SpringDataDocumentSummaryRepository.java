package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.SummaryStyle;
import com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity.DocumentSummaryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for DocumentSummaryEntity.
 */
@Repository
public interface SpringDataDocumentSummaryRepository extends MongoRepository<DocumentSummaryEntity, String> {

    Optional<DocumentSummaryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<DocumentSummaryEntity> findByTenantId(String tenantId);

    List<DocumentSummaryEntity> findBySummarizationTaskIdAndTenantId(UUID taskId, String tenantId);

    List<DocumentSummaryEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<DocumentSummaryEntity> findBySummaryStyleAndTenantId(SummaryStyle style, String tenantId);

    List<DocumentSummaryEntity> findByLanguageAndTenantId(String language, String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    @Query("{ 'tenantId': ?0, 'summaryText': { $regex: ?1, $options: 'i' } }")
    List<DocumentSummaryEntity> searchBySummaryText(String tenantId, String searchTerm);

    @Query("{ 'tenantId': ?0, 'title': { $regex: ?1, $options: 'i' } }")
    List<DocumentSummaryEntity> searchByTitle(String tenantId, String searchTerm);
}
