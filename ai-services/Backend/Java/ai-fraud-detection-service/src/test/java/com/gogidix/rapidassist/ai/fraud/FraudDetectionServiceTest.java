package com.gogidix.rapidassist.ai.fraud;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
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
}
