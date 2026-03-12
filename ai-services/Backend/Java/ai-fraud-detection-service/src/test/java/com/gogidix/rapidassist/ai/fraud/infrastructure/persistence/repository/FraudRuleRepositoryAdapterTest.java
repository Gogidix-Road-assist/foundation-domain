package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudRuleRepositoryAdapter
 */
@DisplayName("FraudRuleRepositoryAdapter Tests")
class FraudRuleRepositoryAdapterTest {

    private FraudRuleRepositoryAdapter adapter;

    private static final String TENANT_ID = "tenant-123";
    private static final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FraudRuleRepositoryAdapter();
    }

    // ==================== save() tests ====================

    @Test
    @DisplayName("save - Saves rule successfully")
    void testSave_SavesRuleSuccessfully() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("High Amount Threshold")
                .ruleCode("HIGH_AMOUNT_001")
                .ruleType("THRESHOLD")
                .isActive(true)
                .build();

        FraudRule result = adapter.save(TENANT_ID, rule);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals("High Amount Threshold", result.getRuleName());
        assertEquals("HIGH_AMOUNT_001", result.getRuleCode());
    }

    // ==================== findById() tests ====================

    @Test
    @DisplayName("findById - Returns rule when found")
    void testFindById_ReturnsRuleWhenFound() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("Test Rule")
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals("Test Rule", result.get().getRuleName());
    }

    @Test
    @DisplayName("findById - Returns empty when not found")
    void testFindById_ReturnsEmptyWhenNotFound() {
        Optional<FraudRule> result = adapter.findById(TENANT_ID, UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    // ==================== findByRuleCode() tests ====================

    @Test
    @DisplayName("findByRuleCode - Returns rule by code")
    void testFindByRuleCode_ReturnsRuleByCode() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleCode("HIGH_AMOUNT_001")
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findByRuleCode(TENANT_ID, "HIGH_AMOUNT_001");

        assertTrue(result.isPresent());
        assertEquals("HIGH_AMOUNT_001", result.get().getRuleCode());
    }

    @Test
    @DisplayName("findByRuleCode - Returns empty for non-existent code")
    void testFindByRuleCode_ReturnsEmptyForNonExistentCode() {
        Optional<FraudRule> result = adapter.findByRuleCode(TENANT_ID, "NON_EXISTENT");

        assertFalse(result.isPresent());
    }

    // ==================== findByTenantId() tests ====================

    @Test
    @DisplayName("findByTenantId - Returns rules for tenant")
    void testFindByTenantId_ReturnsRules() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("Rule1")
                .build();

        adapter.save(TENANT_ID, rule);

        List<FraudRule> result = adapter.findByTenantId(TENANT_ID);

        assertEquals(1, result.size());
        assertEquals(TENANT_ID, result.get(0).getTenantId());
    }

    // ==================== findByRuleType() tests ====================

    @Test
    @DisplayName("findByRuleType - Returns rules by type")
    void testFindByRuleType_ReturnsRulesByType() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleType("VELOCITY")
                .build();

        adapter.save(TENANT_ID, rule);

        List<FraudRule> result = adapter.findByRuleType(TENANT_ID, "VELOCITY");

        assertEquals(1, result.size());
        assertEquals("VELOCITY", result.get(0).getRuleType());
    }

    @Test
    @DisplayName("findByRuleType - All rule types")
    void testFindByRuleType_AllRuleTypes() {
        String[] ruleTypes = {"THRESHOLD", "VELOCITY", "PATTERN", "ANOMALY", "COMPOSITE"};

        for (String ruleType : ruleTypes) {
            UUID id = UUID.randomUUID();
            FraudRule rule = FraudRule.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .ruleType(ruleType)
                    .build();
            adapter.save(TENANT_ID, rule);

            List<FraudRule> result = adapter.findByRuleType(TENANT_ID, ruleType);
            assertEquals(1, result.size());
            assertEquals(ruleType, result.get(0).getRuleType());
        }
    }

    // ==================== findActiveRules() tests ====================

    @Test
    @DisplayName("findActiveRules - Returns active rules")
    void testFindActiveRules_ReturnsActiveRules() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .isActive(true)
                .build();

        adapter.save(TENANT_ID, rule);

        List<FraudRule> result = adapter.findActiveRules(TENANT_ID);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    @DisplayName("findActiveRules - Does not return inactive rules")
    void testFindActiveRules_DoesNotReturnInactiveRules() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .isActive(false)
                .build();

        adapter.save(TENANT_ID, rule);

        List<FraudRule> result = adapter.findActiveRules(TENANT_ID);

        assertEquals(0, result.size());
    }

    // ==================== delete() tests ====================

    @Test
    @DisplayName("delete - Removes rule")
    void testDelete_RemovesRule() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("Test Rule")
                .build();

        adapter.save(TENANT_ID, rule);
        adapter.delete(TENANT_ID, TEST_ID);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete - No-op for non-existent rule")
    void testDelete_NoOpForNonExistentRule() {
        assertDoesNotThrow(() -> adapter.delete(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== exists() tests ====================

    @Test
    @DisplayName("exists - Returns true when rule exists")
    void testExists_ReturnsTrueWhenExists() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        adapter.save(TENANT_ID, rule);

        assertTrue(adapter.exists(TENANT_ID, TEST_ID));
    }

    @Test
    @DisplayName("exists - Returns false when rule not found")
    void testExists_ReturnsFalseWhenNotFound() {
        assertFalse(adapter.exists(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== Rule details tests ====================

    @Test
    @DisplayName("Rule details - All fields mapped")
    void testRuleDetails_AllFieldsMapped() {
        Map<String, Object> conditions = Map.of("min_amount", 1000);
        Map<String, Object> metadata = Map.of("key", "value");

        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("Complex Rule")
                .ruleCode("COMPLEX_001")
                .ruleType("COMPOSITE")
                .description("Complex fraud detection rule")
                .conditions(conditions)
                .action("REVIEW")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(1000)
                .triggerCount(150)
                .falsePositiveRate(0.15)
                .version("2.0")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals("Complex Rule", result.get().getRuleName());
        assertEquals("COMPLEX_001", result.get().getRuleCode());
        assertEquals("COMPOSITE", result.get().getRuleType());
        assertEquals("REVIEW", result.get().getAction());
        assertEquals(1, result.get().getPriority());
        assertTrue(result.get().getIsActive());
        assertFalse(result.get().getAutoBlock());
        assertTrue(result.get().getRequireReview());
        assertEquals(1000, result.get().getExecutionCount());
        assertEquals(150, result.get().getTriggerCount());
        assertEquals(0.15, result.get().getFalsePositiveRate());
        assertEquals("2.0", result.get().getVersion());
    }

    @Test
    @DisplayName("Rule actions - All action types")
    void testRuleActions_AllActionTypes() {
        String[] actions = {"ALLOW", "BLOCK", "REVIEW", "FLAG", "CHALLENGE"};

        for (String action : actions) {
            UUID id = UUID.randomUUID();
            FraudRule rule = FraudRule.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .action(action)
                    .build();
            adapter.save(TENANT_ID, rule);

            Optional<FraudRule> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(action, result.get().getAction());
        }
    }

    // ==================== Multi-tenant tests ====================

    @Test
    @DisplayName("Multi-tenant - No cross-tenant leakage")
    void testMultitenancy_NoCrossTenantLeakage() {
        String tenant1 = "tenant-1";
        String tenant2 = "tenant-2";

        FraudRule rule1 = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(tenant1)
                .ruleCode("RULE1")
                .build();

        FraudRule rule2 = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenant2)
                .ruleCode("RULE2")
                .build();

        adapter.save(tenant1, rule1);
        adapter.save(tenant2, rule2);

        List<FraudRule> tenant1Results = adapter.findByTenantId(tenant1);
        List<FraudRule> tenant2Results = adapter.findByTenantId(tenant2);

        assertEquals(1, tenant1Results.size());
        assertEquals(1, tenant2Results.size());
    }

    // ==================== Active/Inactive tests ====================

    @Test
    @DisplayName("Active status - Can be toggled")
    void testActiveStatus_CanBeToggled() {
        UUID ruleId = UUID.randomUUID();
        FraudRule rule = FraudRule.builder()
                .id(ruleId)
                .tenantId(TENANT_ID)
                .ruleName("Test Rule")
                .isActive(true)
                .build();

        adapter.save(TENANT_ID, rule);

        // Create updated rule with same ID but inactive status
        FraudRule updated = FraudRule.builder()
                .id(ruleId)
                .tenantId(TENANT_ID)
                .ruleName("Test Rule")
                .isActive(false)
                .build();
        adapter.save(TENANT_ID, updated);

        List<FraudRule> activeResult = adapter.findActiveRules(TENANT_ID);
        assertEquals(0, activeResult.size());
    }

    // ==================== Rule code uniqueness tests ====================

    @Test
    @DisplayName("Rule code - Unique per tenant")
    void testRuleCode_UniquePerTenant() {
        String ruleCode = "THRESHOLD_001";

        FraudRule rule1 = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .ruleCode(ruleCode)
                .ruleName("Rule 1")
                .build();

        FraudRule rule2 = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .ruleCode(ruleCode)
                .ruleName("Rule 2")
                .build();

        adapter.save(TENANT_ID, rule1);
        adapter.save(TENANT_ID, rule2);

        List<FraudRule> result = adapter.findByTenantId(TENANT_ID);
        assertEquals(2, result.size());
    }

    // ==================== Version tests ====================

    @Test
    @DisplayName("Version - Semantic versioning")
    void testVersion_SemanticVersioning() {
        String[] versions = {"1.0.0", "1.1.0", "2.0.0", "2.1.3"};

        for (String version : versions) {
            UUID id = UUID.randomUUID();
            FraudRule rule = FraudRule.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .version(version)
                    .build();
            adapter.save(TENANT_ID, rule);

            Optional<FraudRule> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(version, result.get().getVersion());
        }
    }

    // ==================== Priority tests ====================

    @Test
    @DisplayName("Priority - Various priority levels")
    void testPriority_VariousPriorityLevels() {
        for (int priority = 0; priority <= 100; priority += 10) {
            UUID id = UUID.randomUUID();
            FraudRule rule = FraudRule.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .priority(priority)
                    .build();
            adapter.save(TENANT_ID, rule);

            Optional<FraudRule> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(priority, result.get().getPriority());
        }
    }

    // ==================== Auto block and require review tests ====================

    @Test
    @DisplayName("Auto block and require review - Both flags")
    void testAutoBlockAndRequireReview_BothFlags() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .autoBlock(true)
                .requireReview(true)
                .action("BLOCK")
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertTrue(result.get().getAutoBlock());
        assertTrue(result.get().getRequireReview());
        assertEquals("BLOCK", result.get().getAction());
    }

    // ==================== Execution counts tests ====================

    @Test
    @DisplayName("Execution counts - Tracking metrics")
    void testExecutionCounts_TrackingMetrics() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .executionCount(5000)
                .triggerCount(500)
                .falsePositiveRate(0.10)
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(5000, result.get().getExecutionCount());
        assertEquals(500, result.get().getTriggerCount());
        assertEquals(0.10, result.get().getFalsePositiveRate());
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Edge case - Empty rule name")
    void testEdgeCase_EmptyRuleName() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("")
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals("", result.get().getRuleName());
    }

    @Test
    @DisplayName("Edge case - Empty rule code")
    void testEdgeCase_EmptyRuleCode() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleCode("")
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findByRuleCode(TENANT_ID, "");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Edge case - Zero execution counts")
    void testEdgeCase_ZeroExecutionCounts() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .executionCount(0)
                .triggerCount(0)
                .falsePositiveRate(0.0)
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getExecutionCount());
        assertEquals(0, result.get().getTriggerCount());
        assertEquals(0.0, result.get().getFalsePositiveRate());
    }

    @Test
    @DisplayName("Edge case - Maximum false positive rate")
    void testEdgeCase_MaxFalsePositiveRate() {
        FraudRule rule = FraudRule.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .falsePositiveRate(1.0)
                .build();

        adapter.save(TENANT_ID, rule);

        Optional<FraudRule> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(1.0, result.get().getFalsePositiveRate());
    }
}
