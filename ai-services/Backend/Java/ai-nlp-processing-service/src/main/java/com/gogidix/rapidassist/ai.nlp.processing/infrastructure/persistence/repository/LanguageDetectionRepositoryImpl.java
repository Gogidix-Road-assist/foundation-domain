package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.LanguageDetection;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.LanguageDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.LanguageDetectionEntity;
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
public class LanguageDetectionRepositoryImpl implements LanguageDetectionRepositoryPort {
    private final SpringDataLanguageDetectionRepository springDataRepository;

    @Override
    public LanguageDetection save(LanguageDetection entity) {
        LanguageDetectionEntity entityToSave = LanguageDetectionEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text(entity.getText())
                .detectedLanguage(entity.getDetectedLanguage())
                .languageCode(entity.getLanguageCode())
                .confidence(entity.getConfidence())
                .alternativeLanguages(entity.getAlternativeLanguages())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        LanguageDetectionEntity saved = springDataRepository.save(entityToSave);
        return mapToDomain(saved);
    }

    @Override
    public Optional<LanguageDetection> findById(UUID id) {
        return springDataRepository.findById(id.toString()).map(this::mapToDomain);
    }

    @Override
    public Optional<LanguageDetection> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId).map(this::mapToDomain);
    }

    @Override
    public List<LanguageDetection> findByTextProcessingId(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingId(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<LanguageDetection> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    private LanguageDetection mapToDomain(LanguageDetectionEntity entity) {
        return LanguageDetection.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text(entity.getText())
                .detectedLanguage(entity.getDetectedLanguage())
                .languageCode(entity.getLanguageCode())
                .confidence(entity.getConfidence())
                .alternativeLanguages(entity.getAlternativeLanguages())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
