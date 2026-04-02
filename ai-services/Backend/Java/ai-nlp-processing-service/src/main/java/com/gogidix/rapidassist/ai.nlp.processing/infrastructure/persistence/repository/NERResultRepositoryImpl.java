package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.NERResult;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.NERResultRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.NERResultEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NERResultRepositoryImpl implements NERResultRepositoryPort {
    private final SpringDataNERResultRepository springDataRepository;

    @Override
    public NERResult save(NERResult entity) {
        NERResultEntity entityToSave = NERResultEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .entities(entity.getEntities())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        NERResultEntity saved = springDataRepository.save(entityToSave);
        return mapToDomain(saved);
    }

    @Override
    public Optional<NERResult> findById(UUID id) {
        return springDataRepository.findById(id.toString()).map(this::mapToDomain);
    }

    @Override
    public Optional<NERResult> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId).map(this::mapToDomain);
    }

    @Override
    public List<NERResult> findByTextProcessingId(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingId(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<NERResult> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    private NERResult mapToDomain(NERResultEntity entity) {
        return NERResult.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .entities(entity.getEntities())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
