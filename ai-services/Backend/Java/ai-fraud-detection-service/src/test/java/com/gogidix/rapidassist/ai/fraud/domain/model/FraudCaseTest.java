package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudCase Domain Model Tests")
class FraudCaseTest {

    private static final String TEST_TENANT_ID = "tenant-123";
    private static final String TEST_INVESTIGATOR = "investigator-1";

    @Test
    @DisplayName("Builder should create valid FraudCase instance")
    void testBuilder_ValidConstruction() {
        UUID id = UUID.randomUUID();

        FraudCase fraudCase = FraudCase.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-123456789")
                .caseType("INSURANCE_CLAIM")
                .title("Suspicious claim pattern")
                .description("Multiple claims from same IP")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo("investigator-1")
                .linkedAlerts(new java.util.ArrayList<>())
                .linkedDetections(new java.util.ArrayList<>())
                .openedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(fraudCase);
        assertEquals(id, fraudCase.getId());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("FC-123456789", fraudCase.getCaseNumber());
        assertEquals("INSURANCE_CLAIM", fraudCase.getCaseType());
        assertEquals("OPEN", fraudCase.getStatus());
        assertEquals("HIGH", fraudCase.getPriority());
        assertEquals("investigator-1", fraudCase.getAssignedTo());
    }

    @Test
    @DisplayName("open should set status to OPEN and initialize timestamps")
    void testOpen_SetsStatusToOpen() {
        FraudCase fraudCase = FraudCase.builder().build();

        fraudCase.open("INSURANCE_CLAIM", "investigator-1");

        assertEquals("OPEN", fraudCase.getStatus());
        assertEquals("INSURANCE_CLAIM", fraudCase.getCaseType());
        assertEquals("investigator-1", fraudCase.getAssignedTo());
        assertNotNull(fraudCase.getOpenedDate());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("startInvestigation should set status to IN_PROGRESS")
    void testStartInvestigation_SetsStatusToInProgress() {
        FraudCase fraudCase = FraudCase.builder()
                .status("OPEN")
                .build();

        fraudCase.startInvestigation();

        assertEquals("IN_PROGRESS", fraudCase.getStatus());
        assertNotNull(fraudCase.getStartDate());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("assignTo should update assignedTo and assignedDate")
    void testAssignTo_UpdatesAssignedTo() {
        FraudCase fraudCase = FraudCase.builder()
                .assignedTo("original-assignee")
                .build();

        fraudCase.assignTo("new-assignee");

        assertEquals("new-assignee", fraudCase.getAssignedTo());
        assertNotNull(fraudCase.getAssignedDate());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("updateFindings should update investigation fields")
    void testUpdateFindings_UpdatesFields() {
        String summary = "Investigation completed";
        String finding = "Confirmed fraud pattern";
        String decision = "Reject claim";

        FraudCase fraudCase = FraudCase.builder().build();

        fraudCase.updateFindings(summary, finding, decision);

        assertEquals(summary, fraudCase.getInvestigationSummary());
        assertEquals(finding, fraudCase.getFinding());
        assertEquals(decision, fraudCase.getDecision());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("updateFindings with null summary should still update other fields")
    void testUpdateFindings_WithNullValues() {
        FraudCase fraudCase = FraudCase.builder()
                .investigationSummary("old summary")
                .build();

        fraudCase.updateFindings(null, "new finding", "decision");

        assertEquals("new finding", fraudCase.getFinding());
        assertEquals("decision", fraudCase.getDecision());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("close should set status to CLOSED and calculate duration")
    void testClose_SetsStatusToClosed() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        FraudCase fraudCase = FraudCase.builder()
                .status("IN_PROGRESS")
                .startDate(startDate)
                .build();

        fraudCase.close("admin", "Investigation complete", "Fraud confirmed");

        assertEquals("CLOSED", fraudCase.getStatus());
        assertEquals("admin", fraudCase.getClosedBy());
        assertEquals("Investigation complete", fraudCase.getClosureReason());
        assertEquals("Fraud confirmed", fraudCase.getOutcome());
        assertNotNull(fraudCase.getClosedDate());
        assertNotNull(fraudCase.getDurationDays());
        assertTrue(fraudCase.getDurationDays() >= 4 && fraudCase.getDurationDays() <= 6);
    }

    @Test
    @DisplayName("close without startDate should set durationDays to null")
    void testClose_WithoutStartDate() {
        FraudCase fraudCase = FraudCase.builder()
                .status("OPEN")
                .startDate(null)
                .build();

        fraudCase.close("admin", "Closed", "Outcome");

        assertEquals("CLOSED", fraudCase.getStatus());
        assertNull(fraudCase.getDurationDays());
    }

    @Test
    @DisplayName("isOpen should return true for OPEN status")
    void testIsOpen_OpenStatus() {
        FraudCase fraudCase = FraudCase.builder()
                .status("OPEN")
                .build();

        assertTrue(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isOpen should return true for IN_PROGRESS status")
    void testIsOpen_InProgressStatus() {
        FraudCase fraudCase = FraudCase.builder()
                .status("IN_PROGRESS")
                .build();

        assertTrue(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isOpen should return false for CLOSED status")
    void testIsOpen_ClosedStatus() {
        FraudCase fraudCase = FraudCase.builder()
                .status("CLOSED")
                .build();

        assertFalse(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isHighPriority should return true for HIGH priority")
    void testIsHighPriority_HighPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .priority("HIGH")
                .build();

        assertTrue(fraudCase.isHighPriority());
    }

    @Test
    @DisplayName("isHighPriority should return true for CRITICAL priority")
    void testIsHighPriority_CriticalPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .priority("CRITICAL")
                .build();

        assertTrue(fraudCase.isHighPriority());
    }

    @Test
    @DisplayName("isHighPriority should return false for MEDIUM priority")
    void testIsHighPriority_MediumPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .priority("MEDIUM")
                .build();

        assertFalse(fraudCase.isHighPriority());
    }

    @Test
    @DisplayName("getAgeInDays should calculate correct age")
    void testCalculateAgeInDays_OpenCase() {
        LocalDateTime openedDate = LocalDateTime.now().minusDays(7);

        FraudCase fraudCase = FraudCase.builder()
                .openedDate(openedDate)
                .build();

        long age = fraudCase.getAgeInDays();

        assertTrue(age >= 6 && age <= 8, "Age should be approximately 7 days");
    }

    @Test
    @DisplayName("getAgeInDays should return 0 when openedDate is null")
    void testCalculateAgeInDays_NullOpenedDate() {
        FraudCase fraudCase = FraudCase.builder()
                .openedDate(null)
                .build();

        assertEquals(0, fraudCase.getAgeInDays());
    }

    @Test
    @DisplayName("recordAction should update actionTaken and recoveredAmount")
    void testRecordAction_UpdatesActionFields() {
        FraudCase fraudCase = FraudCase.builder().build();

        fraudCase.recordAction("Account blocked", 5000.0);

        assertEquals("Account blocked", fraudCase.getActionTaken());
        assertEquals(5000.0, fraudCase.getRecoveredAmount());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("tenantId field should be present for multi-tenancy")
    void testTenantId_Isolation() {
        FraudCase fraudCase = FraudCase.builder()
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty instance")
    void testNoArgsConstructor() {
        FraudCase fraudCase = new FraudCase();

        assertNotNull(fraudCase);
        assertNull(fraudCase.getId());
        assertNull(fraudCase.getTenantId());
    }

    // ========== Mutation-Killing Tests: Getter Verification ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        List<UUID> alerts = List.of(UUID.randomUUID(), UUID.randomUUID());
        List<UUID> detections = List.of(UUID.randomUUID());

        FraudCase fraudCase = FraudCase.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-999")
                .caseType("INSURANCE_CLAIM")
                .title("Test Title")
                .description("Test Description")
                .status("OPEN")
                .priority("HIGH")
                .assignedTo(TEST_INVESTIGATOR)
                .assignedTeam("Team A")
                .linkedAlerts(alerts)
                .linkedDetections(detections)
                .investigationSummary("Summary")
                .investigationDetails(details)
                .finding("Finding")
                .decision("Decision")
                .actionTaken("Action")
                .outcome("Outcome")
                .estimatedLoss(1000.0)
                .recoveredAmount(500.0)
                .openedDate(now)
                .assignedDate(now)
                .startDate(now)
                .closedDate(now)
                .closedBy("admin")
                .closureReason("Reason")
                .durationDays(10)
                .createdBy("creator")
                .updatedBy("updater")
                .metadata(details)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(id, fraudCase.getId());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("FC-999", fraudCase.getCaseNumber());
        assertEquals("INSURANCE_CLAIM", fraudCase.getCaseType());
        assertEquals("Test Title", fraudCase.getTitle());
        assertEquals("Test Description", fraudCase.getDescription());
        assertEquals("OPEN", fraudCase.getStatus());
        assertEquals("HIGH", fraudCase.getPriority());
        assertEquals(TEST_INVESTIGATOR, fraudCase.getAssignedTo());
        assertEquals("Team A", fraudCase.getAssignedTeam());
        assertEquals(alerts, fraudCase.getLinkedAlerts());
        assertEquals(detections, fraudCase.getLinkedDetections());
        assertEquals("Summary", fraudCase.getInvestigationSummary());
        assertEquals(details, fraudCase.getInvestigationDetails());
        assertEquals("Finding", fraudCase.getFinding());
        assertEquals("Decision", fraudCase.getDecision());
        assertEquals("Action", fraudCase.getActionTaken());
        assertEquals("Outcome", fraudCase.getOutcome());
        assertEquals(1000.0, fraudCase.getEstimatedLoss());
        assertEquals(500.0, fraudCase.getRecoveredAmount());
        assertEquals(now, fraudCase.getOpenedDate());
        assertEquals(now, fraudCase.getAssignedDate());
        assertEquals(now, fraudCase.getStartDate());
        assertEquals(now, fraudCase.getClosedDate());
        assertEquals("admin", fraudCase.getClosedBy());
        assertEquals("Reason", fraudCase.getClosureReason());
        assertEquals(10, fraudCase.getDurationDays());
        assertEquals("creator", fraudCase.getCreatedBy());
        assertEquals("updater", fraudCase.getUpdatedBy());
        assertEquals(details, fraudCase.getMetadata());
        assertEquals(now, fraudCase.getCreatedAt());
        assertEquals(now, fraudCase.getUpdatedAt());
    }

    // ========== Mutation-Killing Tests: Business Logic Edge Cases ==========

    @Test
    @DisplayName("isOpen should return false for null status")
    void testIsOpen_NullStatus() {
        FraudCase fraudCase = FraudCase.builder()
                .status(null)
                .build();

        assertFalse(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isOpen should return false for CLOSED status")
    void testIsClosed_Status() {
        FraudCase fraudCase = FraudCase.builder()
                .status("CLOSED")
                .build();

        assertFalse(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isOpen should return false for RESOLVED status")
    void testIsResolved_Status() {
        FraudCase fraudCase = FraudCase.builder()
                .status("RESOLVED")
                .build();

        assertFalse(fraudCase.isOpen());
    }

    @Test
    @DisplayName("isHighPriority should return false for null priority")
    void testIsHighPriority_NullPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .priority(null)
                .build();

        assertFalse(fraudCase.isHighPriority());
    }

    @Test
    @DisplayName("isHighPriority should return false for LOW priority")
    void testIsHighPriority_LowPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .priority("LOW")
                .build();

        assertFalse(fraudCase.isHighPriority());
    }

    @Test
    @DisplayName("open should preserve existing id and tenantId")
    void testOpen_PreservesExistingFields() {
        UUID id = UUID.randomUUID();

        FraudCase fraudCase = FraudCase.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-123")
                .build();

        fraudCase.open("CLAIM_FRAUD", "investigator-2");

        assertEquals(id, fraudCase.getId());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("FC-123", fraudCase.getCaseNumber());
        assertEquals("CLAIM_FRAUD", fraudCase.getCaseType());
        assertEquals("investigator-2", fraudCase.getAssignedTo());
    }

    @Test
    @DisplayName("startInvestigation should preserve existing fields")
    void testStartInvestigation_PreservesExistingFields() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-456")
                .status("OPEN")
                .assignedTo(TEST_INVESTIGATOR)
                .build();

        fraudCase.startInvestigation();

        assertEquals("FC-456", fraudCase.getCaseNumber());
        assertEquals(TEST_INVESTIGATOR, fraudCase.getAssignedTo());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("IN_PROGRESS", fraudCase.getStatus());
        assertNotNull(fraudCase.getStartDate());
    }

    @Test
    @DisplayName("assignTo should preserve other fields")
    void testAssignTo_PreservesOtherFields() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-789")
                .status("OPEN")
                .assignedTo("original-assignee")
                .build();

        fraudCase.assignTo("new-assignee");

        assertEquals("FC-789", fraudCase.getCaseNumber());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("new-assignee", fraudCase.getAssignedTo());
        assertNotNull(fraudCase.getAssignedDate());
    }

    @Test
    @DisplayName("updateFindings should preserve existing fields")
    void testUpdateFindings_PreservesExistingFields() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-101")
                .status("IN_PROGRESS")
                .assignedTo(TEST_INVESTIGATOR)
                .build();

        fraudCase.updateFindings("New Summary", "New Finding", "New Decision");

        assertEquals("FC-101", fraudCase.getCaseNumber());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("IN_PROGRESS", fraudCase.getStatus());
        assertEquals(TEST_INVESTIGATOR, fraudCase.getAssignedTo());
        assertEquals("New Summary", fraudCase.getInvestigationSummary());
        assertEquals("New Finding", fraudCase.getFinding());
        assertEquals("New Decision", fraudCase.getDecision());
    }

    @Test
    @DisplayName("close should preserve existing fields")
    void testClose_PreservesExistingFields() {
        LocalDateTime startDate = LocalDateTime.now();
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-202")
                .caseType("INSURANCE_CLAIM")
                .assignedTo(TEST_INVESTIGATOR)
                .status("IN_PROGRESS")
                .startDate(startDate)
                .build();

        fraudCase.close("admin", "Case resolved", "Fraud confirmed");

        assertEquals("FC-202", fraudCase.getCaseNumber());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("INSURANCE_CLAIM", fraudCase.getCaseType());
        assertEquals(TEST_INVESTIGATOR, fraudCase.getAssignedTo());
        assertEquals("CLOSED", fraudCase.getStatus());
        assertEquals("admin", fraudCase.getClosedBy());
        assertEquals("Case resolved", fraudCase.getClosureReason());
        assertEquals("Fraud confirmed", fraudCase.getOutcome());
    }

    @Test
    @DisplayName("recordAction should preserve existing fields")
    void testRecordAction_PreservesExistingFields() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .caseNumber("FC-303")
                .status("OPEN")
                .assignedTo(TEST_INVESTIGATOR)
                .recoveredAmount(100.0)
                .build();

        fraudCase.recordAction("Block account", 500.0);

        assertEquals("FC-303", fraudCase.getCaseNumber());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("OPEN", fraudCase.getStatus());
        assertEquals(TEST_INVESTIGATOR, fraudCase.getAssignedTo());
        assertEquals("Block account", fraudCase.getActionTaken());
        assertEquals(500.0, fraudCase.getRecoveredAmount());
    }

    @Test
    @DisplayName("close with null startDate should not set durationDays")
    void testClose_NullStartDate_NoDuration() {
        FraudCase fraudCase = FraudCase.builder()
                .status("OPEN")
                .startDate(null)
                .build();

        fraudCase.close("admin", "Closed", "Outcome");

        assertNull(fraudCase.getDurationDays());
    }

    @Test
    @DisplayName("AllArgsConstructor should create complete instance")
    void testAllFieldsViaConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        List<UUID> alerts = List.of(UUID.randomUUID());

        FraudCase fraudCase = new FraudCase(
                id, TEST_TENANT_ID, "FC-111", "TYPE", "Title", "Desc",
                "OPEN", "HIGH", TEST_INVESTIGATOR, "Team", alerts, alerts,
                "Summary", details, "Finding", "Decision", "Action",
                "Outcome", 1000.0, 500.0, now, now, now, now,
                "admin", "Reason", 10, "creator", "updater", details, now, now
        );

        assertEquals(id, fraudCase.getId());
        assertEquals(TEST_TENANT_ID, fraudCase.getTenantId());
        assertEquals("FC-111", fraudCase.getCaseNumber());
        assertEquals("TYPE", fraudCase.getCaseType());
        assertEquals("Title", fraudCase.getTitle());
        assertEquals("OPEN", fraudCase.getStatus());
        assertEquals("HIGH", fraudCase.getPriority());
    }

    @Test
    @DisplayName("updateFindings with null values should update non-null fields")
    void testUpdateFindings_NullUpdatesNonNull() {
        FraudCase fraudCase = FraudCase.builder()
                .investigationSummary("old summary")
                .finding("old finding")
                .decision("old decision")
                .build();

        fraudCase.updateFindings(null, null, null);

        assertNull(fraudCase.getInvestigationSummary());
        assertNull(fraudCase.getFinding());
        assertNull(fraudCase.getDecision());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("close should set closedDate even if other fields are null")
    void testClose_SetsClosedDate() {
        FraudCase fraudCase = FraudCase.builder()
                .status("IN_PROGRESS")
                .closedBy(null)
                .closureReason(null)
                .outcome(null)
                .build();

        fraudCase.close("admin", "Reason", "Outcome");

        assertNotNull(fraudCase.getClosedDate());
    }

    @Test
    @DisplayName("assignTo should set updatedAt timestamp")
    void testAssignTo_SetsUpdatedAt() {
        FraudCase fraudCase = FraudCase.builder()
                .updatedAt(null)
                .build();

        fraudCase.assignTo("investigator");

        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("open should set both openedDate and updatedAt")
    void testOpen_SetsTimestamps() {
        FraudCase fraudCase = FraudCase.builder()
                .openedDate(null)
                .updatedAt(null)
                .build();

        fraudCase.open("TYPE", "investigator");

        assertNotNull(fraudCase.getOpenedDate());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    @Test
    @DisplayName("startInvestigation should set startDate and updatedAt")
    void testStartInvestigation_SetsTimestamps() {
        FraudCase fraudCase = FraudCase.builder()
                .startDate(null)
                .updatedAt(null)
                .build();

        fraudCase.startInvestigation();

        assertNotNull(fraudCase.getStartDate());
        assertNotNull(fraudCase.getUpdatedAt());
    }

    // ========== Mutation-Killing Tests: Duration Calculation ==========

    @Test
    @DisplayName("close should calculate durationDays correctly for various periods")
    void testClose_DurationCalculation() {
        // 1 day investigation
        FraudCase case1 = FraudCase.builder()
                .startDate(LocalDateTime.now().minusDays(1))
                .build();
        case1.close("admin", "done", "resolved");
        assertEquals(1, case1.getDurationDays());

        // 5 day investigation
        FraudCase case2 = FraudCase.builder()
                .startDate(LocalDateTime.now().minusDays(5))
                .build();
        case2.close("admin", "done", "resolved");
        assertEquals(5, case2.getDurationDays());

        // 30 day investigation
        FraudCase case3 = FraudCase.builder()
                .startDate(LocalDateTime.now().minusDays(30))
                .build();
        case3.close("admin", "done", "resolved");
        assertEquals(30, case3.getDurationDays());

        // 90 day investigation
        FraudCase case4 = FraudCase.builder()
                .startDate(LocalDateTime.now().minusDays(90))
                .build();
        case4.close("admin", "done", "resolved");
        assertEquals(90, case4.getDurationDays());
    }

    @Test
    @DisplayName("close should not set durationDays when startDate is null")
    void testClose_NoDurationWhenStartDateNull() {
        FraudCase fraudCase = FraudCase.builder()
                .startDate(null)
                .build();

        fraudCase.close("admin", "done", "resolved");

        assertNull(fraudCase.getDurationDays());
    }

    @Test
    @DisplayName("close should set durationDays to 0 when closed immediately after start")
    void testClose_ZeroDuration() {
        // Close almost immediately after start (same day)
        LocalDateTime justNow = LocalDateTime.now().minusMinutes(5);
        FraudCase fraudCase = FraudCase.builder()
                .startDate(justNow)
                .build();

        fraudCase.close("admin", "done", "resolved");

        assertEquals(0, fraudCase.getDurationDays());
    }

    // ========== Mutation-Killing Tests: Age Calculation ==========

    @Test
    @DisplayName("getAgeInDays should calculate correctly for various ages")
    void testGetAgeInDays_VariousAges() {
        // 1 day old
        FraudCase case1 = FraudCase.builder()
                .openedDate(LocalDateTime.now().minusDays(1))
                .build();
        assertEquals(1, case1.getAgeInDays());

        // 7 days old
        FraudCase case2 = FraudCase.builder()
                .openedDate(LocalDateTime.now().minusDays(7))
                .build();
        assertEquals(7, case2.getAgeInDays());

        // 30 days old
        FraudCase case3 = FraudCase.builder()
                .openedDate(LocalDateTime.now().minusDays(30))
                .build();
        assertEquals(30, case3.getAgeInDays());

        // 365 days old
        FraudCase case4 = FraudCase.builder()
                .openedDate(LocalDateTime.now().minusDays(365))
                .build();
        assertEquals(365, case4.getAgeInDays());
    }

    @Test
    @DisplayName("getAgeInDays should return 0 when openedDate is null")
    void testGetAgeInDays_NullOpenedDate() {
        FraudCase fraudCase = FraudCase.builder()
                .openedDate(null)
                .build();

        assertEquals(0, fraudCase.getAgeInDays());
    }

    @Test
    @DisplayName("getAgeInDays should return 0 for newly opened case")
    void testGetAgeInDays_NewlyOpened() {
        LocalDateTime justNow = LocalDateTime.now().minusMinutes(10);
        FraudCase fraudCase = FraudCase.builder()
                .openedDate(justNow)
                .build();

        assertEquals(0, fraudCase.getAgeInDays());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudCase case1 = FraudCase.builder().id(id).build();
        FraudCase case2 = FraudCase.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(case1.hashCode(), case2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudCase case1 = FraudCase.builder().id(UUID.randomUUID()).build();
        FraudCase case2 = FraudCase.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(case1.hashCode(), case2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudCase")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase case1 = FraudCase.builder().id(id1).caseNumber("FC-001").build();
        FraudCase case2 = FraudCase.builder().id(id2).caseNumber("FC-002").build();
        FraudCase case1Duplicate = FraudCase.builder().id(id1).caseNumber("FC-001-DUP").build();

        java.util.HashSet<FraudCase> set = new java.util.HashSet<>();
        assertTrue(set.add(case1));
        assertTrue(set.add(case2));
        assertFalse(set.add(case1Duplicate)); // Should not add - same ID as case1

        assertEquals(2, set.size());
        assertTrue(set.contains(case1));
        assertTrue(set.contains(case2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudCase as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase key1 = FraudCase.builder().id(id1).build();
        FraudCase key2 = FraudCase.builder().id(id2).build();
        FraudCase key1Duplicate = FraudCase.builder().id(id1).build();

        java.util.Map<FraudCase, String> map = new java.util.HashMap<>();
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
        FraudCase fraudCase = FraudCase.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(fraudCase, fraudCase);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudCase fraudCase = FraudCase.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, fraudCase);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudCase fraudCase = FraudCase.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudCase";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(fraudCase, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        LocalDateTime now = LocalDateTime.now();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase case1 = FraudCase.builder()
                .id(id1)
                .caseNumber("FC-123")
                .status("OPEN")
                .build();

        FraudCase case2 = FraudCase.builder()
                .id(id2)
                .caseNumber("FC-123")
                .status("OPEN")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(case1, case2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudCase case1 = FraudCase.builder().build();
        FraudCase case2 = FraudCase.builder().build();

        assertEquals(case1, case2);
        assertEquals(case1.hashCode(), case2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudCase case1 = FraudCase.builder().id(id).build();
        FraudCase case2 = FraudCase.builder().id(null).build();

        assertNotEquals(case1, case2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudCase fraudCase = FraudCase.builder().build();
        FraudCase other = FraudCase.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(fraudCase.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudCase fraudCase = FraudCase.builder().build();
        String other = "Not a FraudCase";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(fraudCase.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudCase fraudCase = FraudCase.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(fraudCase.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudCase fraudCase = FraudCase.builder().id(id).build();
        FraudAlert otherAlert = FraudAlert.builder().id(id).alertType("ALERT").build();

        // This tests canEqual within equals
        assertNotEquals(fraudCase, otherAlert);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase case1 = FraudCase.builder().id(id1).caseNumber("FC-001").build();
        FraudCase case2 = FraudCase.builder().id(id2).caseNumber("FC-002").build();

        java.util.HashSet<FraudCase> set = new java.util.HashSet<>();
        set.add(case1);
        set.add(case2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(case1));
        assertTrue(set.contains(case2));

        // Create duplicate with same ID
        FraudCase case1Dup = FraudCase.builder().id(id1).caseNumber("FC-001-DUP").build();
        assertTrue(set.contains(case1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(case1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(case1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudCase fraudCase = FraudCase.builder()
                .id(UUID.randomUUID())
                .caseNumber("FC-123")
                .build();

        int hash1 = fraudCase.hashCode();
        int hash2 = fraudCase.hashCode();
        int hash3 = fraudCase.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudCase case1 = FraudCase.builder()
                .id(id)
                .caseNumber(null)
                .status(null)
                .build();

        FraudCase case2 = FraudCase.builder()
                .id(id)
                .caseNumber(null)
                .status(null)
                .build();

        assertEquals(case1.hashCode(), case2.hashCode());
    }
}
