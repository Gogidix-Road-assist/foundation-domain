package com.gogidix.rapidassist.ai.moderation.application.service;

import com.gogidix.rapidassist.ai.moderation.application.command.ModerateContentCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.ModerationRuleCommand;
import com.gogidix.rapidassist.ai.moderation.application.command.QueueActionCommand;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationQueueDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationResultDto;
import com.gogidix.rapidassist.ai.moderation.application.dto.ModerationRuleDto;
import com.gogidix.rapidassist.ai.moderation.application.mapper.ModerationQueueMapper;
import com.gogidix.rapidassist.ai.moderation.application.mapper.ModerationResultMapper;
import com.gogidix.rapidassist.ai.moderation.application.mapper.ModerationRuleMapper;
import com.gogidix.rapidassist.ai.moderation.application.port.in.ContentModerationUseCase;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationQueueRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationResultRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.application.port.out.ModerationRuleRepositoryPort;
import com.gogidix.rapidassist.ai.moderation.domain.event.ContentFlaggedEvent;
import com.gogidix.rapidassist.ai.moderation.domain.event.ContentModeratedEvent;
import com.gogidix.rapidassist.ai.moderation.domain.event.ModerationRuleCreatedEvent;
import com.gogidix.rapidassist.ai.moderation.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for content moderation operations.
 * Implements the ContentModerationUseCase port.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContentModerationApplicationService implements ContentModerationUseCase {

    private final ModerationRuleRepositoryPort ruleRepository;
    private final ModerationResultRepositoryPort resultRepository;
    private final ModerationQueueRepositoryPort queueRepository;
    private final ModerationRuleMapper ruleMapper;
    private final ModerationResultMapper resultMapper;
    private final ModerationQueueMapper queueMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ModerationResultDto moderateContent(ModerateContentCommand command) {
        log.info("Moderating content {} for tenant {}", command.getContentId(), command.getTenantId());

        // Get active rules for tenant
        List<ModerationRule> activeRules = ruleRepository.findActiveByTenantId(command.getTenantId());

        // Analyze content against rules
        List<ModerationResult.RuleViolation> violations = new ArrayList<>();
        double totalConfidence = 0.0;
        int ruleCount = 0;

        for (ModerationRule rule : activeRules) {
            if (rule.matches(command.getContent())) {
                violations.add(ModerationResult.RuleViolation.builder()
                    .ruleId(rule.getId())
                    .ruleName(rule.getName())
                    .ruleType(rule.getRuleType())
                    .severity(rule.getSeverity())
                    .description(rule.getDescription())
                    .build());

                // Calculate confidence based on severity
                totalConfidence += getSeverityConfidence(rule.getSeverity());
                ruleCount++;
            }
        }

        // Determine final status
        ModerationResult.ModerationStatus status;
        double confidenceScore = ruleCount > 0 ? totalConfidence / ruleCount : 0.0;

        if (violations.isEmpty()) {
            status = ModerationResult.ModerationStatus.AUTO_APPROVED;
            confidenceScore = 1.0;
        } else if (confidenceScore >= 0.8) {
            status = ModerationResult.ModerationStatus.AUTO_REJECTED;
        } else {
            status = ModerationResult.ModerationStatus.FLAGGED;
        }

        // Create moderation result
        ModerationResult result = ModerationResult.builder()
            .tenantId(command.getTenantId())
            .contentId(command.getContentId())
            .contentType(command.getContentType())
            .content(command.getContent())
            .status(status)
            .confidenceScore(confidenceScore)
            .violations(violations)
            .moderatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .moderatedBy("SYSTEM")
            .build();

        result = resultRepository.save(result);

        // Publish event
        eventPublisher.publishEvent(ContentModeratedEvent.create(
            command.getTenantId(),
            command.getContentId(),
            command.getContentType(),
            status,
            confidenceScore
        ));

        // If flagged, add to queue
        if (status == ModerationResult.ModerationStatus.FLAGGED) {
            addToQueue(command, result);
            eventPublisher.publishEvent(ContentFlaggedEvent.create(
                command.getTenantId(),
                command.getContentId(),
                command.getContentType(),
                "Content flagged for manual review"
            ));
        }

        return resultMapper.toDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ModerationResultDto getModerationResult(String id) {
        return resultRepository.findById(id)
            .map(resultMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Moderation result not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModerationResultDto> getModerationResults(String tenantId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return resultRepository.findByTenantId(tenantId).stream()
            .sorted(Comparator.comparing(ModerationResult::getCreatedAt).reversed())
            .skip(page * size)
            .limit(size)
            .map(resultMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public ModerationRuleDto createRule(ModerationRuleCommand command) {
        log.info("Creating moderation rule {} for tenant {}", command.getName(), command.getTenantId());

        // Check if rule already exists
        if (ruleRepository.existsByTenantIdAndName(command.getTenantId(), command.getName())) {
            throw new IllegalArgumentException("Rule with name " + command.getName() + " already exists");
        }

        ModerationRule rule = ModerationRule.builder()
            .tenantId(command.getTenantId())
            .name(command.getName())
            .description(command.getDescription())
            .ruleType(command.getRuleType())
            .severity(command.getSeverity())
            .keywords(command.getKeywords())
            .patterns(command.getPatterns())
            .metadata(command.getMetadata())
            .active(command.isActive())
            .priority(command.getPriority() != null ? command.getPriority() : 0)
            .createdBy(command.getCreatedBy())
            .createdAt(LocalDateTime.now())
            .updatedBy(command.getCreatedBy())
            .updatedAt(LocalDateTime.now())
            .build();

        rule = ruleRepository.save(rule);

        // Publish event
        eventPublisher.publishEvent(ModerationRuleCreatedEvent.create(
            command.getTenantId(),
            rule.getId(),
            rule.getName(),
            rule.getRuleType()
        ));

        return ruleMapper.toDto(rule);
    }

    @Override
    public ModerationRuleDto updateRule(String id, ModerationRuleCommand command) {
        log.info("Updating moderation rule {}", id);

        ModerationRule rule = ruleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + id));

        rule.setName(command.getName());
        rule.setDescription(command.getDescription());
        rule.setRuleType(command.getRuleType());
        rule.setSeverity(command.getSeverity());
        rule.setKeywords(command.getKeywords());
        rule.setPatterns(command.getPatterns());
        rule.setMetadata(command.getMetadata());
        rule.setActive(command.isActive());
        rule.setPriority(command.getPriority() != null ? command.getPriority() : 0);
        rule.setUpdatedBy(command.getCreatedBy());
        rule.setUpdatedAt(LocalDateTime.now());

        rule = ruleRepository.save(rule);

        return ruleMapper.toDto(rule);
    }

    @Override
    public void deleteRule(String id) {
        log.info("Deleting moderation rule {}", id);
        ruleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModerationRuleDto> getRules(String tenantId) {
        return ruleRepository.findByTenantId(tenantId).stream()
            .map(ruleMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void approveQueueItem(String id, QueueActionCommand command) {
        log.info("Approving queue item {}", id);

        ModerationQueue queue = queueRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        queue.completeReview(command.getReviewerId(), ModerationQueue.QueueAction.APPROVE, command.getNotes());
        queueRepository.save(queue);

        // Update moderation result
        resultRepository.findByContentId(queue.getContentId()).ifPresent(result -> {
            result.setStatus(ModerationResult.ModerationStatus.APPROVED);
            result.setReviewedBy(command.getReviewerId());
            result.setReviewNotes(command.getNotes());
            resultRepository.save(result);
        });
    }

    @Override
    public void rejectQueueItem(String id, QueueActionCommand command) {
        log.info("Rejecting queue item {}", id);

        ModerationQueue queue = queueRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        queue.completeReview(command.getReviewerId(), ModerationQueue.QueueAction.REJECT, command.getNotes());
        queueRepository.save(queue);

        // Update moderation result
        resultRepository.findByContentId(queue.getContentId()).ifPresent(result -> {
            result.setStatus(ModerationResult.ModerationStatus.REJECTED);
            result.setReviewedBy(command.getReviewerId());
            result.setReviewNotes(command.getNotes());
            resultRepository.save(result);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModerationQueueDto> getQueueItems(String tenantId) {
        return queueRepository.findPendingByTenantId(tenantId).stream()
            .map(queueMapper::toDto)
            .collect(Collectors.toList());
    }

    private void addToQueue(ModerateContentCommand command, ModerationResult result) {
        ModerationQueue queue = ModerationQueue.builder()
            .tenantId(command.getTenantId())
            .contentId(command.getContentId())
            .contentType(command.getContentType())
            .content(command.getContent())
            .userId(command.getUserId())
            .userName(command.getUserName())
            .status(ModerationQueue.QueueStatus.PENDING)
            .priority(result.getHighestSeverity() != null ? getSeverityPriority(result.getHighestSeverity()) : 0)
            .submittedAt(LocalDateTime.now())
            .context(command.getContext())
            .metadata(command.getMetadata())
            .build();

        queueRepository.save(queue);
    }

    private double getSeverityConfidence(ModerationRule.RuleSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 1.0;
            case HIGH -> 0.9;
            case MEDIUM -> 0.7;
            case LOW -> 0.5;
        };
    }

    private int getSeverityPriority(ModerationRule.RuleSeverity severity) {
        return switch (severity) {
            case CRITICAL -> 100;
            case HIGH -> 75;
            case MEDIUM -> 50;
            case LOW -> 25;
        };
    }
}
