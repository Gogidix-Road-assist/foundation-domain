package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence;

import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationQueueRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationQueue;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationQueueDocument;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository.SpringDataModerationQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB implementation of ModerationQueueRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ModerationQueueRepositoryImpl implements ModerationQueueRepositoryPort {

    private final SpringDataModerationQueueRepository springRepository;

    @Override
    public ModerationQueue save(ModerationQueue queue) {
        ModerationQueueDocument document = toDocument(queue);
        ModerationQueueDocument saved = springRepository.save(document);
        return toDomain(saved);
    }

    @Override
    public Optional<ModerationQueue> findById(String id) {
        return springRepository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public Optional<ModerationQueue> findByContentId(String contentId) {
        return springRepository.findByContentId(contentId)
            .map(this::toDomain);
    }

    @Override
    public List<ModerationQueue> findPendingByTenantId(String tenantId) {
        return springRepository.findByTenantIdAndStatusOrderByPriorityDescSubmittedAtAsc(
            tenantId,
            ModerationQueue.QueueStatus.PENDING
        ).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationQueue> findByTenantIdAndStatus(String tenantId, ModerationQueue.QueueStatus status) {
        return springRepository.findByTenantIdAndStatus(tenantId, status).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public List<ModerationQueue> findByAssignedTo(String reviewerId) {
        return springRepository.findByAssignedTo(reviewerId).stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void deleteById(String id) {
        springRepository.deleteById(id);
    }

    private ModerationQueueDocument toDocument(ModerationQueue domain) {
        return ModerationQueueDocument.builder()
            .id(domain.getId())
            .tenantId(domain.getTenantId())
            .contentId(domain.getContentId())
            .contentType(domain.getContentType())
            .content(domain.getContent())
            .userId(domain.getUserId())
            .userName(domain.getUserName())
            .status(domain.getStatus())
            .priority(domain.getPriority())
            .submittedAt(domain.getSubmittedAt())
            .assignedAt(domain.getAssignedAt())
            .assignedTo(domain.getAssignedTo())
            .reviewedAt(domain.getReviewedAt())
            .reviewedBy(domain.getReviewedBy())
            .action(domain.getAction())
            .reviewNotes(domain.getReviewNotes())
            .context(domain.getContext())
            .metadata(domain.getMetadata())
            .build();
    }

    private ModerationQueue toDomain(ModerationQueueDocument document) {
        return ModerationQueue.builder()
            .id(document.getId())
            .tenantId(document.getTenantId())
            .contentId(document.getContentId())
            .contentType(document.getContentType())
            .content(document.getContent())
            .userId(document.getUserId())
            .userName(document.getUserName())
            .status(document.getStatus())
            .priority(document.getPriority())
            .submittedAt(document.getSubmittedAt())
            .assignedAt(document.getAssignedAt())
            .assignedTo(document.getAssignedTo())
            .reviewedAt(document.getReviewedAt())
            .reviewedBy(document.getReviewedBy())
            .action(document.getAction())
            .reviewNotes(document.getReviewNotes())
            .context(document.getContext())
            .metadata(document.getMetadata())
            .build();
    }
}
