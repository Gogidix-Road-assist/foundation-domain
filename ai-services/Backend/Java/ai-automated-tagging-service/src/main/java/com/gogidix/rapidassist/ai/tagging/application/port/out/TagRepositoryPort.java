package com.gogidix.rapidassist.ai.tagging.application.port.out;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepositoryPort {
    Tag save(Tag tag);
    Optional<Tag> findById(UUID id);
    Optional<Tag> findByIdAndTenantId(UUID id, String tenantId);
    List<Tag> findByTenantId(String tenantId);
    List<Tag> findByTenantIdAndCategory(String tenantId, String category);
    List<Tag> findActiveTagsByTenantId(String tenantId);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
    boolean existsByNameAndTenantId(String name, String tenantId);
}
