package com.gogidix.rapidassist.ai.fraud;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.application.mapper.FraudDetectionMapper;
import com.gogidix.rapidassist.ai.fraud.application.service.FraudDetectionService;
import com.gogidix.rapidassist.ai.fraud.domain.model.*;
import com.gogidix.rapidassist.ai.fraud.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Edge case tests for FraudDetectionService
 * Tests null handling, boundary values, empty strings, and exception paths
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionService Edge Cases Tests")
class FraudDetectionServiceEdgeCasesTest {

    @Mock
    private FraudDetectionRepositoryPort detectionRepository;
    @Mock
    private FraudAlertRepositoryPort alertRepository;
    @Mock
    private FraudCaseRepositoryPort caseRepository;
    @Mock
    private FraudPatternRepositoryPort patternRepository;
    @Mock
    private FraudRuleRepositoryPort ruleRepository;
    @Mock
    private FraudDetectionMapper mapper;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    private static final String TENANT_ID = "tenant-123";
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
    }

    // ==================== detectFraud() Edge Cases ====================

    @Test
    @DisplayName("detectFraud - With null details map")
    void testDetectFraud_NullDetails() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .riskScore(0.5)
                .detectionDetails(null)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .riskScore(0.5)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                TENANT_ID, "CLAIM", "claim-123",
                FraudRiskLevel.MEDIUM, 0.5, "RULE_ENGINE", null
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("detectFraud - With empty details map")
    void testDetectFraud_EmptyDetails() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.2)
                .detectionDetails(Collections.emptyMap())
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.2)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                TENANT_ID, "CLAIM", "claim-123",
                FraudRiskLevel.LOW, 0.2, "RULE_ENGINE", Collections.emptyMap()
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("detectFraud - Risk score at boundary 0.0")
    void testDetectFraud_RiskScoreZero() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .riskScore(0.0)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .riskScore(0.0)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                TENANT_ID, "CLAIM", "claim-123",
                FraudRiskLevel.LOW, 0.0, "RULE", null
        );

        assertNotNull(result);
        assertEquals(0.0, result.getRiskScore());
    }

    @Test
    @DisplayName("detectFraud - Risk score at boundary 1.0")
    void testDetectFraud_RiskScoreOne() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .riskScore(1.0)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .riskScore(1.0)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                TENANT_ID, "CLAIM", "claim-123",
                FraudRiskLevel.CRITICAL, 1.0, "ML", null
        );

        assertNotNull(result);
        assertEquals(1.0, result.getRiskScore());
    }

    @Test
    @DisplayName("detectFraud - With MEDIUM risk level")
    void testDetectFraud_MediumRiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .riskLevel(FraudRiskLevel.MEDIUM)
                .riskScore(0.5)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .riskLevel(FraudRiskLevel.MEDIUM)
                .riskScore(0.5)
                .requiresReview(false)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                TENANT_ID, "TRANSACTION", "txn-123",
                FraudRiskLevel.MEDIUM, 0.5, "RULE", null
        );

        assertNotNull(result);
        assertEquals(FraudRiskLevel.MEDIUM, result.getRiskLevel());
        assertFalse(result.getRequiresReview());
    }

    @Test
    @DisplayName("detectFraud - All risk level enum values")
    void testDetectFraud_AllRiskLevelEnums() {
        FraudRiskLevel[] riskLevels = {
            FraudRiskLevel.LOW,
            FraudRiskLevel.MEDIUM,
            FraudRiskLevel.HIGH,
            FraudRiskLevel.CRITICAL
        };

        for (FraudRiskLevel riskLevel : riskLevels) {
            FraudDetection detection = FraudDetection.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .riskLevel(riskLevel)
                    .build();

            FraudDetectionDto dto = FraudDetectionDto.builder()
                    .id(UUID.randomUUID())
                    .riskLevel(riskLevel)
                    .build();

            when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                    .thenReturn(detection);
            when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

            var result = fraudDetectionService.detectFraud(
                    TENANT_ID, "CLAIM", "claim-123",
                    riskLevel, 0.5, "RULE", null
            );

            assertNotNull(result);
            assertEquals(riskLevel, result.getRiskLevel());
        }
    }

    // ==================== getDetectionById() Edge Cases ====================

    @Test
    @DisplayName("getDetectionById - Throws exception when not found")
    void testGetDetectionById_NotFoundThrowsException() {
        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.getDetectionById(TENANT_ID, testId);
        });

        assertTrue(exception.getMessage().contains("FraudDetection not found"));
        assertTrue(exception.getMessage().contains(testId.toString()));
    }

    // ==================== getDetectionsByStatus() Edge Cases ====================

    @Test
    @DisplayName("getDetectionsByStatus - Empty list when no matches")
    void testGetDetectionsByStatus_EmptyList() {
        when(detectionRepository.findByStatus(TENANT_ID, "CLOSED"))
                .thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getDetectionsByStatus(TENANT_ID, "CLOSED");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDetectionsByStatus - Various status values")
    void testGetDetectionsByStatus_VariousStatusValues() {
        String[] statuses = {"PENDING", "REVIEWED", "CONFIRMED_FRAUD", "FALSE_POSITIVE", "CLOSED"};

        for (String status : statuses) {
            FraudDetection detection = FraudDetection.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .status(status)
                    .build();

            FraudDetectionDto dto = FraudDetectionDto.builder()
                    .id(UUID.randomUUID())
                    .status(status)
                    .build();

            when(detectionRepository.findByStatus(TENANT_ID, status))
                    .thenReturn(List.of(detection));
            when(mapper.toDtoList(List.of(detection)))
                    .thenReturn(List.of(dto));

            var result = fraudDetectionService.getDetectionsByStatus(TENANT_ID, status);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(status, result.get(0).getStatus());
        }
    }

    // ==================== updateDetectionReview() Edge Cases ====================

    @Test
    @DisplayName("updateDetectionReview - Null reviewer")
    void testUpdateDetectionReview_NullReviewer() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.updateDetectionReview(
                TENANT_ID, testId, null, null
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("updateDetectionReview - Empty reviewer string")
    void testUpdateDetectionReview_EmptyReviewer() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.updateDetectionReview(
                TENANT_ID, testId, "", "notes"
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("updateDetectionReview - Null notes")
    void testUpdateDetectionReview_NullNotes() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.updateDetectionReview(
                TENANT_ID, testId, "reviewer", null
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("updateDetectionReview - Empty notes string")
    void testUpdateDetectionReview_EmptyNotes() {
        FraudDetection detection = FraudDetection.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(testId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.updateDetectionReview(
                TENANT_ID, testId, "reviewer", ""
        );

        assertNotNull(result);
        verify(detectionRepository).save(eq(TENANT_ID), any(FraudDetection.class));
    }

    @Test
    @DisplayName("updateDetectionReview - Not found throws exception")
    void testUpdateDetectionReview_NotFoundThrowsException() {
        when(detectionRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.updateDetectionReview(TENANT_ID, testId, "reviewer", "notes");
        });

        assertTrue(exception.getMessage().contains("FraudDetection not found"));
        verify(detectionRepository, never()).save(any(), any());
    }

    // ==================== createAlert() Edge Cases ====================

    @Test
    @DisplayName("createAlert - All severity levels")
    void testCreateAlert_AllSeverityLevels() {
        String[] severities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};

        for (String severity : severities) {
            UUID alertId = UUID.randomUUID();
            FraudAlert alert = FraudAlert.builder()
                    .id(alertId)
                    .tenantId(TENANT_ID)
                    .severity(severity)
                    .build();

            FraudAlertDto dto = FraudAlertDto.builder()
                    .id(alertId)
                    .severity(severity)
                    .build();

            when(alertRepository.save(any(String.class), any(FraudAlert.class)))
                    .thenReturn(alert);
            when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

            var result = fraudDetectionService.createAlert(
                    TENANT_ID, testId, "FRAUD_DETECTED", severity,
                    "Alert Title", "Alert Description"
            );

            assertNotNull(result);
            assertEquals(severity, result.getSeverity());
        }
    }

    @Test
    @DisplayName("createAlert - Null description")
    void testCreateAlert_NullDescription() {
        UUID alertId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(alertId)
                .tenantId(TENANT_ID)
                .title("Alert Title")
                .description(null)
                .build();

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(alertId)
                .title("Alert Title")
                .description(null)
                .build();

        when(alertRepository.save(any(String.class), any(FraudAlert.class)))
                .thenReturn(alert);
        when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

        var result = fraudDetectionService.createAlert(
                TENANT_ID, testId, "FRAUD_DETECTED", "HIGH",
                "Alert Title", null
        );

        assertNotNull(result);
        verify(alertRepository).save(eq(TENANT_ID), any(FraudAlert.class));
    }

    // ==================== acknowledgeAlert() Edge Cases ====================

    @Test
    @DisplayName("acknowledgeAlert - Not found throws exception")
    void testAcknowledgeAlert_NotFoundThrowsException() {
        UUID alertId = UUID.randomUUID();
        when(alertRepository.findById(TENANT_ID, alertId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.acknowledgeAlert(TENANT_ID, alertId, "user", "notes");
        });

        assertTrue(exception.getMessage().contains("FraudAlert not found"));
        verify(alertRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("acknowledgeAlert - Null acknowledgedBy")
    void testAcknowledgeAlert_NullAcknowledgedBy() {
        UUID alertId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(alertId)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(alertId)
                .status("ACKNOWLEDGED")
                .build();

        when(alertRepository.findById(TENANT_ID, alertId))
                .thenReturn(Optional.of(alert));
        when(alertRepository.save(any(String.class), any(FraudAlert.class)))
                .thenReturn(alert);
        when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

        var result = fraudDetectionService.acknowledgeAlert(
                TENANT_ID, alertId, null, null
        );

        assertNotNull(result);
        verify(alertRepository).save(eq(TENANT_ID), any(FraudAlert.class));
    }

    // ==================== resolveAlert() Edge Cases ====================

    @Test
    @DisplayName("resolveAlert - Not found throws exception")
    void testResolveAlert_NotFoundThrowsException() {
        UUID alertId = UUID.randomUUID();
        when(alertRepository.findById(TENANT_ID, alertId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.resolveAlert(TENANT_ID, alertId, "user", "notes");
        });

        assertTrue(exception.getMessage().contains("FraudAlert not found"));
        verify(alertRepository, never()).save(any(), any());
    }

    // ==================== getAlertsByStatus() Edge Cases ====================

    @Test
    @DisplayName("getAlertsByStatus - All status values")
    void testGetAlertsByStatus_AllStatusValues() {
        String[] statuses = {
            "PENDING", "ACKNOWLEDGED", "RESOLVED", "ESCALATED", "CLOSED"
        };

        for (String status : statuses) {
            FraudAlert alert = FraudAlert.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .status(status)
                    .build();

            FraudAlertDto dto = FraudAlertDto.builder()
                    .id(UUID.randomUUID())
                    .status(status)
                    .build();

            when(alertRepository.findByStatus(TENANT_ID, status))
                    .thenReturn(List.of(alert));
            when(mapper.toAlertDtoList(List.of(alert)))
                    .thenReturn(List.of(dto));

            var result = fraudDetectionService.getAlertsByStatus(TENANT_ID, status);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(status, result.get(0).getStatus());
        }
    }

    @Test
    @DisplayName("getAlertsByStatus - Empty list when no matches")
    void testGetAlertsByStatus_EmptyList() {
        when(alertRepository.findByStatus(TENANT_ID, "CLOSED"))
                .thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAlertsByStatus(TENANT_ID, "CLOSED");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== createCase() Edge Cases ====================

    @Test
    @DisplayName("createCase - All priority levels")
    void testCreateCase_AllPriorityLevels() {
        String[] priorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL", "URGENT"};

        for (String priority : priorities) {
            FraudCase fraudCase = FraudCase.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .priority(priority)
                    .build();

            FraudCaseDto dto = FraudCaseDto.builder()
                    .id(UUID.randomUUID())
                    .priority(priority)
                    .build();

            when(caseRepository.save(any(String.class), any(FraudCase.class)))
                    .thenReturn(fraudCase);
            when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

            var result = fraudDetectionService.createCase(
                    TENANT_ID, "FRAUD_INVESTIGATION", "Title",
                    "Description", priority, "admin"
            );

            assertNotNull(result);
            assertEquals(priority, result.getPriority());
        }
    }

    @Test
    @DisplayName("createCase - Null assignedTo")
    void testCreateCase_NullAssignedTo() {
        FraudCase fraudCase = FraudCase.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .assignedTo(null)
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(testId)
                .assignedTo(null)
                .build();

        when(caseRepository.save(any(String.class), any(FraudCase.class)))
                .thenReturn(fraudCase);
        when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

        var result = fraudDetectionService.createCase(
                TENANT_ID, "FRAUD_INVESTIGATION", "Title",
                "Description", "HIGH", null
        );

        assertNotNull(result);
        verify(caseRepository).save(eq(TENANT_ID), any(FraudCase.class));
    }

    @Test
    @DisplayName("createCase - All case types")
    void testCreateCase_AllCaseTypes() {
        String[] caseTypes = {
            "FRAUD_INVESTIGATION",
            "MONEY_LAUNDERING",
            "IDENTITY_THEFT",
            "CLAIMS_FRAUD",
            "TRANSACTION_FRAUD"
        };

        for (String caseType : caseTypes) {
            FraudCase fraudCase = FraudCase.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .caseType(caseType)
                    .build();

            FraudCaseDto dto = FraudCaseDto.builder()
                    .id(UUID.randomUUID())
                    .caseType(caseType)
                    .build();

            when(caseRepository.save(any(String.class), any(FraudCase.class)))
                    .thenReturn(fraudCase);
            when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

            var result = fraudDetectionService.createCase(
                    TENANT_ID, caseType, "Title",
                    "Description", "HIGH", "admin"
            );

            assertNotNull(result);
            assertEquals(caseType, result.getCaseType());
        }
    }

    // ==================== getCaseById() Edge Cases ====================

    @Test
    @DisplayName("getCaseById - Not found throws exception")
    void testGetCaseById_NotFoundThrowsException() {
        when(caseRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.getCaseById(TENANT_ID, testId);
        });

        assertTrue(exception.getMessage().contains("FraudCase not found"));
    }

    // ==================== updateCase() Edge Cases ====================

    @Test
    @DisplayName("updateCase - Null assignedTo (no assignment change)")
    void testUpdateCase_NullAssignedTo() {
        FraudCase fraudCase = FraudCase.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .assignedTo("current-user")
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(testId)
                .assignedTo("current-user")
                .build();

        when(caseRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.of(fraudCase));
        when(caseRepository.save(any(String.class), any(FraudCase.class)))
                .thenReturn(fraudCase);
        when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

        var result = fraudDetectionService.updateCase(
                TENANT_ID, testId, null, "finding", "decision"
        );

        assertNotNull(result);
        // assignedTo should remain unchanged when null is passed
        verify(caseRepository).save(eq(TENANT_ID), any(FraudCase.class));
    }

    @Test
    @DisplayName("updateCase - Not found throws exception")
    void testUpdateCase_NotFoundThrowsException() {
        when(caseRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.updateCase(TENANT_ID, testId, "user", "finding", "decision");
        });

        assertTrue(exception.getMessage().contains("FraudCase not found"));
        verify(caseRepository, never()).save(any(), any());
    }

    // ==================== closeCase() Edge Cases ====================

    @Test
    @DisplayName("closeCase - Not found throws exception")
    void testCloseCase_NotFoundThrowsException() {
        when(caseRepository.findById(TENANT_ID, testId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.closeCase(TENANT_ID, testId, "user", "reason", "outcome");
        });

        assertTrue(exception.getMessage().contains("FraudCase not found"));
        verify(caseRepository, never()).save(any(), any());
    }

    // ==================== createPattern() Edge Cases ====================

    @Test
    @DisplayName("createPattern - All pattern types")
    void testCreatePattern_AllPatternTypes() {
        String[] patternTypes = {
            "GEOGRAPHIC", "TEMPORAL", "BEHAVIORAL", "NETWORK", "VELOCITY"
        };

        for (String patternType : patternTypes) {
            FraudPattern pattern = FraudPattern.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .patternType(patternType)
                    .build();

            FraudPatternDto dto = FraudPatternDto.builder()
                    .id(UUID.randomUUID())
                    .patternType(patternType)
                    .build();

            when(patternRepository.save(any(String.class), any(FraudPattern.class)))
                    .thenReturn(pattern);
            when(mapper.toDto(any(FraudPattern.class))).thenReturn(dto);

            var result = fraudDetectionService.createPattern(
                    TENANT_ID, "PatternName", patternType,
                    "Description", Map.of("key", "value")
            );

            assertNotNull(result);
            assertEquals(patternType, result.getPatternType());
        }
    }

    @Test
    @DisplayName("createPattern - Null definition")
    void testCreatePattern_NullDefinition() {
        FraudPattern pattern = FraudPattern.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .patternDefinition(null)
                .build();

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(testId)
                .build();

        when(patternRepository.save(any(String.class), any(FraudPattern.class)))
                .thenReturn(pattern);
        when(mapper.toDto(any(FraudPattern.class))).thenReturn(dto);

        var result = fraudDetectionService.createPattern(
                TENANT_ID, "PatternName", "GEOGRAPHIC",
                "Description", null
        );

        assertNotNull(result);
        verify(patternRepository).save(eq(TENANT_ID), any(FraudPattern.class));
    }

    @Test
    @DisplayName("createPattern - Empty definition")
    void testCreatePattern_EmptyDefinition() {
        FraudPattern pattern = FraudPattern.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .patternDefinition(Collections.emptyMap())
                .build();

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(testId)
                .build();

        when(patternRepository.save(any(String.class), any(FraudPattern.class)))
                .thenReturn(pattern);
        when(mapper.toDto(any(FraudPattern.class))).thenReturn(dto);

        var result = fraudDetectionService.createPattern(
                TENANT_ID, "PatternName", "TEMPORAL",
                "Description", Collections.emptyMap()
        );

        assertNotNull(result);
        verify(patternRepository).save(eq(TENANT_ID), any(FraudPattern.class));
    }

    // ==================== createRule() Edge Cases ====================

    @Test
    @DisplayName("createRule - All rule types")
    void testCreateRule_AllRuleTypes() {
        String[] ruleTypes = {
            "THRESHOLD", "VELOCITY", "PATTERN", "ANOMALY", "COMPOSITE"
        };

        for (String ruleType : ruleTypes) {
            FraudRule rule = FraudRule.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .ruleType(ruleType)
                    .build();

            FraudRuleDto dto = FraudRuleDto.builder()
                    .id(UUID.randomUUID())
                    .ruleType(ruleType)
                    .build();

            when(ruleRepository.save(any(String.class), any(FraudRule.class)))
                    .thenReturn(rule);
            when(mapper.toDto(any(FraudRule.class))).thenReturn(dto);

            var result = fraudDetectionService.createRule(
                    TENANT_ID, "RuleName", "RULE001", "Description",
                    ruleType, null, "BLOCK", 1
            );

            assertNotNull(result);
            assertEquals(ruleType, result.getRuleType());
        }
    }

    @Test
    @DisplayName("createRule - Null conditions")
    void testCreateRule_NullConditions() {
        FraudRule rule = FraudRule.builder()
                .id(testId)
                .tenantId(TENANT_ID)
                .conditions(null)
                .build();

        FraudRuleDto dto = FraudRuleDto.builder()
                .id(testId)
                .build();

        when(ruleRepository.save(any(String.class), any(FraudRule.class)))
                .thenReturn(rule);
        when(mapper.toDto(any(FraudRule.class))).thenReturn(dto);

        var result = fraudDetectionService.createRule(
                TENANT_ID, "RuleName", "RULE001", "Description",
                "THRESHOLD", null, "BLOCK", 1
        );

        assertNotNull(result);
        verify(ruleRepository).save(eq(TENANT_ID), any(FraudRule.class));
    }

    @Test
    @DisplayName("createRule - All action types")
    void testCreateRule_AllActionTypes() {
        String[] actions = {"ALLOW", "BLOCK", "REVIEW", "FLAG", "CHALLENGE"};

        for (String action : actions) {
            FraudRule rule = FraudRule.builder()
                    .id(UUID.randomUUID())
                    .tenantId(TENANT_ID)
                    .action(action)
                    .build();

            FraudRuleDto dto = FraudRuleDto.builder()
                    .id(UUID.randomUUID())
                    .action(action)
                    .build();

            when(ruleRepository.save(any(String.class), any(FraudRule.class)))
                    .thenReturn(rule);
            when(mapper.toDto(any(FraudRule.class))).thenReturn(dto);

            var result = fraudDetectionService.createRule(
                    TENANT_ID, "RuleName", "RULE001", "Description",
                    "THRESHOLD", null, action, 1
            );

            assertNotNull(result);
            assertEquals(action, result.getAction());
        }
    }

    // ==================== getAll methods empty list tests ====================

    @Test
    @DisplayName("getAllDetections - Empty list")
    void testGetAllDetections_EmptyList() {
        when(detectionRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllDetections(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllAlerts - Empty list")
    void testGetAllAlerts_EmptyList() {
        when(alertRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllAlerts(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllCases - Empty list")
    void testGetAllCases_EmptyList() {
        when(caseRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toCaseDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllCases(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllPatterns - Empty list")
    void testGetAllPatterns_EmptyList() {
        when(patternRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toPatternDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllPatterns(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllRules - Empty list")
    void testGetAllRules_EmptyList() {
        when(ruleRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toRuleDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllRules(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getPending methods empty list tests ====================

    @Test
    @DisplayName("getPendingAlerts - Empty list")
    void testGetPendingAlerts_EmptyList() {
        when(alertRepository.findPendingAlerts(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getPendingAlerts(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCriticalAlerts - Empty list")
    void testGetCriticalAlerts_EmptyList() {
        when(alertRepository.findCriticalAlerts(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getCriticalAlerts(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getOpenCases - Empty list")
    void testGetOpenCases_EmptyList() {
        when(caseRepository.findOpenCases(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toCaseDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getOpenCases(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getActivePatterns - Empty list")
    void testGetActivePatterns_EmptyList() {
        when(patternRepository.findActivePatterns(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toPatternDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getActivePatterns(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getActiveRules - Empty list")
    void testGetActiveRules_EmptyList() {
        when(ruleRepository.findActiveRules(TENANT_ID))
                .thenReturn(List.of());
        when(mapper.toRuleDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getActiveRules(TENANT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
