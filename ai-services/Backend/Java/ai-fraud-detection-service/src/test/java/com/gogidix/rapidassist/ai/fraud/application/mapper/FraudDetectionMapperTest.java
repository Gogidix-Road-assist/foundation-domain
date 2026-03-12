package com.gogidix.rapidassist.ai.fraud.application.mapper;

import com.gogidix.rapidassist.ai.fraud.application.dto.FraudDetectionDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudAlertDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudCaseDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudPatternDto;
import com.gogidix.rapidassist.ai.fraud.application.dto.FraudRuleDto;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudCase;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudPattern;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRule;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudDetectionMapper Tests")
class FraudDetectionMapperTest {

    private final FraudDetectionMapper mapper = new FraudDetectionMapperImpl();

    @Test
    @DisplayName("toDto should map FraudDetection to FraudDetectionDto")
    void testToDto_FraudDetection() {
        UUID id = UUID.randomUUID();
        Map<String, Object> details = new HashMap<>();
        details.put("confidence", 0.95);

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .entityId("claim-456")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(List.of("PATTERN_1"))
                .status("PENDING")
                .requiresReview(true)
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        FraudDetectionDto dto = mapper.toDto(detection);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("CLAIM", dto.getEntityType());
        assertEquals(FraudRiskLevel.HIGH, dto.getRiskLevel());
        assertEquals(0.85, dto.getRiskScore());
    }

    @Test
    @DisplayName("toDto should map FraudAlert to FraudAlertDto")
    void testToDto_FraudAlert() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId("tenant-123")
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Suspicious activity")
                .status("PENDING")
                .escalationLevel(0)
                .createdAt(LocalDateTime.now())
                .build();

