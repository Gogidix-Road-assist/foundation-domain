package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagSuggestionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTagSuggestionRepository extends MongoRepository<TagSuggestionEntity, String> {

    Optional<TagSuggestionEntity> findByUuid(UUID uuid);
    TagSuggestionEntity findByTenantIdAndUuid(String tenantId, UUID uuid);
    List<TagSuggestionEntity> findByTenantIdAndContentId(String tenantId, String contentId);
    List<TagSuggestionEntity> findByTenantIdAndContentIdAndStatus(String tenantId, String contentId, TagSuggestion.SuggestionStatus status);
    List<TagSuggestionEntity> findByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status);
    List<TagSuggestionEntity> findHighConfidenceSuggestions(String tenantId, double minConfidence);
    List<TagSuggestionEntity> findExpiredSuggestions(String tenantId, LocalDateTime now);
    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);
    void deleteByTenantIdAndContentId(String tenantId, String contentId);
    void deleteExpiredSuggestions(String tenantId, LocalDateTime now);
    long countByTenantId(String tenantId);
    long countByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status);
}
