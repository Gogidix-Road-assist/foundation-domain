package com.gogidix.rapidassist.ai.tagging.application.port.out;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagDefinitionRepositoryPort {
    TagDefinition save(TagDefinition definition);
    Optional<TagDefinition> findById(UUID id);
    Optional<TagDefinition> findByIdAndTenantId(UUID id, String tenantId);
    List<TagDefinition> findByTenantId(String tenantId);
    List<TagDefinition> findByTenantIdAndCategory(String tenantId, String category);
    List<TagDefinition> findAutoApplyDefinitionsByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
