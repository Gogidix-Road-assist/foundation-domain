package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.KeySentenceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataKeySentenceRepository extends MongoRepository<KeySentenceEntity, String> {

    List<KeySentenceEntity> findBySummaryIdAndTenantId(UUID summaryId, String tenantId);

    void deleteBySummaryIdAndTenantId(UUID summaryId, String tenantId);
}
