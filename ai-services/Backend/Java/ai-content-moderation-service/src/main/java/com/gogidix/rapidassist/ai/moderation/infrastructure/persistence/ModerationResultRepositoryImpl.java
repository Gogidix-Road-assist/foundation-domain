package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence;

import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationResultRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationResult;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationResultDocument;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository.SpringDataModerationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of ModerationResultRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ModerationResultRepositoryImpl implements ModerationResultRepositoryPort {

    private final SpringDataModerationResultRepository springRepository;

    @Override
    public ModerationResult save(ModerationResult result) {
        ModerationResultDocument document = toDocument(result);
        ModerationResultDocument saved = springRepository.save(document);
        return toDomain(saved);
    }

    @Override
    public Optional<ModerationResult> findById(String id) {
        return springRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Optional<ModerationResult> findByContentId(String contentId) {
        return springRepository.findByContentId(contentId)
            .map(this::toDomain);
    }

    @Override
    public List<ModerationResult> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationResult> findByTenantIdAndStatus(String tenantId, ModerationResult.ModerationStatus status) {
        return springRepository.findByTenantIdAndStatus(tenantId, status).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationResult> findRecentByTenantId(String tenantId, int limit) {
        return springRepository.findByTenantIdOrderByCreatedAtDesc(
            tenantId,
            PageRequest.of(0, limit)
        ).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String id) {
        springRepository.deleteById(id);
    }

    private ModerationResultDocument toDocument(ModerationResult domain) {
        return ModerationResultDocument.builder()
            .id(domain.getId())
            .tenantId(domain.getTenantId())
            .contentId(domain.getContentId())
            .contentType(domain.getContentType())
            .content(domain.getContent())
            .status(domain.getStatus())
            .confidenceScore(domain.getConfidenceScore())
            .violations(domain.getViolations())
            .analysisDetails(domain.getAnalysisDetails())
            .moderatedBy(domain.getModeratedBy())
            .moderatedAt(domain.getModeratedAt())
            .createdAt(domain.getCreatedAt())
            .reviewNotes(domain.getReviewNotes())
            .metadata(domain.getMetadata())
            .build();
    }

    private ModerationResult toDomain(ModerationResultDocument document) {
        return ModerationResult.builder()
            .id(document.getId())
            .tenantId(document.getTenantId())
            .contentId(document.getContentId())
            .contentType(document.getContentType())
            .content(document.getContent())
            .status(document.getStatus())
            .confidenceScore(document.getConfidenceScore())
            .violations(document.getViolations())
            .analysisDetails(document.getAnalysisDetails())
            .moderatedBy(document.getModeratedBy())
            .moderatedAt(document.getModeratedAt())
            .createdAt(document.getCreatedAt())
            .reviewNotes(document.getReviewNotes())
            .metadata(document.getMetadata())
            .build();
    }
}
