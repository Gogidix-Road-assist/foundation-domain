package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationResultDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for ModerationResultDocument
 */
@Repository
public interface SpringDataModerationResultRepository extends MongoRepository<ModerationResultDocument, String> {

    List<ModerationResultDocument> findByTenantId(String tenantId);

    List<ModerationResultDocument> findByTenantIdAndStatus(String tenantId, ModerationResult.ModerationStatus status);

    Optional<ModerationResultDocument> findByContentId(String contentId);

    List<ModerationResultDocument> findByTenantIdOrderByCreatedAtDesc(String tenantId, Pageable pageable);

    Page<ModerationResultDocument> findByTenantId(String tenantId, Pageable pageable);
}
