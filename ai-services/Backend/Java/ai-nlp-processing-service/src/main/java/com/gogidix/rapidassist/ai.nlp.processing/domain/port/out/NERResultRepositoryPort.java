package com.gogidix.rapidassist.ai.nlp.processing.domain.port.out;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.NERResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NERResultRepositoryPort {
    NERResult save(NERResult entity);
    Optional<NERResult> findById(UUID id);
    Optional<NERResult> findByIdAndTenantId(UUID id, String tenantId);
    List<NERResult> findByTextProcessingId(UUID textProcessingId);
    List<NERResult> findByTenantId(String tenantId);
    void deleteById(UUID id);
}
