package com.gogidix.rapidassist.ai.riskassessment.application.service;

import com.gogidix.rapidassist.ai.riskassessment.application.command.AddRiskFactorCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.CreateRiskAssessmentCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.UpdateAssessmentStatusCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAssessmentDto;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskFactorDto;
import com.gogidix.rapidassist.ai.riskassessment.application.mapper.RiskAssessmentMapper;
import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.domain.exception.RiskAssessmentNotFoundException;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskFactor;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskThreshold;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskAssessmentRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskThresholdRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.domain.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Risk Assessment operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentApplicationService {

    private final RiskAssessmentRepositoryPort assessmentRepository;
    private final RiskThresholdRepositoryPort thresholdRepository;
    private final RiskAssessmentMapper mapper;

    @Transactional
    public RiskAssessmentDto createAssessment(CreateRiskAssessmentCommand command) {
        log.info("Creating risk assessment for tenant: {}, subject: {}",
                command.getTenantId(), command.getSubjectId());

        TenantContext.setTenantId(command.getTenantId());

        RiskAssessment assessment = RiskAssessment.initialize(
                command.getTenantId(),
                command.getSubjectId(),
                command.getSubjectType(),
                command.getTitle(),
                command.getDescription(),
                command.getCategory(),
                command.getCreatedBy()
        );

        if (command.getMetadata() != null) {
            command.getMetadata().forEach(assessment::updateMetadata);
        }

        RiskAssessment saved = assessmentRepository.save(assessment);
        log.info("Risk assessment created with ID: {}", saved.getId());

        return mapper.toDto(saved);
    }

    @Transactional
    public RiskAssessmentDto addRiskFactor(AddRiskFactorCommand command) {
        log.info("Adding risk factor to assessment: {} for tenant: {}",
                command.getRiskAssessmentId(), command.getTenantId());

        TenantContext.setTenantId(command.getTenantId());

        RiskAssessment assessment = assessmentRepository.findByIdAndTenantId(
                command.getRiskAssessmentId(),
                command.getTenantId()
        ).orElseThrow(() -> new RiskAssessmentNotFoundException(
                command.getRiskAssessmentId(),
                command.getTenantId()
        ));

        RiskFactor factor = RiskFactor.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .category(command.getCategory())
                .weight(command.getWeight())
                .score(command.getScore())
                .impact(command.getImpact() != null ? command.getImpact() : 0.0)
                .likelihood(command.getLikelihood() != null ? command.getLikelihood() : 0.0)
                .metadata(command.getMetadata())
                .createdBy(command.getCreatedBy())
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        assessment.addRiskFactor(factor);
        assessment.calculateOverallRiskScore();

        RiskAssessment saved = assessmentRepository.save(assessment);
        log.info("Risk factor added: {} to assessment: {}", factor.getId(), saved.getId());

        return mapper.toDto(saved);
    }

    @Transactional
    public RiskAssessmentDto updateStatus(UpdateAssessmentStatusCommand command) {
        log.info("Updating assessment status: {} to {} for tenant: {}",
                command.getRiskAssessmentId(), command.getStatus(), command.getTenantId());

        TenantContext.setTenantId(command.getTenantId());

        RiskAssessment assessment = assessmentRepository.findByIdAndTenantId(
                command.getRiskAssessmentId(),
                command.getTenantId()
        ).orElseThrow(() -> new RiskAssessmentNotFoundException(
                command.getRiskAssessmentId(),
                command.getTenantId()
        ));

        String updatedBy = command.getUpdatedBy() != null ? command.getUpdatedBy() : "system";

        switch (command.getStatus()) {
            case IN_PROGRESS -> assessment.startAssessment(updatedBy);
            case COMPLETED -> assessment.completeAssessment(updatedBy);
            case REVIEWED -> assessment.reviewAssessment(updatedBy);
            case APPROVED -> assessment.approveAssessment(updatedBy);
            case REJECTED -> {
                if (command.getReason() == null || command.getReason().isBlank()) {
                    throw new IllegalArgumentException("Rejection reason is required");
                }
                assessment.rejectAssessment(command.getReason(), updatedBy);
            }
            case CANCELLED -> assessment.cancelAssessment(updatedBy);
            default -> throw new IllegalArgumentException("Invalid status transition: " + command.getStatus());
        }

        RiskAssessment saved = assessmentRepository.save(assessment);
        log.info("Assessment status updated: {} -> {}", saved.getId(), saved.getStatus());

        return mapper.toDto(saved);
    }

    @Transactional
    public RiskAssessmentDto completeAssessmentAndGenerateAlerts(UUID assessmentId, String tenantId) {
        log.info("Completing assessment and generating alerts: {} for tenant: {}",
                assessmentId, tenantId);

        TenantContext.setTenantId(tenantId);

        RiskAssessment assessment = assessmentRepository.findByIdAndTenantId(
                assessmentId,
                tenantId
        ).orElseThrow(() -> new RiskAssessmentNotFoundException(assessmentId, tenantId));

        assessment.completeAssessment(assessment.getAssessedBy());

        // Generate alerts based on threshold
        RiskThreshold threshold = thresholdRepository.findDefaultByTenantId(tenantId)
                .orElseThrow(() -> new IllegalStateException(
                        "No risk threshold configured for tenant: " + tenantId
                ));

        List<com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert> alerts =
                assessment.generateAlerts(threshold);

        RiskAssessment saved = assessmentRepository.save(assessment);
        log.info("Assessment completed with {} alerts generated", alerts.size());

        return mapper.toDto(saved);
    }

    public RiskAssessmentDto getAssessment(UUID assessmentId, String tenantId) {
        log.debug("Getting assessment: {} for tenant: {}", assessmentId, tenantId);

        TenantContext.setTenantId(tenantId);

        RiskAssessment assessment = assessmentRepository.findByIdAndTenantId(
                assessmentId,
                tenantId
        ).orElseThrow(() -> new RiskAssessmentNotFoundException(assessmentId, tenantId));

        return mapper.toDto(assessment);
    }

    public List<RiskAssessmentDto> getAssessmentsBySubject(String subjectId, String tenantId) {
        log.debug("Getting assessments for subject: {} tenant: {}", subjectId, tenantId);

        TenantContext.setTenantId(tenantId);

        return assessmentRepository.findBySubjectIdAndTenantId(subjectId, tenantId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<RiskAssessmentDto> getActiveAssessments(String tenantId) {
        log.debug("Getting active assessments for tenant: {}", tenantId);

        TenantContext.setTenantId(tenantId);

        return assessmentRepository.findActiveAssessments(tenantId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<RiskAssessmentDto> getCriticalRiskAssessments(String tenantId) {
        log.debug("Getting critical risk assessments for tenant: {}", tenantId);

        TenantContext.setTenantId(tenantId);

        return assessmentRepository.findCriticalRiskAssessments(tenantId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteAssessment(UUID assessmentId, String tenantId) {
        log.info("Deleting assessment: {} for tenant: {}", assessmentId, tenantId);

        TenantContext.setTenantId(tenantId);

        if (!assessmentRepository.existsByIdAndTenantId(assessmentId, tenantId)) {
            throw new RiskAssessmentNotFoundException(assessmentId, tenantId);
        }

        assessmentRepository.deleteByIdAndTenantId(assessmentId, tenantId);
        log.info("Assessment deleted: {}", assessmentId);
    }
}
