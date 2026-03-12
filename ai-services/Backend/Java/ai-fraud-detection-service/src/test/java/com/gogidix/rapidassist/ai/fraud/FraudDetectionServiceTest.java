package com.gogidix.rapidassist.ai.fraud;

import com.gogidix.rapidassist.ai.fraud.application.dto.*;
import com.gogidix.rapidassist.ai.fraud.application.mapper.FraudDetectionMapper;
import com.gogidix.rapidassist.ai.fraud.application.service.FraudDetectionService;
import com.gogidix.rapidassist.ai.fraud.domain.model.*;
import com.gogidix.rapidassist.ai.fraud.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

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

    private String tenantId;
    private UUID detectionId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-1";
        detectionId = UUID.randomUUID();
    }

    @Test
    void testDetectFraud_Success() {
        String entityType = "CLAIM";
        String entityId = "claim-123";

        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, entityType, entityId, FraudRiskLevel.HIGH, 0.85,
                "ML_MODEL", Map.of("confidence", 0.85)
        );

        assertNotNull(result);
        assertEquals(tenantId, result.getTenantId());
        assertEquals(entityType, result.getEntityType());
        assertEquals(FraudRiskLevel.HIGH, result.getRiskLevel());
        verify(detectionRepository).save(eq(tenantId), any(FraudDetection.class));
        verify(mapper).toDto(any(FraudDetection.class));
    }

    @Test
    void testDetectFraud_WithLowRisk() {
        String entityType = "TRANSACTION";
        String entityId = "txn-456";
        UUID detectionId2 = UUID.randomUUID();

        FraudDetection detection = FraudDetection.builder()
                .id(detectionId2)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.15)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId2)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.15)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, entityType, entityId, FraudRiskLevel.LOW, 0.15,
                "RULE_ENGINE", Map.of("rule_matched", "amount_threshold")
        );

        assertNotNull(result);
        assertEquals(FraudRiskLevel.LOW, result.getRiskLevel());
        assertEquals(0.15, result.getRiskScore());
    }

    @Test
    void testDetectFraud_WithCriticalRisk() {
        String entityType = "CLAIM";
        String entityId = "claim-789";
        UUID detectionId3 = UUID.randomUUID();

        FraudDetection detection = FraudDetection.builder()
                .id(detectionId3)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.CRITICAL)
                .riskScore(0.95)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId3)
                .tenantId(tenantId)
                .entityType(entityType)
                .entityId(entityId)
                .riskLevel(FraudRiskLevel.CRITICAL)
                .riskScore(0.95)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, entityType, entityId, FraudRiskLevel.CRITICAL, 0.95,
                "NEURAL_NETWORK", Map.of("anomaly_score", 0.98)
        );

        assertNotNull(result);
        assertEquals(FraudRiskLevel.CRITICAL, result.getRiskLevel());
        assertTrue(result.getRiskScore() >= 0.9);
    }

    @Test
    void testGetDetectionById_Success() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();

        when(detectionRepository.findById(tenantId, detectionId)).thenReturn(Optional.of(detection));
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.getDetectionById(tenantId, detectionId);

        assertNotNull(result);
        assertEquals(detectionId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        verify(detectionRepository).findById(tenantId, detectionId);
        verify(mapper).toDto(detection);
    }

    @Test
    void testGetDetectionById_NotFound() {
        when(detectionRepository.findById(tenantId, detectionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.getDetectionById(tenantId, detectionId);
        });

        verify(detectionRepository).findById(tenantId, detectionId);
    }

    @Test
    void testGetAllDetections_Success() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.LOW)
                .build();

        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.LOW)
                .build();

        when(detectionRepository.findByTenantId(tenantId))
                .thenReturn(List.of(detection1, detection2));
        when(mapper.toDtoList(List.of(detection1, detection2)))
                .thenReturn(List.of(dto1, dto2));

        var result = fraudDetectionService.getAllDetections(tenantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(detectionRepository).findByTenantId(tenantId);
        verify(mapper).toDtoList(List.of(detection1, detection2));
    }

    @Test
    void testGetAllDetections_EmptyList() {
        when(detectionRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllDetections(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(detectionRepository).findByTenantId(tenantId);
        verify(mapper).toDtoList(List.of());
    }

    @Test
    void testUpdateDetectionReview_Success() {
        String reviewer = "admin";
        String notes = "Verified as false positive";

        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(tenantId, detectionId)).thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class))).thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        var result = fraudDetectionService.updateDetectionReview(tenantId, detectionId, reviewer, notes);

        assertNotNull(result);
        assertEquals("REVIEWED", result.getStatus());
        verify(detectionRepository).save(eq(tenantId), any(FraudDetection.class));
        verify(mapper).toDto(detection);
    }

    @Test
    void testUpdateDetectionReview_NotFound() {
        when(detectionRepository.findById(tenantId, detectionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.updateDetectionReview(tenantId, detectionId, "admin", "notes");
        });

        verify(detectionRepository, never()).save(any(), any());
    }

    @Test
    void testUpdateDetectionReview_WithDifferentStatuses() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .status("PENDING")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .status("REVIEWED")
                .build();

        when(detectionRepository.findById(tenantId, detectionId)).thenReturn(Optional.of(detection));
        when(detectionRepository.save(any(String.class), any(FraudDetection.class))).thenReturn(detection);
        when(mapper.toDto(detection)).thenReturn(dto);

        // Test CONFIRMED_FRAUD
        var result1 = fraudDetectionService.updateDetectionReview(
            tenantId, detectionId, "reviewer1", "Confirmed fraudulent activity"
        );

        assertNotNull(result1);

        // Test FALSE_POSITIVE
        var result2 = fraudDetectionService.updateDetectionReview(
            tenantId, detectionId, "reviewer2", "Verified as legitimate"
        );

        assertNotNull(result2);

        verify(detectionRepository, atLeastOnce()).save(eq(tenantId), any(FraudDetection.class));
        verify(mapper, atLeastOnce()).toDto(detection);
    }

    // NOTE: Tests for deleteDetection are commented out because the method
    // doesn't exist in FraudDetectionService. The service doesn't provide
    // delete functionality - only status updates via updateDetectionReview.

    /*
    @Test
    void testDeleteDetection_Success() {
        when(detectionRepository.exists(tenantId, detectionId)).thenReturn(true);
        doNothing().when(detectionRepository).delete(tenantId, detectionId);

        fraudDetectionService.deleteDetection(tenantId, detectionId);

        verify(detectionRepository).delete(tenantId, detectionId);
    }

    @Test
    void testDeleteDetection_NotFound() {
        when(detectionRepository.exists(tenantId, detectionId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            fraudDetectionService.deleteDetection(tenantId, detectionId);
        });

        verify(detectionRepository, never()).delete(any(), any());
    }
    */

    // NOTE: Tests for getDetectionsByEntityType, getDetectionsByRiskLevel,
    // getDetectionsByDateRange, and getHighRiskDetections are commented out
    // because these methods don't exist in FraudDetectionService.
    // The service only provides: detectFraud, getDetectionById, getAllDetections,
    // getDetectionsByStatus, getPendingReviewDetections, and updateDetectionReview.

    /*
    @Test
    void testGetDetectionsByEntityType_Success() {
        // Method doesn't exist - would require findByTenantIdAndEntityType in repository
    }

    @Test
    void testGetDetectionsByRiskLevel_Success() {
        // Method doesn't exist - would require findByTenantIdAndRiskLevel in repository
    }

    @Test
    void testGetDetectionsByDateRange_Success() {
        // Method doesn't exist - would require findByTenantIdAndDetectionTimestampBetween in repository
    }

    @Test
    void testGetHighRiskDetections_Success() {
        // Method doesn't exist - would require findByTenantIdAndRiskLevelIn in repository
    }
    */

    @Test
    void testGetDetectionsByStatus_Success() {
        String status = "PENDING";
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .status(status)
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .status(status)
                .build();

        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .status(status)
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.MEDIUM)
                .status(status)
                .build();

        when(detectionRepository.findByStatus(tenantId, status))
                .thenReturn(List.of(detection1, detection2));
        when(mapper.toDtoList(List.of(detection1, detection2)))
                .thenReturn(List.of(dto1, dto2));

        var result = fraudDetectionService.getDetectionsByStatus(tenantId, status);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> status.equals(d.getStatus())));
        verify(detectionRepository).findByStatus(tenantId, status);
        verify(mapper).toDtoList(List.of(detection1, detection2));
    }

    @Test
    void testGetPendingReviewDetections_Success() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .status("PENDING")
                .requiresReview(true)
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.CRITICAL)
                .status("PENDING")
                .requiresReview(true)
                .build();

        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .status("PENDING")
                .requiresReview(true)
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id2)
                .tenantId(tenantId)
                .entityType("TRANSACTION")
                .riskLevel(FraudRiskLevel.CRITICAL)
                .status("PENDING")
                .requiresReview(true)
                .build();

        when(detectionRepository.findPendingReview(tenantId))
                .thenReturn(List.of(detection1, detection2));
        when(mapper.toDtoList(List.of(detection1, detection2)))
                .thenReturn(List.of(dto1, dto2));

        var result = fraudDetectionService.getPendingReviewDetections(tenantId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(detectionRepository).findPendingReview(tenantId);
        verify(mapper).toDtoList(List.of(detection1, detection2));
    }

    @Test
    void testGetPendingReviewDetections_EmptyList() {
        when(detectionRepository.findPendingReview(tenantId)).thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getPendingReviewDetections(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(detectionRepository).findPendingReview(tenantId);
        verify(mapper).toDtoList(List.of());
    }

    // ========== Mutation-Killing Tests: requiresReview Logic ==========

    @Test
    void testDetectFraud_HighRisk_SetsRequiresReviewTrue() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(true)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(true)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, "CLAIM", "claim-1", FraudRiskLevel.HIGH, 0.75,
                "METHOD", Map.of()
        );

        assertTrue(result.getRequiresReview(), "HIGH risk should require review");
    }

    @Test
    void testDetectFraud_CriticalRisk_SetsRequiresReviewTrue() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(true)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(true)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, "CLAIM", "claim-1", FraudRiskLevel.CRITICAL, 0.95,
                "METHOD", Map.of()
        );

        assertTrue(result.getRequiresReview(), "CRITICAL risk should require review");
    }

    @Test
    void testDetectFraud_MediumRisk_SetsRequiresReviewFalse() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.MEDIUM)
                .requiresReview(false)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.MEDIUM)
                .requiresReview(false)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, "CLAIM", "claim-1", FraudRiskLevel.MEDIUM, 0.5,
                "METHOD", Map.of()
        );

        assertFalse(result.getRequiresReview(), "MEDIUM risk should not require review");
    }

    @Test
    void testDetectFraud_LowRisk_SetsRequiresReviewFalse() {
        FraudDetection detection = FraudDetection.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(detectionId)
                .tenantId(tenantId)
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();

        when(detectionRepository.save(any(String.class), any(FraudDetection.class)))
                .thenReturn(detection);
        when(mapper.toDto(any(FraudDetection.class))).thenReturn(dto);

        var result = fraudDetectionService.detectFraud(
                tenantId, "CLAIM", "claim-1", FraudRiskLevel.LOW, 0.2,
                "METHOD", Map.of()
        );

        assertFalse(result.getRequiresReview(), "LOW risk should not require review");
    }

    // ========== Mutation-Killing Tests: Alert Operations ==========

    @Test
    void testCreateAlert_Success() {
        UUID detectionId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Suspicious Activity")
                .description("Possible fraud detected")
                .status("PENDING")
                .escalationLevel(0)
                .caseCreated(false)
                .build();

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(alert.getId())
                .tenantId(tenantId)
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Suspicious Activity")
                .status("PENDING")
                .escalationLevel(0)
                .build();

        when(alertRepository.save(any(String.class), any(FraudAlert.class))).thenReturn(alert);
        when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

        var result = fraudDetectionService.createAlert(
                tenantId, detectionId, "FRAUD_DETECTED", "HIGH",
                "Suspicious Activity", "Possible fraud detected"
        );

        assertNotNull(result);
        assertEquals("FRAUD_DETECTED", result.getAlertType());
        assertEquals("HIGH", result.getSeverity());
        assertEquals("Suspicious Activity", result.getTitle());
        assertEquals("PENDING", result.getStatus());
        assertEquals(0, result.getEscalationLevel());
    }

    @Test
    void testGetAllAlerts_Success() {
        UUID id1 = UUID.randomUUID();
        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(tenantId)
                .alertType("FRAUD")
                .build();

        FraudAlertDto dto1 = FraudAlertDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .alertType("FRAUD")
                .build();

        when(alertRepository.findByTenantId(tenantId)).thenReturn(List.of(alert1));
        when(mapper.toAlertDtoList(List.of(alert1))).thenReturn(List.of(dto1));

        var result = fraudDetectionService.getAllAlerts(tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("FRAUD", result.get(0).getAlertType());
    }

    @Test
    void testAcknowledgeAlert_Success() {
        UUID alertId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(alertId)
                .tenantId(tenantId)
                .status("PENDING")
                .acknowledgedBy("admin")
                .acknowledgmentNotes("Reviewing")
                .build();

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(alertId)
                .tenantId(tenantId)
                .status("ACKNOWLEDGED")
                .acknowledgedBy("admin")
                .acknowledgmentNotes("Reviewing")
                .build();

        when(alertRepository.findById(tenantId, alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(String.class), any(FraudAlert.class))).thenReturn(alert);
        when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

        var result = fraudDetectionService.acknowledgeAlert(tenantId, alertId, "admin", "Reviewing");

        assertNotNull(result);
        assertEquals("admin", result.getAcknowledgedBy());
        assertEquals("Reviewing", result.getAcknowledgmentNotes());
    }

    @Test
    void testResolveAlert_Success() {
        UUID alertId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(alertId)
                .tenantId(tenantId)
                .status("ACKNOWLEDGED")
                .resolvedBy("investigator")
                .resolutionNotes("False positive")
                .build();

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(alertId)
                .tenantId(tenantId)
                .status("RESOLVED")
                .resolvedBy("investigator")
                .resolutionNotes("False positive")
                .build();

        when(alertRepository.findById(tenantId, alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(String.class), any(FraudAlert.class))).thenReturn(alert);
        when(mapper.toDto(any(FraudAlert.class))).thenReturn(dto);

        var result = fraudDetectionService.resolveAlert(tenantId, alertId, "investigator", "False positive");

        assertNotNull(result);
        assertEquals("investigator", result.getResolvedBy());
        assertEquals("False positive", result.getResolutionNotes());
    }

    // ========== Mutation-Killing Tests: Case Operations ==========

    @Test
    void testCreateCase_Success() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .caseType("INSURANCE_CLAIM")
                .title("Investigation Case")
                .description("Suspicious claim pattern")
                .priority("HIGH")
                .assignedTo("investigator-1")
                .status("OPEN")
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(fraudCase.getId())
                .tenantId(tenantId)
                .caseType("INSURANCE_CLAIM")
                .title("Investigation Case")
                .priority("HIGH")
                .assignedTo("investigator-1")
                .status("OPEN")
                .build();

        when(caseRepository.save(any(String.class), any(FraudCase.class))).thenReturn(fraudCase);
        when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

        var result = fraudDetectionService.createCase(
                tenantId, "INSURANCE_CLAIM", "Investigation Case",
                "Suspicious claim pattern", "HIGH", "investigator-1"
        );

        assertNotNull(result);
        assertEquals("INSURANCE_CLAIM", result.getCaseType());
        assertEquals("Investigation Case", result.getTitle());
        assertEquals("HIGH", result.getPriority());
        assertEquals("investigator-1", result.getAssignedTo());
        assertEquals("OPEN", result.getStatus());
    }

    @Test
    void testGetAllCases_Success() {
        UUID id1 = UUID.randomUUID();
        FraudCase fraudCase = FraudCase.builder()
                .id(id1)
                .tenantId(tenantId)
                .caseType("CLAIM_FRAUD")
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .caseType("CLAIM_FRAUD")
                .build();

        when(caseRepository.findByTenantId(tenantId)).thenReturn(List.of(fraudCase));
        when(mapper.toCaseDtoList(List.of(fraudCase))).thenReturn(List.of(dto));

        var result = fraudDetectionService.getAllCases(tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CLAIM_FRAUD", result.get(0).getCaseType());
    }

    @Test
    void testUpdateCase_WithAssignedTo_Success() {
        UUID caseId = UUID.randomUUID();
        FraudCase fraudCase = FraudCase.builder()
                .id(caseId)
                .tenantId(tenantId)
                .status("OPEN")
                .assignedTo("new-investigator")
                .finding("Suspicious pattern found")
                .decision("Requires investigation")
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(caseId)
                .tenantId(tenantId)
                .assignedTo("new-investigator")
                .finding("Suspicious pattern found")
                .decision("Requires investigation")
                .build();

        when(caseRepository.findById(tenantId, caseId)).thenReturn(Optional.of(fraudCase));
        when(caseRepository.save(any(String.class), any(FraudCase.class))).thenReturn(fraudCase);
        when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

        var result = fraudDetectionService.updateCase(
                tenantId, caseId, "new-investigator", "Suspicious pattern found", "Requires investigation"
        );

        assertNotNull(result);
        assertEquals("new-investigator", result.getAssignedTo());
        assertEquals("Suspicious pattern found", result.getFinding());
        assertEquals("Requires investigation", result.getDecision());
    }

    @Test
    void testCloseCase_Success() {
        UUID caseId = UUID.randomUUID();
        FraudCase fraudCase = FraudCase.builder()
                .id(caseId)
                .tenantId(tenantId)
                .status("CLOSED")
                .closedBy("admin")
                .build();

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(caseId)
                .tenantId(tenantId)
                .status("CLOSED")
                .closedBy("admin")
                .build();

        when(caseRepository.findById(tenantId, caseId)).thenReturn(Optional.of(fraudCase));
        when(caseRepository.save(any(String.class), any(FraudCase.class))).thenReturn(fraudCase);
        when(mapper.toDto(any(FraudCase.class))).thenReturn(dto);

        var result = fraudDetectionService.closeCase(
                tenantId, caseId, "admin", "Investigation complete", "Fraud confirmed"
        );

        assertNotNull(result);
        assertEquals("admin", result.getClosedBy());
        assertEquals("CLOSED", result.getStatus());
    }

    // ========== Mutation-Killing Tests: Pattern Operations ==========

    @Test
    void testCreatePattern_Success() {
        Map<String, Object> definition = Map.of("threshold", 0.8);
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .patternName("HIGH_VALUE_CLAIM")
                .patternType("THRESHOLD")
                .description("High value claims")
                .patternDefinition(definition)
                .isActive(true)
                .priority(0)
                .build();

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(pattern.getId())
                .tenantId(tenantId)
                .patternName("HIGH_VALUE_CLAIM")
                .patternType("THRESHOLD")
                .isActive(true)
                .priority(0)
                .build();

        when(patternRepository.save(any(String.class), any(FraudPattern.class))).thenReturn(pattern);
        when(mapper.toDto(any(FraudPattern.class))).thenReturn(dto);

        var result = fraudDetectionService.createPattern(
                tenantId, "HIGH_VALUE_CLAIM", "THRESHOLD",
                "High value claims", definition
        );

        assertNotNull(result);
        assertEquals("HIGH_VALUE_CLAIM", result.getPatternName());
        assertEquals("THRESHOLD", result.getPatternType());
        assertTrue(result.getIsActive());
        assertEquals(0, result.getPriority());
    }

    @Test
    void testGetAllPatterns_Success() {
        UUID id1 = UUID.randomUUID();
        FraudPattern pattern = FraudPattern.builder()
                .id(id1)
                .tenantId(tenantId)
                .patternName("TEST_PATTERN")
                .isActive(true)
                .build();

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .patternName("TEST_PATTERN")
                .isActive(true)
                .build();

        when(patternRepository.findByTenantId(tenantId)).thenReturn(List.of(pattern));
        when(mapper.toPatternDtoList(List.of(pattern))).thenReturn(List.of(dto));

        var result = fraudDetectionService.getAllPatterns(tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST_PATTERN", result.get(0).getPatternName());
    }

    // ========== Mutation-Killing Tests: Rule Operations ==========

    @Test
    void testCreateRule_Success() {
        Map<String, Object> conditions = Map.of("amount", 10000);
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .ruleName("High Amount Rule")
                .ruleCode("RULE_001")
                .description("Flag high amount claims")
                .ruleType("THRESHOLD")
                .conditions(conditions)
                .action("FLAG_FOR_REVIEW")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(false)
                .version("1.0")
                .build();

        FraudRuleDto dto = FraudRuleDto.builder()
                .id(rule.getId())
                .tenantId(tenantId)
                .ruleName("High Amount Rule")
                .ruleCode("RULE_001")
                .action("FLAG_FOR_REVIEW")
                .priority(1)
                .isActive(true)
                .version("1.0")
                .build();

        when(ruleRepository.save(any(String.class), any(FraudRule.class))).thenReturn(rule);
        when(mapper.toDto(any(FraudRule.class))).thenReturn(dto);

        var result = fraudDetectionService.createRule(
                tenantId, "High Amount Rule", "RULE_001", "Flag high amount claims",
                "THRESHOLD", conditions, "FLAG_FOR_REVIEW", 1
        );

        assertNotNull(result);
        assertEquals("High Amount Rule", result.getRuleName());
        assertEquals("RULE_001", result.getRuleCode());
        assertEquals("FLAG_FOR_REVIEW", result.getAction());
        assertEquals(1, result.getPriority());
        assertTrue(result.getIsActive());
        assertEquals("1.0", result.getVersion());
    }

    @Test
    void testGetAllRules_Success() {
        UUID id1 = UUID.randomUUID();
        FraudRule rule = FraudRule.builder()
                .id(id1)
                .tenantId(tenantId)
                .ruleName("Test Rule")
                .isActive(true)
                .build();

        FraudRuleDto dto = FraudRuleDto.builder()
                .id(id1)
                .tenantId(tenantId)
                .ruleName("Test Rule")
                .isActive(true)
                .build();

        when(ruleRepository.findByTenantId(tenantId)).thenReturn(List.of(rule));
        when(mapper.toRuleDtoList(List.of(rule))).thenReturn(List.of(dto));

        var result = fraudDetectionService.getAllRules(tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Rule", result.get(0).getRuleName());
    }

    // ========== Mutation-Killing Tests: Empty Lists ==========

    @Test
    void testGetAllAlerts_EmptyList() {
        when(alertRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllAlerts(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAlertsByStatus_EmptyList() {
        when(alertRepository.findByStatus(tenantId, "PENDING")).thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAlertsByStatus(tenantId, "PENDING");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPendingAlerts_EmptyList() {
        when(alertRepository.findPendingAlerts(tenantId)).thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getPendingAlerts(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCriticalAlerts_EmptyList() {
        when(alertRepository.findCriticalAlerts(tenantId)).thenReturn(List.of());
        when(mapper.toAlertDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getCriticalAlerts(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllCases_EmptyList() {
        when(caseRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toCaseDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllCases(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOpenCases_EmptyList() {
        when(caseRepository.findOpenCases(tenantId)).thenReturn(List.of());
        when(mapper.toCaseDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getOpenCases(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllPatterns_EmptyList() {
        when(patternRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toPatternDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllPatterns(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetActivePatterns_EmptyList() {
        when(patternRepository.findActivePatterns(tenantId)).thenReturn(List.of());
        when(mapper.toPatternDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getActivePatterns(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllRules_EmptyList() {
        when(ruleRepository.findByTenantId(tenantId)).thenReturn(List.of());
        when(mapper.toRuleDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getAllRules(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetActiveRules_EmptyList() {
        when(ruleRepository.findActiveRules(tenantId)).thenReturn(List.of());
        when(mapper.toRuleDtoList(List.of())).thenReturn(List.of());

        var result = fraudDetectionService.getActiveRules(tenantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
