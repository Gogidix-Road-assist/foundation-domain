package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudCaseRepositoryAdapter
 */
@DisplayName("FraudCaseRepositoryAdapter Tests")
class FraudCaseRepositoryAdapterTest {

    private FraudCaseRepositoryAdapter adapter;

    private static final String TENANT_ID = "tenant-123";
    private static final String TENANT_ID_2 = "tenant-456";
    private static final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FraudCaseRepositoryAdapter();
    }

    // ==================== save() tests ====================

    @Test
    @DisplayName("save - Saves case successfully")
    void testSave_SavesCaseSuccessfully() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-12345")
                .caseType("FRAUD_INVESTIGATION")
                .status("OPEN")
                .build();

        FraudCase result = adapter.save(TENANT_ID, fraudCase);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals("FC-12345", result.getCaseNumber());
    }

    // ==================== findById() tests ====================

    @Test
    @DisplayName("findById - Returns case when found")
    void testFindById_ReturnsCaseWhenFound() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-12345")
                .build();

        adapter.save(TENANT_ID, fraudCase);

        Optional<FraudCase> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals("FC-12345", result.get().getCaseNumber());
    }

    @Test
    @DisplayName("findById - Returns empty when not found")
    void testFindById_ReturnsEmptyWhenNotFound() {
        Optional<FraudCase> result = adapter.findById(TENANT_ID, UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    // ==================== findByCaseNumber() tests ====================

    @Test
    @DisplayName("findByCaseNumber - Returns case by case number")
    void testFindByCaseNumber_ReturnsCaseByNumber() {
        String caseNumber = "FC-99999";
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber(caseNumber)
                .build();

        adapter.save(TENANT_ID, fraudCase);

        Optional<FraudCase> result = adapter.findByCaseNumber(TENANT_ID, caseNumber);

        assertTrue(result.isPresent());
        assertEquals(caseNumber, result.get().getCaseNumber());
    }

    @Test
    @DisplayName("findByCaseNumber - Returns empty for non-existent case number")
    void testFindByCaseNumber_ReturnsEmptyForNonExistent() {
        Optional<FraudCase> result = adapter.findByCaseNumber(TENANT_ID, "FC-NONEXISTENT");

        assertFalse(result.isPresent());
    }

    // ==================== findByTenantId() tests ====================

    @Test
    @DisplayName("findByTenantId - Returns cases for tenant")
    void testFindByTenantId_ReturnsCases() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase case1 = FraudCase.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .caseNumber("FC-001")
                .build();

        FraudCase case2 = FraudCase.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .caseNumber("FC-002")
                .build();

        adapter.save(TENANT_ID, case1);
        adapter.save(TENANT_ID, case2);

        List<FraudCase> result = adapter.findByTenantId(TENANT_ID);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findByTenantId - Filters by tenant")
    void testFindByTenantId_FiltersByTenant() {
        FraudCase case1 = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .caseNumber("FC-001")
                .build();

        FraudCase case2 = FraudCase.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID_2)
                .caseNumber("FC-002")
                .build();

        adapter.save(TENANT_ID, case1);
        adapter.save(TENANT_ID_2, case2);

        List<FraudCase> result1 = adapter.findByTenantId(TENANT_ID);
        List<FraudCase> result2 = adapter.findByTenantId(TENANT_ID_2);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(TENANT_ID, result1.get(0).getTenantId());
        assertEquals(TENANT_ID_2, result2.get(0).getTenantId());
    }

    // ==================== findByStatus() tests ====================

    @Test
    @DisplayName("findByStatus - Returns cases with matching status")
    void testFindByStatus_ReturnsCasesWithStatus() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudCase case1 = FraudCase.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .status("OPEN")
                .build();

        FraudCase case2 = FraudCase.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .status("CLOSED")
                .build();

        adapter.save(TENANT_ID, case1);
        adapter.save(TENANT_ID, case2);

        List<FraudCase> result = adapter.findByStatus(TENANT_ID, "OPEN");

        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getStatus());
    }

    // ==================== findByAssignedTo() tests ====================

    @Test
    @DisplayName("findByAssignedTo - Returns cases assigned to user")
    void testFindByAssignedTo_ReturnsAssignedCases() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .assignedTo("admin")
                .build();

        adapter.save(TENANT_ID, fraudCase);

        List<FraudCase> result = adapter.findByAssignedTo(TENANT_ID, "admin");

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getAssignedTo());
    }

    // ==================== findByPriority() tests ====================

    @Test
    @DisplayName("findByPriority - Returns cases with priority")
    void testFindByPriority_ReturnCasesWithPriority() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .priority("HIGH")
                .build();

        adapter.save(TENANT_ID, fraudCase);

        List<FraudCase> result = adapter.findByPriority(TENANT_ID, "HIGH");

        assertEquals(1, result.size());
        assertEquals("HIGH", result.get(0).getPriority());
    }

    @Test
    @DisplayName("findByPriority - All priority levels")
    void testFindByPriority_AllPriorityLevels() {
        String[] priorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL", "URGENT"};

        for (String priority : priorities) {
            UUID id = UUID.randomUUID();
            FraudCase fraudCase = FraudCase.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .priority(priority)
                    .build();
            adapter.save(TENANT_ID, fraudCase);

            List<FraudCase> result = adapter.findByPriority(TENANT_ID, priority);
            assertEquals(priority, result.get(0).getPriority());
        }
    }

    // ==================== findOpenCases() tests ====================

    @Test
    @DisplayName("findOpenCases - Returns open cases")
    void testFindOpenCases_ReturnsOpenCases() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .status("OPEN")
                .openedDate(LocalDateTime.now())
                .build();

        adapter.save(TENANT_ID, fraudCase);

        List<FraudCase> result = adapter.findOpenCases(TENANT_ID);

        assertEquals(1, result.size());
        assertEquals("OPEN", result.get(0).getStatus());
    }

    @Test
    @DisplayName("findOpenCases - Does not return closed cases")
    void testFindOpenCases_DoesNotReturnClosedCases() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .status("CLOSED")
                .openedDate(LocalDateTime.now())
                .closedDate(LocalDateTime.now())
                .build();

        adapter.save(TENANT_ID, fraudCase);

        List<FraudCase> result = adapter.findOpenCases(TENANT_ID);

        assertEquals(0, result.size());
    }

    // ==================== delete() tests ====================

    @Test
    @DisplayName("delete - Removes case")
    void testDelete_RemovesCase() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("FC-123")
                .build();

        adapter.save(TENANT_ID, fraudCase);
        adapter.delete(TENANT_ID, TEST_ID);

        Optional<FraudCase> result = adapter.findById(TENANT_ID, TEST_ID);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete - No-op for non-existent case")
    void testDelete_NoOpForNonExistentCase() {
        assertDoesNotThrow(() -> adapter.delete(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== exists() tests ====================

    @Test
    @DisplayName("exists - Returns true when case exists")
    void testExists_ReturnsTrueWhenExists() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        adapter.save(TENANT_ID, fraudCase);

        assertTrue(adapter.exists(TENANT_ID, TEST_ID));
    }

    @Test
    @DisplayName("exists - Returns false when case not found")
    void testExists_ReturnsFalseWhenNotFound() {
        assertFalse(adapter.exists(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== Case types tests ====================

    @Test
    @DisplayName("Case types - Various case types")
    void testCaseTypes_VariousCaseTypes() {
        String[] caseTypes = {
                "FRAUD_INVESTIGATION",
                "MONEY_LAUNDERING",
                "IDENTITY_THEFT",
                "CLAIMS_FRAUD",
                "TRANSACTION_FRAUD"
        };

        for (String caseType : caseTypes) {
            UUID id = UUID.randomUUID();
            FraudCase fraudCase = FraudCase.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .caseType(caseType)
                    .build();
            adapter.save(TENANT_ID, fraudCase);

            Optional<FraudCase> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(caseType, result.get().getCaseType());
        }
    }

    // ==================== Status values tests ====================

    @Test
    @DisplayName("Status values - All valid statuses")
    void testStatusValues_AllValidStatuses() {
        String[] statuses = {"OPEN", "IN_PROGRESS", "PENDING_REVIEW", "CLOSED", "ESCALATED"};

        for (String status : statuses) {
            UUID id = UUID.randomUUID();
            FraudCase fraudCase = FraudCase.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .status(status)
                    .build();
            adapter.save(TENANT_ID, fraudCase);

            List<FraudCase> result = adapter.findByStatus(TENANT_ID, status);
            assertEquals(status, result.get(0).getStatus());
        }
    }

    // ==================== Timestamp tests ====================

    @Test
    @DisplayName("Timestamps - Opened date is set")
    void testTimestamps_OpenedDateIsSet() {
        LocalDateTime now = LocalDateTime.now();
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .openedDate(now)
                .build();

        adapter.save(TENANT_ID, fraudCase);

        Optional<FraudCase> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(now, result.get().getOpenedDate());
    }

    @Test
    @DisplayName("Timestamps - Closed date is set")
    void testTimestamps_ClosedDateIsSet() {
        LocalDateTime opened = LocalDateTime.now();
        LocalDateTime closed = opened.plusDays(7);

        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .openedDate(opened)
                .closedDate(closed)
                .status("CLOSED")
                .build();

        adapter.save(TENANT_ID, fraudCase);

        Optional<FraudCase> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(closed, result.get().getClosedDate());
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Edge case - Empty case number")
    void testEdgeCase_EmptyCaseNumber() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .caseNumber("")
                .build();

        adapter.save(TENANT_ID, fraudCase);

        Optional<FraudCase> result = adapter.findByCaseNumber(TENANT_ID, "");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Edge case - Null assignedTo")
    void testEdgeCase_NullAssignedTo() {
        FraudCase fraudCase = FraudCase.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .assignedTo(null)
                .build();

        adapter.save(TENANT_ID, fraudCase);

        List<FraudCase> result = adapter.findByAssignedTo(TENANT_ID, null);
        assertTrue(result.isEmpty()); // null won't match any non-null assignedTo
    }
}
