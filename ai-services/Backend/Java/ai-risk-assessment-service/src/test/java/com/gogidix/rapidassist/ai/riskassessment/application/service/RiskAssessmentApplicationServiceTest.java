package com.gogidix.rapidassist.ai.riskassessment.application.service;

import com.gogidix.rapidassist.ai.riskassessment.application.command.AddRiskFactorCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.CreateRiskAssessmentCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.command.UpdateAssessmentStatusCommand;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAssessmentDto;
import com.gogidix.rapidassist.ai.riskassessment.application.mapper.RiskAssessmentMapper;
import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.domain.exception.RiskAssessmentNotFoundException;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskFactor;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskAssessmentRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskThresholdRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RiskAssessmentApplicationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Risk Assessment Application Service Tests")
class RiskAssessmentApplicationServiceTest {

    @Mock
    private RiskAssessmentRepositoryPort assessmentRepository;

    @Mock
    private RiskThresholdRepositoryPort thresholdRepository;

    @Mock
    private RiskAssessmentMapper mapper;

    @InjectMocks
    private RiskAssessmentApplicationService service;

    private final String tenantId = "tenant-123";
    private final UUID assessmentId = UUID.randomUUID();
    private final String subjectId = "customer-123";
    private final String createdBy = "user-123";

    @Test
    @DisplayName("Should create risk assessment successfully")
    void shouldCreateRiskAssessmentSuccessfully() {
        // Given
        CreateRiskAssessmentCommand command = CreateRiskAssessmentCommand.builder()
                .tenantId(tenantId)
                .subjectId(subjectId)
                .subjectType("CUSTOMER")
                .title("Credit Risk Assessment")
                .description("Assessment for customer credit risk")
                .category(RiskCategory.FINANCIAL)
                .createdBy(createdBy)
                .metadata(new HashMap<>())
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .subjectId(subjectId)
                .title("Credit Risk Assessment")
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .subjectId(subjectId)
                .build();

        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.createAssessment(command);

        // Then
        assertNotNull(response);
        assertEquals(assessmentId, response.getId());
        assertEquals(subjectId, response.getSubjectId());
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
        verify(mapper, times(1)).toDto(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should add risk factor successfully")
    void shouldAddRiskFactorSuccessfully() {
        // Given
        AddRiskFactorCommand command = AddRiskFactorCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .name("Credit Score")
                .description("Customer credit history")
                .category(RiskCategory.FINANCIAL)
                .weight(0.3)
                .score(75.0)
                .impact(0.8)
                .likelihood(0.7)
                .createdBy(createdBy)
                .metadata(new HashMap<>())
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.addRiskFactor(command);

        // Then
        assertNotNull(response);
        verify(assessmentRepository, times(1)).findByIdAndTenantId(assessmentId, tenantId);
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
        verify(mapper, times(1)).toDto(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should throw exception when assessment not found")
    void shouldThrowExceptionWhenAssessmentNotFound() {
        // Given
        AddRiskFactorCommand command = AddRiskFactorCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .name("Test Factor")
                .category(RiskCategory.FINANCIAL)
                .weight(0.3)
                .score(75.0)
                .createdBy(createdBy)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RiskAssessmentNotFoundException.class, () -> service.addRiskFactor(command));
        verify(assessmentRepository, times(1)).findByIdAndTenantId(assessmentId, tenantId);
        verify(assessmentRepository, never()).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should update assessment status to in_progress")
    void shouldUpdateAssessmentStatusToInProgress() {
        // Given
        UpdateAssessmentStatusCommand command = UpdateAssessmentStatusCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .status(AssessmentStatus.IN_PROGRESS)
                .updatedBy(createdBy)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .status(AssessmentStatus.PENDING)
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .status(AssessmentStatus.IN_PROGRESS)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.updateStatus(command);

        // Then
        assertNotNull(response);
        assertEquals(AssessmentStatus.IN_PROGRESS, response.getStatus());
        verify(assessmentRepository, times(1)).findByIdAndTenantId(assessmentId, tenantId);
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should update assessment status to completed")
    void shouldUpdateAssessmentStatusToCompleted() {
        // Given
        UpdateAssessmentStatusCommand command = UpdateAssessmentStatusCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .status(AssessmentStatus.COMPLETED)
                .updatedBy(createdBy)
                .build();

        RiskFactor factor = RiskFactor.builder()
                .id(UUID.randomUUID())
                .name("Test Factor")
                .category(RiskCategory.FINANCIAL)
                .weight(0.3)
                .score(75.0)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .status(AssessmentStatus.IN_PROGRESS)
                .build();
        assessment.addRiskFactor(factor);

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .status(AssessmentStatus.COMPLETED)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.updateStatus(command);

        // Then
        assertNotNull(response);
        assertEquals(AssessmentStatus.COMPLETED, response.getStatus());
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should throw exception when rejection reason is missing")
    void shouldThrowExceptionWhenRejectionReasonIsMissing() {
        // Given
        UpdateAssessmentStatusCommand command = UpdateAssessmentStatusCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .status(AssessmentStatus.REJECTED)
                .updatedBy(createdBy)
                .reason(null)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(command));
        verify(assessmentRepository, never()).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should handle null impact in add risk factor")
    void shouldHandleNullImpactInAddRiskFactor() {
        // Given
        AddRiskFactorCommand command = AddRiskFactorCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .name("Test Factor")
                .category(RiskCategory.FINANCIAL)
                .weight(0.3)
                .score(75.0)
                .impact(null)
                .likelihood(0.7)
                .createdBy(createdBy)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.addRiskFactor(command);

        // Then
        assertNotNull(response);
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should handle null likelihood in add risk factor")
    void shouldHandleNullLikelihoodInAddRiskFactor() {
        // Given
        AddRiskFactorCommand command = AddRiskFactorCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .name("Test Factor")
                .category(RiskCategory.FINANCIAL)
                .weight(0.3)
                .score(75.0)
                .impact(0.8)
                .likelihood(null)
                .createdBy(createdBy)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.addRiskFactor(command);

        // Then
        assertNotNull(response);
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
    }

    @Test
    @DisplayName("Should handle null updated by in status update")
    void shouldHandleNullUpdatedByInStatusUpdate() {
        // Given
        UpdateAssessmentStatusCommand command = UpdateAssessmentStatusCommand.builder()
                .tenantId(tenantId)
                .riskAssessmentId(assessmentId)
                .status(AssessmentStatus.IN_PROGRESS)
                .updatedBy(null)
                .build();

        RiskAssessment assessment = RiskAssessment.builder()
                .id(assessmentId)
                .tenantId(tenantId)
                .status(AssessmentStatus.PENDING)
                .build();

        RiskAssessmentDto dto = RiskAssessmentDto.builder()
                .id(assessmentId)
                .status(AssessmentStatus.IN_PROGRESS)
                .build();

        when(assessmentRepository.findByIdAndTenantId(assessmentId, tenantId))
                .thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(RiskAssessment.class)))
                .thenReturn(assessment);
        when(mapper.toDto(any(RiskAssessment.class)))
                .thenReturn(dto);

        // When
        RiskAssessmentDto response = service.updateStatus(command);

        // Then
        assertNotNull(response);
        verify(assessmentRepository, times(1)).save(any(RiskAssessment.class));
    }
}