        FraudAlertDto dto = mapper.toDto(alert);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("FRAUD_DETECTED", dto.getAlertType());
        assertEquals("HIGH", dto.getSeverity());
    }

    @Test
    @DisplayName("toDto should map FraudCase to FraudCaseDto")
    void testToDto_FraudCase() {
        UUID id = UUID.randomUUID();

        FraudCase fraudCase = FraudCase.builder()
                .id(id)
                .tenantId("tenant-123")
                .caseNumber("FC-123456")
                .caseType("INSURANCE")
                .title("Investigation case")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo("investigator-1")
                .linkedAlerts(new ArrayList<>())
                .linkedDetections(new ArrayList<>())
                .openedDate(LocalDateTime.now())
                .build();

        FraudCaseDto dto = mapper.toDto(fraudCase);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("FC-123456", dto.getCaseNumber());
        assertEquals("OPEN", dto.getStatus());
    }

    @Test
    @DisplayName("toDto should map FraudPattern to FraudPatternDto")
    void testToDto_FraudPattern() {
        UUID id = UUID.randomUUID();
        Map<String, Object> definition = new HashMap<>();
        definition.put("threshold", 0.8);

        FraudPattern pattern = FraudPattern.builder()
                .id(id)
                .tenantId("tenant-123")
                .patternName("HIGH_VALUE")
                .patternType("THRESHOLD")
                .patternDefinition(definition)
                .isActive(true)
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .build();

        FraudPatternDto dto = mapper.toDto(pattern);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("HIGH_VALUE", dto.getPatternName());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("toDto should map FraudRule to FraudRuleDto")
    void testToDto_FraudRule() {
        UUID id = UUID.randomUUID();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("amount", 10000);

        FraudRule rule = FraudRule.builder()
                .id(id)
                .tenantId("tenant-123")
                .ruleName("High Amount Rule")
                .ruleCode("RULE_001")
                .conditions(conditions)
                .action("FLAG")
                .priority(1)
                .isActive(true)
                .executionCount(50)
                .triggerCount(10)
                .build();

        FraudRuleDto dto = mapper.toDto(rule);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("High Amount Rule", dto.getRuleName());
        assertEquals("RULE_001", dto.getRuleCode());
        assertTrue(dto.getIsActive());
    }

    @Test
    @DisplayName("toDtoList should map List<FraudDetection> to List<FraudDetectionDto>")
    void testToDtoList_FraudDetectionList() {
        List<FraudDetection> detections = List.of(
                FraudDetection.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudDetection.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudDetection.builder().id(UUID.randomUUID()).tenantId("tenant-1").build()
        );

        List<FraudDetectionDto> dtos = mapper.toDtoList(detections);

        assertNotNull(dtos);
        assertEquals(3, dtos.size());
        assertEquals(detections.get(0).getId(), dtos.get(0).getId());
    }

    @Test
    @DisplayName("toAlertDtoList should map List<FraudAlert> to List<FraudAlertDto>")
    void testToAlertDtoList_FraudAlertList() {
        List<FraudAlert> alerts = List.of(
                FraudAlert.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudAlert.builder().id(UUID.randomUUID()).tenantId("tenant-1").build()
        );

        List<FraudAlertDto> dtos = mapper.toAlertDtoList(alerts);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
    }

    @Test
    @DisplayName("toCaseDtoList should map List<FraudCase> to List<FraudCaseDto>")
    void testToCaseDtoList_FraudCaseList() {
        List<FraudCase> cases = List.of(
                FraudCase.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudCase.builder().id(UUID.randomUUID()).tenantId("tenant-1").build()
        );

        List<FraudCaseDto> dtos = mapper.toCaseDtoList(cases);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
    }

    @Test
    @DisplayName("toPatternDtoList should map List<FraudPattern> to List<FraudPatternDto>")
    void testToPatternDtoList_FraudPatternList() {
        List<FraudPattern> patterns = List.of(
                FraudPattern.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudPattern.builder().id(UUID.randomUUID()).tenantId("tenant-1").build()
        );

        List<FraudPatternDto> dtos = mapper.toPatternDtoList(patterns);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
    }

    @Test
    @DisplayName("toRuleDtoList should map List<FraudRule> to List<FraudRuleDto>")
    void testToRuleDtoList_FraudRuleList() {
        List<FraudRule> rules = List.of(
                FraudRule.builder().id(UUID.randomUUID()).tenantId("tenant-1").build(),
                FraudRule.builder().id(UUID.randomUUID()).tenantId("tenant-1").build()
        );

        List<FraudRuleDto> dtos = mapper.toRuleDtoList(rules);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
    }

    @Test
    @DisplayName("toDto should handle null input gracefully")
    void testToDto_NullInput() {
        FraudDetectionDto dto = mapper.toDto((FraudDetection) null);

        assertNull(dto);
    }

    @Test
    @DisplayName("toDtoList should handle empty list")
    void testToDtoList_EmptyList() {
        List<FraudDetectionDto> dtos = mapper.toDtoList(new ArrayList<>());

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("toEntity should map FraudDetectionDto to FraudDetection")
    void testToEntity_FraudDetection() {
        UUID id = UUID.randomUUID();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(id)
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .build();

        FraudDetection entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("tenant-123", entity.getTenantId());
        assertEquals(FraudRiskLevel.HIGH, entity.getRiskLevel());
    }

    @Test
    @DisplayName("updateEntityFromDto should update existing entity")
    void testUpdateEntityFromDto() {
        FraudDetection original = FraudDetection.builder()
                .id(UUID.randomUUID())
                .status("PENDING")
                .riskScore(0.5)
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .status("REVIEWED")
                .riskScore(0.8)
                .build();

        mapper.updateEntityFromDto(dto, original);

        assertEquals("REVIEWED", original.getStatus());
        assertEquals(0.8, original.getRiskScore());
    }

    // ========== Mutation-Killing Tests: Complete Field Mapping ==========

    @Test
    @DisplayName("toDto should map all FraudDetection fields")
    void testToDto_AllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        List<String> patterns = List.of("pattern1", "pattern2");

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .entityId("claim-456")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(patterns)
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .detectedAt(now)
                .reviewedBy("reviewer")
                .reviewedAt(now.plusHours(1))
                .reviewNotes("Confirmed fraud")
                .metadata(Map.of("source", "api"))
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        FraudDetectionDto dto = mapper.toDto(detection);

        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("CLAIM", dto.getEntityType());
        assertEquals("claim-456", dto.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, dto.getRiskLevel());
        assertEquals(0.85, dto.getRiskScore());
        assertEquals("ML_MODEL", dto.getDetectionMethod());
        assertEquals(details, dto.getDetectionDetails());
        assertEquals(patterns, dto.getDetectedPatterns());
        assertEquals("PENDING", dto.getStatus());
        assertTrue(dto.getRequiresReview());
        assertEquals("admin", dto.getAssignedTo());
        assertEquals("reviewer", dto.getReviewedBy());
        assertEquals("Confirmed fraud", dto.getReviewNotes());
        assertEquals(Map.of("source", "api"), dto.getMetadata());
    }

    @Test
    @DisplayName("toDto should map all FraudAlert fields")
    void testToDto_FraudAlert_AllFields() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId("tenant-123")
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Suspicious activity")
                .description("Possible fraud")
                .alertDetails(Map.of("detail", "value"))
                .status("PENDING")
                .assignedTo("admin")
                .acknowledgedAt(now)
                .acknowledgedBy("admin")
                .acknowledgmentNotes("Reviewing")
                .resolvedAt(now.plusHours(1))
                .resolvedBy("investigator")
                .resolutionNotes("False positive")
                .fraudCaseId(caseId)
                .caseCreated(true)
                .escalationLevel(1)
                .metadata(Map.of("meta", "value"))
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        FraudAlertDto dto = mapper.toDto(alert);

        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals(detectionId, dto.getFraudDetectionId());
        assertEquals("FRAUD_DETECTED", dto.getAlertType());
        assertEquals("HIGH", dto.getSeverity());
        assertEquals("Suspicious activity", dto.getTitle());
        assertEquals("Possible fraud", dto.getDescription());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("admin", dto.getAssignedTo());
        assertEquals("admin", dto.getAcknowledgedBy());
        assertEquals("Reviewing", dto.getAcknowledgmentNotes());
        assertEquals("investigator", dto.getResolvedBy());
        assertEquals("False positive", dto.getResolutionNotes());
        assertEquals(caseId, dto.getFraudCaseId());
        assertTrue(dto.getCaseCreated());
        assertEquals(1, dto.getEscalationLevel());
    }

    @Test
    @DisplayName("toDto should map all FraudCase fields")
    void testToDto_FraudCase_AllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        FraudCase fraudCase = FraudCase.builder()
                .id(id)
                .tenantId("tenant-123")
                .caseNumber("FC-123456")
                .caseType("INSURANCE")
                .title("Investigation")
                .description("Fraud investigation")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo("investigator-1")
                .assignedTeam("Team A")
                .linkedAlerts(List.of(UUID.randomUUID()))
                .linkedDetections(List.of(UUID.randomUUID()))
                .investigationSummary("Summary")
                .investigationDetails(Map.of("detail", "value"))
                .finding("Fraud confirmed")
                .decision("Reject claim")
                .actionTaken("Block payment")
                .outcome("Claim rejected")
                .estimatedLoss(1000.0)
                .recoveredAmount(500.0)
                .openedDate(now)
                .assignedDate(now.plusHours(1))
                .startDate(now.plusHours(2))
                .closedDate(now.plusDays(1))
                .closedBy("admin")
                .closureReason("Completed")
                .durationDays(5)
                .createdBy("creator")
                .updatedBy("updater")
                .metadata(Map.of("meta", "value"))
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        FraudCaseDto dto = mapper.toDto(fraudCase);

        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("FC-123456", dto.getCaseNumber());
        assertEquals("INSURANCE", dto.getCaseType());
        assertEquals("Investigation", dto.getTitle());
        assertEquals("OPEN", dto.getStatus());
        assertEquals("HIGH", dto.getPriority());
        assertEquals("investigator-1", dto.getAssignedTo());
        assertEquals("Team A", dto.getAssignedTeam());
        assertEquals("Summary", dto.getInvestigationSummary());
        assertEquals("Fraud confirmed", dto.getFinding());
        assertEquals("Reject claim", dto.getDecision());
        assertEquals("Block payment", dto.getActionTaken());
        assertEquals("Claim rejected", dto.getOutcome());
        assertEquals(1000.0, dto.getEstimatedLoss());
        assertEquals(500.0, dto.getRecoveredAmount());
        assertEquals(5, dto.getDurationDays());
    }

    @Test
    @DisplayName("toDto should map all FraudPattern fields")
    void testToDto_FraudPattern_AllFields() {
        UUID id = UUID.randomUUID();
        Map<String, Object> definition = Map.of("threshold", 0.8);

        FraudPattern pattern = FraudPattern.builder()
                .id(id)
                .tenantId("tenant-123")
                .patternName("HIGH_VALUE")
                .patternType("THRESHOLD")
                .description("High value pattern")
                .patternDefinition(definition)
                .detectionAlgorithm("NEURAL_NETWORK")
                .priority(1)
                .isActive(true)
                .confidenceThreshold(0.75)
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .precision(0.8)
                .recall(0.85)
                .createdBy("creator")
                .updatedBy("updater")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .metadata(Map.of("meta", "value"))
                .build();

        FraudPatternDto dto = mapper.toDto(pattern);

        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("HIGH_VALUE", dto.getPatternName());
        assertEquals("THRESHOLD", dto.getPatternType());
        assertEquals("High value pattern", dto.getDescription());
        assertEquals("NEURAL_NETWORK", dto.getDetectionAlgorithm());
        assertEquals(1, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertEquals(0.75, dto.getConfidenceThreshold());
        assertEquals(100, dto.getDetectionCount());
        assertEquals(80, dto.getTruePositiveCount());
        assertEquals(20, dto.getFalsePositiveCount());
        assertEquals(0.8, dto.getPrecision());
        assertEquals(0.85, dto.getRecall());
    }

    @Test
    @DisplayName("toDto should map all FraudRule fields")
    void testToDto_FraudRule_AllFields() {
        UUID id = UUID.randomUUID();
        Map<String, Object> conditions = Map.of("amount", 10000);

        FraudRule rule = FraudRule.builder()
                .id(id)
                .tenantId("tenant-123")
                .ruleName("High Amount Rule")
                .ruleCode("RULE_001")
                .description("High amount check")
                .ruleType("THRESHOLD")
                .conditions(conditions)
                .action("FLAG")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(100)
                .triggerCount(50)
                .falsePositiveRate(0.1)
                .version("1.0")
                .createdBy("creator")
                .updatedBy("updater")
                .lastTriggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .metadata(Map.of("meta", "value"))
                .build();

        FraudRuleDto dto = mapper.toDto(rule);

        assertEquals(id, dto.getId());
        assertEquals("tenant-123", dto.getTenantId());
        assertEquals("High Amount Rule", dto.getRuleName());
        assertEquals("RULE_001", dto.getRuleCode());
        assertEquals("High amount check", dto.getDescription());
        assertEquals("THRESHOLD", dto.getRuleType());
        assertEquals("FLAG", dto.getAction());
        assertEquals(1, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertFalse(dto.getAutoBlock());
        assertTrue(dto.getRequireReview());
        assertEquals(100, dto.getExecutionCount());
        assertEquals(50, dto.getTriggerCount());
        assertEquals(0.1, dto.getFalsePositiveRate());
        assertEquals("1.0", dto.getVersion());
    }

    @Test
    @DisplayName("toEntity should map all FraudDetectionDto fields")
    void testToEntity_AllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(id)
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .entityId("claim-456")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(Map.of("key", "value"))
                .detectedPatterns(List.of("pattern1"))
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .detectedAt(now)
                .reviewedBy("reviewer")
                .reviewedAt(now.plusHours(1))
                .reviewNotes("Confirmed")
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        FraudDetection entity = mapper.toEntity(dto);

        assertEquals(id, entity.getId());
        assertEquals("tenant-123", entity.getTenantId());
        assertEquals("CLAIM", entity.getEntityType());
        assertEquals("claim-456", entity.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, entity.getRiskLevel());
        assertEquals(0.85, entity.getRiskScore());
        assertEquals("ML_MODEL", entity.getDetectionMethod());
        assertEquals("PENDING", entity.getStatus());
        assertTrue(entity.getRequiresReview());
        assertEquals("admin", entity.getAssignedTo());
    }

    @Test
    @DisplayName("toDtoList should handle single element list")
    void testToDtoList_SingleElement() {
        FraudDetection detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .build();

        List<FraudDetectionDto> dtos = mapper.toDtoList(List.of(detection));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    @DisplayName("toAlertDtoList should handle single element")
    void testToAlertDtoList_SingleElement() {
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .build();

        List<FraudAlertDto> dtos = mapper.toAlertDtoList(List.of(alert));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    @DisplayName("toCaseDtoList should handle single element")
    void testToCaseDtoList_SingleElement() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .build();

        List<FraudCaseDto> dtos = mapper.toCaseDtoList(List.of(fraudCase));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    @DisplayName("toPatternDtoList should handle single element")
    void testToPatternDtoList_SingleElement() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .build();

        List<FraudPatternDto> dtos = mapper.toPatternDtoList(List.of(pattern));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    @DisplayName("toRuleDtoList should handle single element")
    void testToRuleDtoList_SingleElement() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .build();

        List<FraudRuleDto> dtos = mapper.toRuleDtoList(List.of(rule));

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    @DisplayName("updateEntityFromDto should update all fields including null")
    void testUpdateEntityFromDto_PartialUpdate() {
        FraudDetection original = FraudDetection.builder()
                .id(UUID.randomUUID())
                .status("PENDING")
                .riskScore(0.5)
                .assignedTo("original-admin")
                .build();

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .status("REVIEWED")
                .riskScore(null)  // MapStruct updates with null
                .assignedTo(null)
                .build();

        mapper.updateEntityFromDto(dto, original);

        assertEquals("REVIEWED", original.getStatus());
        assertNull(original.getRiskScore());  // MapStruct updates even with null
        assertNull(original.getAssignedTo());
    }

    @Test
    @DisplayName("toEntity should handle null DTO")
    void testToEntity_NullDto() {
        FraudDetection entity = mapper.toEntity((FraudDetectionDto) null);

        assertNull(entity);
    }
}
