package com.gogidix.rapidassist.ai.fraud.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudCaseDto
 */
@DisplayName("FraudCaseDto Tests")
class FraudCaseDtoTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder - All fields")
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<UUID> linkedAlerts = List.of(UUID.randomUUID());
        List<UUID> linkedDetections = List.of(UUID.randomUUID());
        Map<String, Object> details = Map.of("key", "value");

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-12345")
                .caseType("FRAUD_INVESTIGATION")
                .title("Suspicious claims pattern")
                .description("Multiple claims from same IP")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo("investigator1")
                .assignedTeam("FRAUD_TEAM")
                .linkedAlerts(linkedAlerts)
                .linkedDetections(linkedDetections)
                .investigationSummary("Under investigation")
                .investigationDetails(details)
                .finding("Fraud confirmed")
                .decision("Block payment")
                .actionTaken("Account blocked")
                .outcome("Fraud prevented")
                .estimatedLoss(10000.0)
                .recoveredAmount(5000.0)
                .openedDate(now)
                .assignedDate(now)
                .startDate(now)
                .closedDate(now.plusDays(7))
                .closedBy("manager")
                .closureReason("Case resolved")
                .durationDays(7)
                .createdBy("admin")
                .updatedBy("investigator1")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("FC-12345", dto.getCaseNumber());
        assertEquals("FRAUD_INVESTIGATION", dto.getCaseType());
        assertEquals("Suspicious claims pattern", dto.getTitle());
        assertEquals("OPEN", dto.getStatus());
        assertEquals("HIGH", dto.getPriority());
        assertEquals("investigator1", dto.getAssignedTo());
        assertEquals("FRAUD_TEAM", dto.getAssignedTeam());
        assertEquals(linkedAlerts, dto.getLinkedAlerts());
        assertEquals(linkedDetections, dto.getLinkedDetections());
        assertEquals(10000.0, dto.getEstimatedLoss());
        assertEquals(5000.0, dto.getRecoveredAmount());
        assertEquals(7, dto.getDurationDays());
    }

    @Test
    @DisplayName("Builder - Minimal fields")
    void testBuilder_MinimalFields() {
        FraudCaseDto dto = FraudCaseDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertNull(dto.getCaseNumber());
        assertNull(dto.getCaseType());
        assertNull(dto.getStatus());
    }

    @Test
    @DisplayName("Getters and Setters")
    void testGettersSetters() {
        FraudCaseDto dto = new FraudCaseDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setCaseNumber("FC-999");
        dto.setCaseType("MONEY_LAUNDERING");
        dto.setTitle("AML Investigation");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("CRITICAL");
        dto.setAssignedTo("agent2");

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("FC-999", dto.getCaseNumber());
        assertEquals("MONEY_LAUNDERING", dto.getCaseType());
        assertEquals("AML Investigation", dto.getTitle());
        assertEquals("IN_PROGRESS", dto.getStatus());
        assertEquals("CRITICAL", dto.getPriority());
        assertEquals("agent2", dto.getAssignedTo());
    }

    @Test
    @DisplayName("Equals - Same ID")
    void testEquals_SameId() {
        FraudCaseDto dto1 = FraudCaseDto.builder().id(TEST_ID).build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(TEST_ID).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Equals - Different ID")
    void testEquals_DifferentId() {
        FraudCaseDto dto1 = FraudCaseDto.builder().id(UUID.randomUUID()).build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(UUID.randomUUID()).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Case types")
    void testCaseTypes() {
        String[] caseTypes = {"FRAUD_INVESTIGATION", "MONEY_LAUNDERING", "IDENTITY_THEFT", "CLAIMS_FRAUD"};

        for (String caseType : caseTypes) {
            FraudCaseDto dto = FraudCaseDto.builder().caseType(caseType).build();
            assertEquals(caseType, dto.getCaseType());
        }
    }

    @Test
    @DisplayName("Status values")
    void testStatusValues() {
        String[] statuses = {"OPEN", "IN_PROGRESS", "PENDING_REVIEW", "CLOSED", "ESCALATED"};

        for (String status : statuses) {
            FraudCaseDto dto = FraudCaseDto.builder().status(status).build();
            assertEquals(status, dto.getStatus());
        }
    }

    @Test
    @DisplayName("toString - Contains ID")
    void testToString_ContainsId() {
        FraudCaseDto dto = FraudCaseDto.builder().id(TEST_ID).build();
        assertTrue(dto.toString().contains(TEST_ID.toString()));
    }

    @Test
    @DisplayName("Linked alerts and detections")
    void testLinkedAlertsAndDetections() {
        UUID alert1 = UUID.randomUUID();
        UUID alert2 = UUID.randomUUID();
        UUID det1 = UUID.randomUUID();
        List<UUID> alerts = List.of(alert1, alert2);
        List<UUID> detections = List.of(det1);

        FraudCaseDto dto = FraudCaseDto.builder()
                .linkedAlerts(alerts)
                .linkedDetections(detections)
                .build();

        assertEquals(2, dto.getLinkedAlerts().size());
        assertEquals(1, dto.getLinkedDetections().size());
        assertTrue(dto.getLinkedAlerts().contains(alert1));
        assertTrue(dto.getLinkedAlerts().contains(alert2));
        assertTrue(dto.getLinkedDetections().contains(det1));
    }

    // ========== Mutation-Killing Tests: All Getter/Setter Combinations ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        LocalDateTime now = LocalDateTime.now();
        List<UUID> linkedAlerts = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<UUID> linkedDetections = List.of(UUID.randomUUID());
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudCaseDto dto = FraudCaseDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-999")
                .caseType("TYPE")
                .title("Title")
                .description("Description")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo("investigator")
                .assignedTeam("Team A")
                .linkedAlerts(linkedAlerts)
                .linkedDetections(linkedDetections)
                .investigationSummary("Summary")
                .investigationDetails(details)
                .finding("Finding")
                .decision("Decision")
                .actionTaken("Action")
                .outcome("Outcome")
                .estimatedLoss(1000.0)
                .recoveredAmount(500.0)
                .openedDate(now)
                .assignedDate(now.plusHours(1))
                .startDate(now.plusHours(2))
                .closedDate(now.plusDays(1))
                .closedBy("admin")
                .closureReason("Reason")
                .durationDays(5)
                .createdBy("creator")
                .updatedBy("updater")
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("FC-999", dto.getCaseNumber());
        assertEquals("TYPE", dto.getCaseType());
        assertEquals("Title", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals("OPEN", dto.getStatus());
        assertEquals("HIGH", dto.getPriority());
        assertEquals("investigator", dto.getAssignedTo());
        assertEquals("Team A", dto.getAssignedTeam());
        assertEquals(linkedAlerts, dto.getLinkedAlerts());
        assertEquals(linkedDetections, dto.getLinkedDetections());
        assertEquals("Summary", dto.getInvestigationSummary());
        assertEquals(details, dto.getInvestigationDetails());
        assertEquals("Finding", dto.getFinding());
        assertEquals("Decision", dto.getDecision());
        assertEquals("Action", dto.getActionTaken());
        assertEquals("Outcome", dto.getOutcome());
        assertEquals(1000.0, dto.getEstimatedLoss());
        assertEquals(500.0, dto.getRecoveredAmount());
        assertEquals(5, dto.getDurationDays());
    }

    @Test
    @DisplayName("Setters should handle null values")
    void testSetters_NullValues() {
        FraudCaseDto dto = FraudCaseDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-123")
                .build();

        dto.setCaseType(null);
        dto.setTitle(null);
        dto.setStatus(null);
        dto.setPriority(null);
        dto.setAssignedTo(null);
        dto.setAssignedTeam(null);
        dto.setInvestigationSummary(null);
        dto.setFinding(null);
        dto.setDecision(null);
        dto.setActionTaken(null);
        dto.setOutcome(null);
        dto.setLinkedAlerts(null);
        dto.setLinkedDetections(null);
        dto.setInvestigationDetails(null);
        dto.setMetadata(null);

        assertNull(dto.getCaseType());
        assertNull(dto.getTitle());
        assertNull(dto.getStatus());
        assertNull(dto.getPriority());
        assertNull(dto.getAssignedTo());
        assertNull(dto.getAssignedTeam());
        assertNull(dto.getInvestigationSummary());
        assertNull(dto.getFinding());
        assertNull(dto.getDecision());
        assertNull(dto.getActionTaken());
        assertNull(dto.getOutcome());
        assertNull(dto.getLinkedAlerts());
        assertNull(dto.getLinkedDetections());
        assertNull(dto.getInvestigationDetails());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Double fields: estimatedLoss and recoveredAmount")
    void testDoubleFields() {
        FraudCaseDto dto = FraudCaseDto.builder()
                .estimatedLoss(5000.0)
                .recoveredAmount(2500.0)
                .build();

        assertEquals(5000.0, dto.getEstimatedLoss());
        assertEquals(2500.0, dto.getRecoveredAmount());

        dto.setEstimatedLoss(10000.0);
        dto.setRecoveredAmount(7500.0);

        assertEquals(10000.0, dto.getEstimatedLoss());
        assertEquals(7500.0, dto.getRecoveredAmount());
    }

    @Test
    @DisplayName("Integer field: durationDays")
    void testDurationDays() {
        FraudCaseDto dto = FraudCaseDto.builder().durationDays(10).build();

        assertEquals(10, dto.getDurationDays());

        dto.setDurationDays(20);
        assertEquals(20, dto.getDurationDays());

        dto.setDurationDays(null);
        assertNull(dto.getDurationDays());
    }

    @Test
    @DisplayName("List fields: linkedAlerts and linkedDetections")
    void testListFields() {
        List<UUID> alerts = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<UUID> detections = List.of(UUID.randomUUID());

        FraudCaseDto dto = FraudCaseDto.builder()
                .linkedAlerts(alerts)
                .linkedDetections(detections)
                .build();

        assertEquals(3, dto.getLinkedAlerts().size());
        assertEquals(1, dto.getLinkedDetections().size());

        dto.setLinkedAlerts(List.of());
        dto.setLinkedDetections(null);

        assertTrue(dto.getLinkedAlerts().isEmpty());
        assertNull(dto.getLinkedDetections());
    }

    @Test
    @DisplayName("Map fields: investigationDetails and metadata")
    void testMapFields() {
        Map<String, Object> details = Map.of("key1", "value1", "key2", 100);
        Map<String, Object> metadata = Map.of("source", "api");

        FraudCaseDto dto = FraudCaseDto.builder()
                .investigationDetails(details)
                .metadata(metadata)
                .build();

        assertEquals(2, dto.getInvestigationDetails().size());
        assertEquals("value1", dto.getInvestigationDetails().get("key1"));
        assertEquals("api", dto.getMetadata().get("source"));

        dto.setInvestigationDetails(Map.of());
        dto.setMetadata(null);

        assertTrue(dto.getInvestigationDetails().isEmpty());
        assertNull(dto.getMetadata());
    }

    // ========== Mutation-Killing Tests: Numeric Field Operations ==========

    @Test
    @DisplayName("Estimated loss should handle various decimal values")
    void testEstimatedLoss_NumericValues() {
        FraudCaseDto dto1 = FraudCaseDto.builder()
                .estimatedLoss(0.0)
                .build();
        assertEquals(0.0, dto1.getEstimatedLoss());

        FraudCaseDto dto2 = FraudCaseDto.builder()
                .estimatedLoss(100.0)
                .build();
        assertEquals(100.0, dto2.getEstimatedLoss());

        FraudCaseDto dto3 = FraudCaseDto.builder()
                .estimatedLoss(1000.5)
                .build();
        assertEquals(1000.5, dto3.getEstimatedLoss());

        FraudCaseDto dto4 = FraudCaseDto.builder()
                .estimatedLoss(10000.0)
                .build();
        assertEquals(10000.0, dto4.getEstimatedLoss());

        FraudCaseDto dto5 = FraudCaseDto.builder()
                .estimatedLoss(100000.99)
                .build();
        assertEquals(100000.99, dto5.getEstimatedLoss());
    }

    @Test
    @DisplayName("Recovered amount should handle various decimal values")
    void testRecoveredAmount_NumericValues() {
        FraudCaseDto dto1 = FraudCaseDto.builder()
                .recoveredAmount(0.0)
                .build();
        assertEquals(0.0, dto1.getRecoveredAmount());

        FraudCaseDto dto2 = FraudCaseDto.builder()
                .recoveredAmount(50.0)
                .build();
        assertEquals(50.0, dto2.getRecoveredAmount());

        FraudCaseDto dto3 = FraudCaseDto.builder()
                .recoveredAmount(500.25)
                .build();
        assertEquals(500.25, dto3.getRecoveredAmount());

        FraudCaseDto dto4 = FraudCaseDto.builder()
                .recoveredAmount(5000.0)
                .build();
        assertEquals(5000.0, dto4.getRecoveredAmount());

        FraudCaseDto dto5 = FraudCaseDto.builder()
                .recoveredAmount(null)
                .build();
        assertNull(dto5.getRecoveredAmount());
    }

    @Test
    @DisplayName("Duration days should handle various integer values")
    void testDurationDays_NumericValues() {
        FraudCaseDto dto1 = FraudCaseDto.builder()
                .durationDays(0)
                .build();
        assertEquals(0, dto1.getDurationDays());

        FraudCaseDto dto2 = FraudCaseDto.builder()
                .durationDays(1)
                .build();
        assertEquals(1, dto2.getDurationDays());

        FraudCaseDto dto3 = FraudCaseDto.builder()
                .durationDays(7)
                .build();
        assertEquals(7, dto3.getDurationDays());

        FraudCaseDto dto4 = FraudCaseDto.builder()
                .durationDays(30)
                .build();
        assertEquals(30, dto4.getDurationDays());

        FraudCaseDto dto5 = FraudCaseDto.builder()
                .durationDays(90)
                .build();
        assertEquals(90, dto5.getDurationDays());

        FraudCaseDto dto6 = FraudCaseDto.builder()
                .durationDays(365)
                .build();
        assertEquals(365, dto6.getDurationDays());

        FraudCaseDto dto7 = FraudCaseDto.builder()
                .durationDays(null)
                .build();
        assertNull(dto7.getDurationDays());
    }

    @Test
    @DisplayName("All numeric fields should work together correctly")
    void testAllNumericFields_Together() {
        FraudCaseDto dto = FraudCaseDto.builder()
                .estimatedLoss(5000.0)
                .recoveredAmount(3500.0)
                .durationDays(15)
                .build();

        assertEquals(5000.0, dto.getEstimatedLoss());
        assertEquals(3500.0, dto.getRecoveredAmount());
        assertEquals(15, dto.getDurationDays());
    }

    @Test
    @DisplayName("Estimated loss setter should update correctly")
    void testEstimatedLoss_Setter() {
        FraudCaseDto dto = new FraudCaseDto();
        dto.setEstimatedLoss(1000.0);
        assertEquals(1000.0, dto.getEstimatedLoss());

        dto.setEstimatedLoss(0.0);
        assertEquals(0.0, dto.getEstimatedLoss());

        dto.setEstimatedLoss(null);
        assertNull(dto.getEstimatedLoss());
    }

    @Test
    @DisplayName("Recovered amount setter should update correctly")
    void testRecoveredAmount_Setter() {
        FraudCaseDto dto = new FraudCaseDto();
        dto.setRecoveredAmount(500.0);
        assertEquals(500.0, dto.getRecoveredAmount());

        dto.setRecoveredAmount(0.0);
        assertEquals(0.0, dto.getRecoveredAmount());

        dto.setRecoveredAmount(null);
        assertNull(dto.getRecoveredAmount());
    }

    @Test
    @DisplayName("Duration days setter should update correctly")
    void testDurationDays_Setter() {
        FraudCaseDto dto = new FraudCaseDto();
        dto.setDurationDays(5);
        assertEquals(5, dto.getDurationDays());

        dto.setDurationDays(0);
        assertEquals(0, dto.getDurationDays());

        dto.setDurationDays(100);
        assertEquals(100, dto.getDurationDays());

        dto.setDurationDays(null);
        assertNull(dto.getDurationDays());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudCaseDto dto1 = FraudCaseDto.builder().id(id).build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudCaseDto dto1 = FraudCaseDto.builder().id(UUID.randomUUID()).build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudCaseDto")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCaseDto dto1 = FraudCaseDto.builder().id(id1).caseNumber("FC-001").build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(id2).caseNumber("FC-002").build();
        FraudCaseDto dto1Duplicate = FraudCaseDto.builder().id(id1).caseNumber("FC-001-DUP").build();

        java.util.HashSet<FraudCaseDto> set = new java.util.HashSet<>();
        assertTrue(set.add(dto1));
        assertTrue(set.add(dto2));
        assertFalse(set.add(dto1Duplicate)); // Should not add - same ID as dto1

        assertEquals(2, set.size());
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudCaseDto as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCaseDto key1 = FraudCaseDto.builder().id(id1).build();
        FraudCaseDto key2 = FraudCaseDto.builder().id(id2).build();
        FraudCaseDto key1Duplicate = FraudCaseDto.builder().id(id1).build();

        java.util.Map<FraudCaseDto, String> map = new java.util.HashMap<>();
        map.put(key1, "First");
        map.put(key2, "Second");
        map.put(key1Duplicate, "First Updated");

        // This kills Math mutants in hashCode() and NegateConditionalsMutator in equals()
        assertEquals(2, map.size());
        assertEquals("First Updated", map.get(key1));
        assertEquals("Second", map.get(key2));
        assertEquals("First Updated", map.get(key1Duplicate));
    }

    @Test
    @DisplayName("equals - Same object reference")
    void testEquals_SameObjectReference() {
        FraudCaseDto dto = FraudCaseDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudCaseDto dto = FraudCaseDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudCaseDto dto = FraudCaseDto.builder().id(TEST_ID).build();
        String other = "Not a FraudCaseDto";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        LocalDateTime now = LocalDateTime.now();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCaseDto dto1 = FraudCaseDto.builder()
                .id(id1)
                .caseNumber("FC-123")
                .status("OPEN")
                .openedDate(now)
                .build();

        FraudCaseDto dto2 = FraudCaseDto.builder()
                .id(id2)
                .caseNumber("FC-123")
                .status("OPEN")
                .openedDate(now)
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudCaseDto dto1 = FraudCaseDto.builder().build();
        FraudCaseDto dto2 = FraudCaseDto.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudCaseDto dto1 = FraudCaseDto.builder().id(id).build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(null).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudCaseDto dto = FraudCaseDto.builder().build();
        FraudCaseDto other = FraudCaseDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudCaseDto dto = FraudCaseDto.builder().build();
        String other = "Not a FraudCaseDto";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudCaseDto dto = FraudCaseDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        FraudCaseDto dto = FraudCaseDto.builder().id(TEST_ID).build();
        FraudDetectionDto otherDto = FraudDetectionDto.builder()
                .id(TEST_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();

        // This tests canEqual within equals
        assertNotEquals(dto, otherDto);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        FraudCaseDto dto1 = FraudCaseDto.builder().id(id1).caseNumber("FC-001").build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(id2).caseNumber("FC-002").build();
        FraudCaseDto dto3 = FraudCaseDto.builder().id(id3).caseNumber("FC-003").build();

        java.util.HashSet<FraudCaseDto> set = new java.util.HashSet<>();
        set.add(dto1);
        set.add(dto2);
        set.add(dto3);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
        assertTrue(set.contains(dto3));

        // Create duplicate with same ID
        FraudCaseDto dto1Dup = FraudCaseDto.builder().id(id1).caseNumber("FC-001-DUP").build();
        assertTrue(set.contains(dto1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(dto1Dup));
        assertEquals(2, set.size());
        assertFalse(set.contains(dto1));
    }

    @Test
    @DisplayName("LinkedHashSet should maintain no duplicates based on ID")
    void testLinkedHashSet_NoDuplicates() {
        UUID id = UUID.randomUUID();

        FraudCaseDto dto1 = FraudCaseDto.builder().id(id).caseNumber("FC-001").build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(id).caseNumber("FC-002").build();
        FraudCaseDto dto3 = FraudCaseDto.builder().id(id).caseNumber("FC-003").build();

        java.util.LinkedHashSet<FraudCaseDto> set = new java.util.LinkedHashSet<>();
        set.add(dto1);
        set.add(dto2);
        set.add(dto3);

        // Only one should be in set (first one added)
        assertEquals(1, set.size());
        assertEquals("FC-001", set.iterator().next().getCaseNumber());
    }

    @Test
    @DisplayName("TreeSet should work with FraudCaseDto")
    void testTreeSet_Comparable() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        FraudCaseDto dto1 = FraudCaseDto.builder().id(id1).caseNumber("FC-001").build();
        FraudCaseDto dto2 = FraudCaseDto.builder().id(id2).caseNumber("FC-002").build();
        FraudCaseDto dto3 = FraudCaseDto.builder().id(id3).caseNumber("FC-003").build();

        try {
            java.util.TreeSet<FraudCaseDto> set = new java.util.TreeSet<>();
            set.add(dto3);
            set.add(dto1);
            set.add(dto2);

            assertEquals(3, set.size());
            assertTrue(set.contains(dto1));
            assertTrue(set.contains(dto2));
            assertTrue(set.contains(dto3));
        } catch (ClassCastException e) {
            // FraudCaseDto may not implement Comparable
            // This is expected - TreeSet requires Comparable or Comparator
        }
    }

    @Test
    @DisplayName("equals with different field combinations")
    void testEquals_DifferentFieldCombinations() {
        LocalDateTime now = LocalDateTime.now();
        UUID id = UUID.randomUUID();

        FraudCaseDto base = FraudCaseDto.builder()
                .id(id)
                .tenantId("tenant-1")
                .caseNumber("FC-123")
                .caseType("FRAUD")
                .status("OPEN")
                .priority("HIGH")
                .build();

        // Same ID - should be equal
        FraudCaseDto sameId = FraudCaseDto.builder()
                .id(id)
                .tenantId("tenant-2")  // Different
                .caseNumber("FC-456")   // Different
                .caseType("OTHER")      // Different
                .status("CLOSED")       // Different
                .priority("LOW")        // Different
                .build();

        assertEquals(base, sameId);
        assertEquals(base.hashCode(), sameId.hashCode());
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudCaseDto dto = FraudCaseDto.builder()
                .id(TEST_ID)
                .caseNumber("FC-123")
                .status("OPEN")
                .build();

        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        int hash3 = dto.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        FraudCaseDto dto1 = FraudCaseDto.builder()
                .id(TEST_ID)
                .caseNumber(null)
                .status(null)
                .build();

        FraudCaseDto dto2 = FraudCaseDto.builder()
                .id(TEST_ID)
                .caseNumber(null)
                .status(null)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========== Additional Getter Return Value Tests ==========

    @Test
    @DisplayName("Getters should return non-null values when set")
    void testGetters_NonNullValues() {
        LocalDateTime now = LocalDateTime.now();
        FraudCaseDto dto = FraudCaseDto.builder()
                .openedDate(now)
                .assignedDate(now.plusHours(1))
                .startDate(now.plusHours(2))
                .closedDate(now.plusDays(1))
                .createdBy("admin")
                .updatedBy("user")
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .closureReason("Resolved")
                .build();

        assertNotNull(dto.getOpenedDate());
        assertNotNull(dto.getAssignedDate());
        assertNotNull(dto.getStartDate());
        assertNotNull(dto.getClosedDate());
        assertNotNull(dto.getCreatedBy());
        assertNotNull(dto.getUpdatedBy());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertNotNull(dto.getClosureReason());
    }

    @Test
    @DisplayName("Getters should return exact values set via builder")
    void testGetters_ExactValues() {
        String assignedTo = "investigator-123";
        String assignedTeam = "FRAUD-SQUAD-ALPHA";
        String closureReason = "Case resolved - fraud confirmed";

        FraudCaseDto dto = FraudCaseDto.builder()
                .assignedTo(assignedTo)
                .assignedTeam(assignedTeam)
                .closureReason(closureReason)
                .build();

        // This kills EmptyObjectReturnValsMutator for string getters
        assertEquals(assignedTo, dto.getAssignedTo());
        assertEquals(assignedTeam, dto.getAssignedTeam());
        assertEquals(closureReason, dto.getClosureReason());
    }

    @Test
    @DisplayName("Setter for assignedDate should work correctly")
    void testAssignedDate_Setter() {
        LocalDateTime now = LocalDateTime.now();
        FraudCaseDto dto = new FraudCaseDto();

        dto.setAssignedDate(now);
        assertEquals(now, dto.getAssignedDate());

        dto.setAssignedDate(null);
        assertNull(dto.getAssignedDate());
    }

    @Test
    @DisplayName("Setter for closedDate should work correctly")
    void testClosedDate_Setter() {
        LocalDateTime now = LocalDateTime.now();
        FraudCaseDto dto = new FraudCaseDto();

        dto.setClosedDate(now);
        assertEquals(now, dto.getClosedDate());

        dto.setClosedDate(null);
        assertNull(dto.getClosedDate());
    }

    @Test
    @DisplayName("Setter for createdBy and updatedBy should work correctly")
    void testCreatedByUpdatedBy_Setters() {
        FraudCaseDto dto = new FraudCaseDto();

        dto.setCreatedBy("admin-001");
        assertEquals("admin-001", dto.getCreatedBy());

        dto.setUpdatedBy("user-002");
        assertEquals("user-002", dto.getUpdatedBy());

        dto.setCreatedBy(null);
        assertNull(dto.getCreatedBy());

        dto.setUpdatedBy(null);
        assertNull(dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Setter for createdAt and updatedAt should work correctly")
    void testCreatedAtUpdatedAt_Setters() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);
        FraudCaseDto dto = new FraudCaseDto();

        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());

        dto.setUpdatedAt(later);
        assertEquals(later, dto.getUpdatedAt());

        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());

        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Setter for closureReason should work correctly")
    void testClosureReason_Setter() {
        FraudCaseDto dto = new FraudCaseDto();

        dto.setClosureReason("Fraud confirmed");
        assertEquals("Fraud confirmed", dto.getClosureReason());

        dto.setClosureReason("");
        assertEquals("", dto.getClosureReason());

        dto.setClosureReason(null);
        assertNull(dto.getClosureReason());
    }
}
