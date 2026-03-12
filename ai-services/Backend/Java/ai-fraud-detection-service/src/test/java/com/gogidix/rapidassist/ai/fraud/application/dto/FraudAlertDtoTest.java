package com.gogidix.rapidassist.ai.fraud.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudAlertDto
 */
@DisplayName("FraudAlertDto Tests")
class FraudAlertDtoTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder - All fields")
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        UUID detectionId = UUID.randomUUID();
        Map<String, Object> details = Map.of("key", "value");

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .fraudDetectionId(detectionId)
                .alertType("SUSPICIOUS_PATTERN")
                .severity("HIGH")
                .title("Suspicious activity")
                .description("Multiple claims from same IP")
                .alertDetails(details)
                .status("PENDING")
                .assignedTo("admin")
                .acknowledgedAt(now)
                .acknowledgedBy("admin")
                .acknowledgmentNotes("Under review")
                .resolvedAt(now.plusDays(1))
                .resolvedBy("manager")
                .resolutionNotes("Resolved")
                .caseCreated(false)
                .escalationLevel(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals(detectionId, dto.getFraudDetectionId());
        assertEquals("SUSPICIOUS_PATTERN", dto.getAlertType());
        assertEquals("HIGH", dto.getSeverity());
        assertEquals("Suspicious activity", dto.getTitle());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("admin", dto.getAssignedTo());
        assertEquals("admin", dto.getAcknowledgedBy());
        assertEquals("manager", dto.getResolvedBy());
        assertFalse(dto.getCaseCreated());
        assertEquals(0, dto.getEscalationLevel());
    }

    @Test
    @DisplayName("Builder - Minimal fields")
    void testBuilder_MinimalFields() {
        FraudAlertDto dto = FraudAlertDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertNull(dto.getAlertType());
        assertNull(dto.getSeverity());
    }

    @Test
    @DisplayName("Getters and Setters")
    void testGettersSetters() {
        FraudAlertDto dto = new FraudAlertDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setAlertType("ANOMALY_DETECTED");
        dto.setSeverity("CRITICAL");
        dto.setTitle("Critical anomaly");
        dto.setStatus("OPEN");
        dto.setCaseCreated(true);
        dto.setEscalationLevel(2);

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("ANOMALY_DETECTED", dto.getAlertType());
        assertEquals("CRITICAL", dto.getSeverity());
        assertEquals("Critical anomaly", dto.getTitle());
        assertEquals("OPEN", dto.getStatus());
        assertTrue(dto.getCaseCreated());
        assertEquals(2, dto.getEscalationLevel());
    }

    @Test
    @DisplayName("Equals - Same ID")
    void testEquals_SameId() {
        FraudAlertDto dto1 = FraudAlertDto.builder().id(TEST_ID).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(TEST_ID).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Equals - Different ID")
    void testEquals_DifferentId() {
        FraudAlertDto dto1 = FraudAlertDto.builder().id(UUID.randomUUID()).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(UUID.randomUUID()).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Equals - Not null")
    void testEquals_NotNull() {
        FraudAlertDto dto = FraudAlertDto.builder().id(TEST_ID).build();
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("Severity levels")
    void testSeverityLevels() {
        String[] severities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};

        for (String severity : severities) {
            FraudAlertDto dto = FraudAlertDto.builder().severity(severity).build();
            assertEquals(severity, dto.getSeverity());
        }
    }

    @Test
    @DisplayName("Status values")
    void testStatusValues() {
        String[] statuses = {"PENDING", "ACKNOWLEDGED", "RESOLVED", "ESCALATED"};

        for (String status : statuses) {
            FraudAlertDto dto = FraudAlertDto.builder().status(status).build();
            assertEquals(status, dto.getStatus());
        }
    }

    @Test
    @DisplayName("toString - Contains ID")
    void testToString_ContainsId() {
        FraudAlertDto dto = FraudAlertDto.builder().id(TEST_ID).build();
        assertTrue(dto.toString().contains(TEST_ID.toString()));
    }

    // ========== Mutation-Killing Tests: All Getter/Setter Combinations ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        LocalDateTime now = LocalDateTime.now();
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudAlertDto dto = FraudAlertDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Test Title")
                .description("Test Description")
                .alertDetails(details)
                .status("PENDING")
                .assignedTo("admin")
                .acknowledgedAt(now)
                .acknowledgedBy("admin")
                .acknowledgmentNotes("notes")
                .resolvedAt(now.plusHours(1))
                .resolvedBy("resolver")
                .resolutionNotes("resolution")
                .fraudCaseId(caseId)
                .caseCreated(true)
                .escalationLevel(2)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals(detectionId, dto.getFraudDetectionId());
        assertEquals("FRAUD_DETECTED", dto.getAlertType());
        assertEquals("HIGH", dto.getSeverity());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(details, dto.getAlertDetails());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("admin", dto.getAssignedTo());
        assertEquals(now, dto.getAcknowledgedAt());
        assertEquals("admin", dto.getAcknowledgedBy());
        assertEquals("notes", dto.getAcknowledgmentNotes());
        assertEquals(now.plusHours(1), dto.getResolvedAt());
        assertEquals("resolver", dto.getResolvedBy());
        assertEquals("resolution", dto.getResolutionNotes());
        assertEquals(caseId, dto.getFraudCaseId());
        assertTrue(dto.getCaseCreated());
        assertEquals(2, dto.getEscalationLevel());
        assertEquals(metadata, dto.getMetadata());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now.plusHours(1), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("All setters should update fields")
    void testAllSetters_UpdateFields() {
        LocalDateTime now = LocalDateTime.now();
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        Map<String, Object> details = Map.of("key", "value");

        FraudAlertDto dto = new FraudAlertDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setFraudDetectionId(detectionId);
        dto.setAlertType("FRAUD");
        dto.setSeverity("CRITICAL");
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setAlertDetails(details);
        dto.setStatus("OPEN");
        dto.setAssignedTo("user");
        dto.setAcknowledgedAt(now);
        dto.setAcknowledgedBy("user");
        dto.setAcknowledgmentNotes("notes");
        dto.setResolvedAt(now.plusHours(1));
        dto.setResolvedBy("resolver");
        dto.setResolutionNotes("resolved");
        dto.setFraudCaseId(caseId);
        dto.setCaseCreated(true);
        dto.setEscalationLevel(3);

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals(detectionId, dto.getFraudDetectionId());
        assertEquals("FRAUD", dto.getAlertType());
        assertEquals("CRITICAL", dto.getSeverity());
        assertEquals("Title", dto.getTitle());
        assertEquals("OPEN", dto.getStatus());
        assertEquals(3, dto.getEscalationLevel());
    }

    @Test
    @DisplayName("Setters should handle null values")
    void testSetters_NullValues() {
        FraudAlertDto dto = FraudAlertDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .alertType("TYPE")
                .build();

        dto.setFraudDetectionId(null);
        dto.setSeverity(null);
        dto.setAssignedTo(null);
        dto.setAcknowledgedBy(null);
        dto.setResolvedBy(null);
        dto.setFraudCaseId(null);
        dto.setAlertDetails(null);
        dto.setMetadata(null);

        assertNull(dto.getFraudDetectionId());
        assertNull(dto.getSeverity());
        assertNull(dto.getAssignedTo());
        assertNull(dto.getAcknowledgedBy());
        assertNull(dto.getResolvedBy());
        assertNull(dto.getFraudCaseId());
        assertNull(dto.getAlertDetails());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Getters after setters should return correct values")
    void testGettersAfterSetters_CorrectValues() {
        FraudAlertDto dto = new FraudAlertDto();
        dto.setId(TEST_ID);
        dto.setTenantId("modified-tenant");
        dto.setAlertType("MODIFIED_TYPE");
        dto.setSeverity("MEDIUM");
        dto.setTitle("Modified Title");
        dto.setEscalationLevel(5);

        assertEquals(TEST_ID, dto.getId());
        assertEquals("modified-tenant", dto.getTenantId());
        assertEquals("MODIFIED_TYPE", dto.getAlertType());
        assertEquals("MEDIUM", dto.getSeverity());
        assertEquals("Modified Title", dto.getTitle());
        assertEquals(5, dto.getEscalationLevel());
    }

    @Test
    @DisplayName("Boolean field: caseCreated should handle all values")
    void testCaseCreated_AllValues() {
        FraudAlertDto dto1 = FraudAlertDto.builder().caseCreated(true).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().caseCreated(false).build();
        FraudAlertDto dto3 = FraudAlertDto.builder().caseCreated(null).build();

        assertTrue(dto1.getCaseCreated());
        assertFalse(dto2.getCaseCreated());
        assertNull(dto3.getCaseCreated());
    }

    @Test
    @DisplayName("Integer field: escalationLevel should handle all values")
    void testEscalationLevel_AllValues() {
        FraudAlertDto dto1 = FraudAlertDto.builder().escalationLevel(0).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().escalationLevel(5).build();
        FraudAlertDto dto3 = FraudAlertDto.builder().escalationLevel(null).build();

        assertEquals(0, dto1.getEscalationLevel());
        assertEquals(5, dto2.getEscalationLevel());
        assertNull(dto3.getEscalationLevel());
    }

    // ========== Mutation-Killing Tests: Escalation Level Arithmetic ==========

    @Test
    @DisplayName("Escalation level should handle various integer values")
    void testEscalationLevel_VariousIntegers() {
        FraudAlertDto dto1 = FraudAlertDto.builder().escalationLevel(0).build();
        assertEquals(0, dto1.getEscalationLevel());

        FraudAlertDto dto2 = FraudAlertDto.builder().escalationLevel(1).build();
        assertEquals(1, dto2.getEscalationLevel());

        FraudAlertDto dto3 = FraudAlertDto.builder().escalationLevel(2).build();
        assertEquals(2, dto3.getEscalationLevel());

        FraudAlertDto dto4 = FraudAlertDto.builder().escalationLevel(3).build();
        assertEquals(3, dto4.getEscalationLevel());

        FraudAlertDto dto5 = FraudAlertDto.builder().escalationLevel(5).build();
        assertEquals(5, dto5.getEscalationLevel());

        FraudAlertDto dto6 = FraudAlertDto.builder().escalationLevel(10).build();
        assertEquals(10, dto6.getEscalationLevel());

        FraudAlertDto dto7 = FraudAlertDto.builder().escalationLevel(100).build();
        assertEquals(100, dto7.getEscalationLevel());
    }

    @Test
    @DisplayName("Escalation level setter should work correctly")
    void testEscalationLevel_Setter() {
        FraudAlertDto dto = new FraudAlertDto();
        dto.setEscalationLevel(0);
        assertEquals(0, dto.getEscalationLevel());

        dto.setEscalationLevel(5);
        assertEquals(5, dto.getEscalationLevel());

        dto.setEscalationLevel(10);
        assertEquals(10, dto.getEscalationLevel());

        dto.setEscalationLevel(null);
        assertNull(dto.getEscalationLevel());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudAlertDto dto1 = FraudAlertDto.builder().id(id).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudAlertDto dto1 = FraudAlertDto.builder().id(UUID.randomUUID()).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudAlertDto")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlertDto dto1 = FraudAlertDto.builder().id(id1).alertType("ALERT-001").build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(id2).alertType("ALERT-002").build();
        FraudAlertDto dto1Duplicate = FraudAlertDto.builder().id(id1).alertType("ALERT-001-DUP").build();

        java.util.HashSet<FraudAlertDto> set = new java.util.HashSet<>();
        assertTrue(set.add(dto1));
        assertTrue(set.add(dto2));
        assertFalse(set.add(dto1Duplicate)); // Should not add - same ID as dto1

        assertEquals(2, set.size());
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudAlertDto as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlertDto key1 = FraudAlertDto.builder().id(id1).build();
        FraudAlertDto key2 = FraudAlertDto.builder().id(id2).build();
        FraudAlertDto key1Duplicate = FraudAlertDto.builder().id(id1).build();

        java.util.Map<FraudAlertDto, String> map = new java.util.HashMap<>();
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
        FraudAlertDto dto = FraudAlertDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudAlertDto dto = FraudAlertDto.builder().id(TEST_ID).build();
        String other = "Not a FraudAlertDto";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlertDto dto1 = FraudAlertDto.builder()
                .id(id1)
                .alertType("ALERT")
                .severity("HIGH")
                .build();

        FraudAlertDto dto2 = FraudAlertDto.builder()
                .id(id2)
                .alertType("ALERT")
                .severity("HIGH")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudAlertDto dto1 = FraudAlertDto.builder().build();
        FraudAlertDto dto2 = FraudAlertDto.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudAlertDto dto1 = FraudAlertDto.builder().id(id).build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(null).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudAlertDto dto = FraudAlertDto.builder().build();
        FraudAlertDto other = FraudAlertDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudAlertDto dto = FraudAlertDto.builder().build();
        String other = "Not a FraudAlertDto";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudAlertDto dto = FraudAlertDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudAlertDto dto = FraudAlertDto.builder()
                .id(id)
                .alertType("ALERT")
                .build();
        FraudCaseDto otherDto = FraudCaseDto.builder()
                .id(id)
                .caseNumber("FC-123")
                .build();

        // This tests canEqual within equals
        assertNotEquals(dto, otherDto);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlertDto dto1 = FraudAlertDto.builder().id(id1).alertType("ALERT-001").build();
        FraudAlertDto dto2 = FraudAlertDto.builder().id(id2).alertType("ALERT-002").build();

        java.util.HashSet<FraudAlertDto> set = new java.util.HashSet<>();
        set.add(dto1);
        set.add(dto2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));

        // Create duplicate with same ID
        FraudAlertDto dto1Dup = FraudAlertDto.builder().id(id1).alertType("ALERT-001-DUP").build();
        assertTrue(set.contains(dto1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(dto1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(dto1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudAlertDto dto = FraudAlertDto.builder()
                .id(TEST_ID)
                .alertType("ALERT")
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
        UUID id = UUID.randomUUID();
        FraudAlertDto dto1 = FraudAlertDto.builder()
                .id(id)
                .alertType(null)
                .build();

        FraudAlertDto dto2 = FraudAlertDto.builder()
                .id(id)
                .alertType(null)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========== Additional Getter Return Value Tests ==========

    @Test
    @DisplayName("Getters should return exact values set via builder")
    void testGetters_ExactValues() {
        String alertType = "SUSPICIOUS_ACTIVITY";
        String severity = "CRITICAL";
        String title = "Alert Title";
        String status = "ACKNOWLEDGED";
        String assignedTo = "admin-001";

        FraudAlertDto dto = FraudAlertDto.builder()
                .alertType(alertType)
                .severity(severity)
                .title(title)
                .status(status)
                .assignedTo(assignedTo)
                .build();

        // This kills EmptyObjectReturnValsMutator for string getters
        assertEquals(alertType, dto.getAlertType());
        assertEquals(severity, dto.getSeverity());
        assertEquals(title, dto.getTitle());
        assertEquals(status, dto.getStatus());
        assertEquals(assignedTo, dto.getAssignedTo());
    }

    @Test
    @DisplayName("Setter for acknowledgedAt and resolvedAt should work correctly")
    void testAcknowledgedAtResolvedAt_Setters() {
        LocalDateTime now = LocalDateTime.now();
        FraudAlertDto dto = new FraudAlertDto();

        dto.setAcknowledgedAt(now);
        assertEquals(now, dto.getAcknowledgedAt());

        dto.setResolvedAt(now.plusHours(1));
        assertEquals(now.plusHours(1), dto.getResolvedAt());

        dto.setAcknowledgedAt(null);
        assertNull(dto.getAcknowledgedAt());

        dto.setResolvedAt(null);
        assertNull(dto.getResolvedAt());
    }

    @Test
    @DisplayName("Setter for acknowledgedBy and acknowledgmentNotes should work correctly")
    void testAcknowledgedByAcknowledgmentNotes_Setters() {
        FraudAlertDto dto = new FraudAlertDto();

        dto.setAcknowledgedBy("admin-001");
        assertEquals("admin-001", dto.getAcknowledgedBy());

        dto.setAcknowledgmentNotes("Review started");
        assertEquals("Review started", dto.getAcknowledgmentNotes());

        dto.setAcknowledgedBy(null);
        assertNull(dto.getAcknowledgedBy());

        dto.setAcknowledgmentNotes(null);
        assertNull(dto.getAcknowledgmentNotes());
    }

    @Test
    @DisplayName("Setter for resolvedBy and resolutionNotes should work correctly")
    void testResolvedByResolutionNotes_Setters() {
        FraudAlertDto dto = new FraudAlertDto();

        dto.setResolvedBy("resolver-001");
        assertEquals("resolver-001", dto.getResolvedBy());

        dto.setResolutionNotes("Case resolved");
        assertEquals("Case resolved", dto.getResolutionNotes());

        dto.setResolvedBy(null);
        assertNull(dto.getResolvedBy());

        dto.setResolutionNotes(null);
        assertNull(dto.getResolutionNotes());
    }

    @Test
    @DisplayName("Setter for fraudDetectionId and fraudCaseId should work correctly")
    void testFraudDetectionIdFraudCaseId_Setters() {
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        FraudAlertDto dto = new FraudAlertDto();

        dto.setFraudDetectionId(detectionId);
        assertEquals(detectionId, dto.getFraudDetectionId());

        dto.setFraudCaseId(caseId);
        assertEquals(caseId, dto.getFraudCaseId());

        dto.setFraudDetectionId(null);
        assertNull(dto.getFraudDetectionId());

        dto.setFraudCaseId(null);
        assertNull(dto.getFraudCaseId());
    }

    @Test
    @DisplayName("Setter for alertDetails and metadata should work correctly")
    void testAlertDetailsMetadata_Setters() {
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("source", "api");
        FraudAlertDto dto = new FraudAlertDto();

        dto.setAlertDetails(details);
        assertEquals(details, dto.getAlertDetails());

        dto.setMetadata(metadata);
        assertEquals(metadata, dto.getMetadata());

        dto.setAlertDetails(null);
        assertNull(dto.getAlertDetails());

        dto.setMetadata(null);
        assertNull(dto.getMetadata());
    }
}
