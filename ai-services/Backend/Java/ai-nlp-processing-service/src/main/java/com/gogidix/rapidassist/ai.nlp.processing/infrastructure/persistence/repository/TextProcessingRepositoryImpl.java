package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.TextProcessingRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextProcessingEntity;
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
public class TextProcessingRepositoryImpl implements TextProcessingRepositoryPort {

    private final SpringDataTextProcessingRepository springDataRepository;

    @Override
    public TextProcessing save(TextProcessing entity) {
        log.info("Saving TextProcessing: {} for tenant: {}", entity.getId(), entity.getTenantId());
        
        TextProcessingEntity entityToSave = TextProcessingEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .text(entity.getText())
                .processingType(entity.getProcessingType())
                .language(entity.getLanguage())
                .status(entity.getStatus() != null ? entity.getStatus() : TextProcessing.ProcessingStatus.PENDING)
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();

        TextProcessingEntity savedEntity = springDataRepository.save(entityToSave);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<TextProcessing> findById(UUID id) {
        return springDataRepository.findByUuid(id)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<TextProcessing> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding TextProcessing by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::mapToDomain);
    }

    @Override
    public List<TextProcessing> findByTenantId(String tenantId) {
        log.info("Finding all TextProcessing for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TextProcessing> findByStatus(String tenantId, TextProcessing.ProcessingStatus status) {
        log.info("Finding TextProcessing by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting TextProcessing: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    private TextProcessing mapToDomain(TextProcessingEntity entity) {
        return TextProcessing.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .text(entity.getText())
                .processingType(entity.getProcessingType())
                .language(entity.getLanguage())
                .status(entity.getStatus())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
