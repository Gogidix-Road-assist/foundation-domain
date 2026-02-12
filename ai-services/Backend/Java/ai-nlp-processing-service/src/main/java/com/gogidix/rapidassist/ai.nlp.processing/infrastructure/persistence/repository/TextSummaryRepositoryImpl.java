package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSummary;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.TextSummaryRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextSummaryEntity;
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
public class TextSummaryRepositoryImpl implements TextSummaryRepositoryPort {
    private final SpringDataTextSummaryRepository springDataRepository;

    @Override
    public TextSummary save(TextSummary entity) {
        TextSummaryEntity entityToSave = TextSummaryEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .originalText(entity.getOriginalText())
                .summary(entity.getSummary())
                .compressionRatio(entity.getCompressionRatio())
                .originalLength(entity.getOriginalLength())
                .summaryLength(entity.getSummaryLength())
                .summarizationMethod(entity.getSummarizationMethod())
                .relevanceScore(entity.getRelevanceScore())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        TextSummaryEntity saved = springDataRepository.save(entityToSave);
        return mapToDomain(saved);
    }

    @Override
    public Optional<TextSummary> findById(UUID id) {
        return springDataRepository.findById(id.toString()).map(this::mapToDomain);
    }

    @Override
    public Optional<TextSummary> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId).map(this::mapToDomain);
    }

    @Override
    public List<TextSummary> findByTextProcessingId(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingId(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<TextSummary> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    private TextSummary mapToDomain(TextSummaryEntity entity) {
        return TextSummary.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .originalText(entity.getOriginalText())
                .summary(entity.getSummary())
                .compressionRatio(entity.getCompressionRatio())
                .originalLength(entity.getOriginalLength())
                .summaryLength(entity.getSummaryLength())
                .summarizationMethod(entity.getSummarizationMethod())
                .relevanceScore(entity.getRelevanceScore())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
