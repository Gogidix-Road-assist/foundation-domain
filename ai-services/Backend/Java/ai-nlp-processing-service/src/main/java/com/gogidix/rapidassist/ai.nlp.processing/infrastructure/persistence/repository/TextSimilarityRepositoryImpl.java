package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextSimilarity;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.TextSimilarityRepositoryPort;
import com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity.TextSimilarityEntity;
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
public class TextSimilarityRepositoryImpl implements TextSimilarityRepositoryPort {
    private final SpringDataTextSimilarityRepository springDataRepository;

    @Override
    public TextSimilarity save(TextSimilarity entity) {
        TextSimilarityEntity entityToSave = TextSimilarityEntity.builder()
                .uuid(entity.getId() != null ? entity.getId() : UUID.randomUUID())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text1(entity.getText1())
                .text2(entity.getText2())
                .similarityScore(entity.getSimilarityScore())
                .similarityMethod(entity.getMethod() != null ? entity.getMethod().name() : null)
                .detailedScores(entity.getDetailedScores())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        TextSimilarityEntity saved = springDataRepository.save(entityToSave);
        return mapToDomain(saved);
    }

    @Override
    public Optional<TextSimilarity> findById(UUID id) {
        return springDataRepository.findById(id.toString()).map(this::mapToDomain);
    }

    @Override
    public Optional<TextSimilarity> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId).map(this::mapToDomain);
    }

    @Override
    public List<TextSimilarity> findByTextProcessingId(UUID textProcessingId) {
        return springDataRepository.findByTextProcessingId(textProcessingId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<TextSimilarity> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    private TextSimilarity mapToDomain(TextSimilarityEntity entity) {
        return TextSimilarity.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .textProcessingId(entity.getTextProcessingId())
                .text1(entity.getText1())
                .text2(entity.getText2())
                .similarityScore(entity.getSimilarityScore())
                .method(entity.getSimilarityMethod() != null ? 
                    TextSimilarity.SimilarityMethod.valueOf(entity.getSimilarityMethod()) : null)
                .detailedScores(entity.getDetailedScores())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
