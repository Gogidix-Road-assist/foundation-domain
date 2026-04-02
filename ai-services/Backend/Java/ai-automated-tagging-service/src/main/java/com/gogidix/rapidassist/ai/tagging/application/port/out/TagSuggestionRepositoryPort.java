package com.gogidix.rapidassist.ai.tagging.application.port.out;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagSuggestionRepositoryPort {
    TagSuggestion save(TagSuggestion suggestion);
    Optional<TagSuggestion> findById(UUID id);
    Optional<TagSuggestion> findByIdAndTenantId(UUID id, String tenantId);
    List<TagSuggestion> findByTenantId(String tenantId);
    List<TagSuggestion> findByContentIdAndTenantId(String contentId, String tenantId);
    List<TagSuggestion> findPendingSuggestionsByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
