package com.gogidix.rapidassist.ai.tagging.application.port.out;

import com.gogidix.rapidassist.ai.tagging.domain.model.TaggingResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaggingResultRepositoryPort {
    TaggingResult save(TaggingResult result);
    Optional<TaggingResult> findById(UUID id);
    Optional<TaggingResult> findByIdAndTenantId(UUID id, String tenantId);
    List<TaggingResult> findByTenantId(String tenantId);
    List<TaggingResult> findByContentIdAndTenantId(String contentId, String tenantId);
    List<TaggingResult> findByTagIdAndTenantId(UUID tagId, String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    long countByTenantId(String tenantId);
}
