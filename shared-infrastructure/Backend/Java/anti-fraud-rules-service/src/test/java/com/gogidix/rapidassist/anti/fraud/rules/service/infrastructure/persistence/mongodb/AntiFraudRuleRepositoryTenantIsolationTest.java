package com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL SECURITY TESTS: Tenant Isolation for AntiFraudRuleRepository
 *
 * These tests verify that tenant isolation is enforced at the repository layer.
 * Failure of any test indicates a DATA LEAKAGE VULNERABILITY where one tenant
 * could access another tenant's fraud rules.
 */
@DataMongoTest
class AntiFraudRuleRepositoryTenantIsolationTest {

    @Autowired
    private AntiFraudRuleRepository repository;

    private final String tenant1Id = "tenant-1";
    private final String tenant2Id = "tenant-2";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("CRITICAL: Tenant Isolation - Cross-Tenant Data Access Prevention")
    class CrossTenantDataAccessTests {

        @Test
        @DisplayName("CRITICAL: Should not return rules from other tenants when querying by tenantId")
        void testTenantIsolationInFindByTenantId() {
            // Create rule for tenant-1
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    null,
                    tenant1Id,
                    "Tenant 1 Rule",
                    "Description 1",
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    Map.of("amount", 10000),
                    Map.of("action", "alert"),
                    Instant.now(),
                    Instant.now(),
                    "user1",
                    "user1"
            );
            repository.save(rule1);

            // Create rule for tenant-2
            AntiFraudRuleDocument rule2 = new AntiFraudRuleDocument(
                    null,
                    tenant2Id,
                    "Tenant 2 Rule",
                    "Description 2",
                    AntiFraudRule.RuleType.PATTERN,
                    true,
                    200,
                    Map.of("pattern", "suspicious"),
                    Map.of("action", "block"),
                    Instant.now(),
                    Instant.now(),
                    "user2",
                    "user2"
            );
            repository.save(rule2);

            // Query for tenant-1 - should ONLY return tenant-1 rules
            List<AntiFraudRuleDocument> tenant1Rules = repository.findByTenantId(tenant1Id);
            assertThat(tenant1Rules)
                    .as("Tenant 1 should only see their own rules")
                    .hasSize(1);
            assertThat(tenant1Rules.get(0).tenantId())
                    .as("Returned rule must belong to tenant-1")
                    .isEqualTo(tenant1Id);

            // Query for tenant-2 - should ONLY return tenant-2 rules
            List<AntiFraudRuleDocument> tenant2Rules = repository.findByTenantId(tenant2Id);
            assertThat(tenant2Rules)
                    .as("Tenant 2 should only see their own rules")
                    .hasSize(1);
            assertThat(tenant2Rules.get(0).tenantId())
                    .as("Returned rule must belong to tenant-2")
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: Should enforce tenant filtering in active rules query")
        void testTenantFilteredActiveRules() {
            // Create active rules for both tenants
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    null,
                    tenant1Id,
                    "Active Rule T1",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            AntiFraudRuleDocument rule2 = new AntiFraudRuleDocument(
                    null,
                    tenant2Id,
                    "Active Rule T2",
                    null,
                    AntiFraudRule.RuleType.PATTERN,
                    true,
                    200,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule2);

            // Verify tenant isolation for active rules
            List<AntiFraudRuleDocument> tenant1Active = repository.findByTenantIdAndActiveTrue(tenant1Id);
            assertThat(tenant1Active)
                    .as("Tenant 1 should only see their active rules")
                    .hasSize(1);
            assertThat(tenant1Active.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            List<AntiFraudRuleDocument> tenant2Active = repository.findByTenantIdAndActiveTrue(tenant2Id);
            assertThat(tenant2Active)
                    .as("Tenant 2 should only see their active rules")
                    .hasSize(1);
            assertThat(tenant2Active.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findByIdAndTenantId should only return rule if it belongs to tenant")
        void testFindByIdAndTenantId() {
            // Create rule for tenant-1
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    "rule-id-1",
                    tenant1Id,
                    "Tenant 1 Rule",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            // Tenant-1 should be able to find their rule
            Optional<AntiFraudRuleDocument> foundForT1 = repository.findByIdAndTenantId("rule-id-1", tenant1Id);
            assertThat(foundForT1)
                    .as("Tenant 1 should find their own rule")
                    .isPresent();
            assertThat(foundForT1.get().tenantId())
                    .isEqualTo(tenant1Id);

            // Tenant-2 should NOT be able to find tenant-1's rule
            Optional<AntiFraudRuleDocument> foundForT2 = repository.findByIdAndTenantId("rule-id-1", tenant2Id);
            assertThat(foundForT2)
                    .as("Tenant 2 should NOT find tenant 1's rule")
                    .isEmpty();
        }

        @Test
        @DisplayName("CRITICAL: existsByIdAndTenantId should only return true for rules belonging to tenant")
        void testExistsByIdAndTenantId() {
            // Create rule for tenant-1
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    "rule-id-1",
                    tenant1Id,
                    "Tenant 1 Rule",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            // Tenant-1 should see their rule as existing
            boolean existsForT1 = repository.existsByIdAndTenantId("rule-id-1", tenant1Id);
            assertThat(existsForT1)
                    .as("Tenant 1 should see their rule as existing")
                    .isTrue();

            // Tenant-2 should NOT see tenant-1's rule as existing
            boolean existsForT2 = repository.existsByIdAndTenantId("rule-id-1", tenant2Id);
            assertThat(existsForT2)
                    .as("Tenant 2 should NOT see tenant 1's rule as existing")
                    .isFalse();
        }

        @Test
        @DisplayName("CRITICAL: findByTenantIdAndRuleTypeAndActiveTrue should enforce tenant filtering")
        void testTenantFilteredByTypeAndActive() {
            // Create rules of same type for both tenants
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    null,
                    tenant1Id,
                    "Threshold Rule T1",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            AntiFraudRuleDocument rule2 = new AntiFraudRuleDocument(
                    null,
                    tenant2Id,
                    "Threshold Rule T2",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    200,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule2);

            // Verify tenant isolation by type and active status
            List<AntiFraudRuleDocument> tenant1ThresholdRules =
                    repository.findByTenantIdAndRuleTypeAndActiveTrue(tenant1Id, AntiFraudRule.RuleType.THRESHOLD);
            assertThat(tenant1ThresholdRules)
                    .as("Tenant 1 should only see their threshold rules")
                    .hasSize(1);
            assertThat(tenant1ThresholdRules.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            List<AntiFraudRuleDocument> tenant2ThresholdRules =
                    repository.findByTenantIdAndRuleTypeAndActiveTrue(tenant2Id, AntiFraudRule.RuleType.THRESHOLD);
            assertThat(tenant2ThresholdRules)
                    .as("Tenant 2 should only see their threshold rules")
                    .hasSize(1);
            assertThat(tenant2ThresholdRules.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findActiveByTenantIdOrderByPriorityDesc should enforce tenant filtering")
        void testTenantFilteredActiveRulesByPriority() {
            // Create active rules for both tenants with different priorities
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    null,
                    tenant1Id,
                    "High Priority Rule T1",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    300,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            AntiFraudRuleDocument rule2 = new AntiFraudRuleDocument(
                    null,
                    tenant2Id,
                    "High Priority Rule T2",
                    null,
                    AntiFraudRule.RuleType.PATTERN,
                    true,
                    400,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule2);

            // Verify tenant isolation in priority-ordered query
            List<AntiFraudRuleDocument> tenant1ByPriority =
                    repository.findActiveByTenantIdOrderByPriorityDesc(tenant1Id);
            assertThat(tenant1ByPriority)
                    .as("Tenant 1 should only see their rules ordered by priority")
                    .hasSize(1);
            assertThat(tenant1ByPriority.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            List<AntiFraudRuleDocument> tenant2ByPriority =
                    repository.findActiveByTenantIdOrderByPriorityDesc(tenant2Id);
            assertThat(tenant2ByPriority)
                    .as("Tenant 2 should only see their rules ordered by priority")
                    .hasSize(1);
            assertThat(tenant2ByPriority.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: countByTenantIdAndActiveTrue should only count rules for specific tenant")
        void testCountByTenantIdAndActiveTrue() {
            // Create active rules for both tenants
            AntiFraudRuleDocument rule1 = new AntiFraudRuleDocument(
                    null,
                    tenant1Id,
                    "Active Rule T1",
                    null,
                    AntiFraudRule.RuleType.THRESHOLD,
                    true,
                    100,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule1);

            AntiFraudRuleDocument rule2 = new AntiFraudRuleDocument(
                    null,
                    tenant2Id,
                    "Active Rule T2",
                    null,
                    AntiFraudRule.RuleType.PATTERN,
                    true,
                    200,
                    null,
                    null,
                    Instant.now(),
                    Instant.now(),
                    null,
                    null
            );
            repository.save(rule2);

            // Verify tenant isolation in count
            long tenant1Count = repository.countByTenantIdAndActiveTrue(tenant1Id);
            assertThat(tenant1Count)
                    .as("Tenant 1 should only count their active rules")
                    .isEqualTo(1);

            long tenant2Count = repository.countByTenantIdAndActiveTrue(tenant2Id);
            assertThat(tenant2Count)
                    .as("Tenant 2 should only count their active rules")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: deleteByTenantId should only delete rules for specific tenant")
        void testDeleteByTenantId() {
            // Create rules for both tenants
            repository.save(new AntiFraudRuleDocument(
                    null, tenant1Id, "Rule T1", null,
                    AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null,
                    Instant.now(), Instant.now(), null, null
            ));
            repository.save(new AntiFraudRuleDocument(
                    null, tenant2Id, "Rule T2", null,
                    AntiFraudRule.RuleType.PATTERN, true, 200, null, null,
                    Instant.now(), Instant.now(), null, null
            ));

            // Delete only tenant-1's rules
            repository.deleteByTenantId(tenant1Id);

            // Verify only tenant-1's rules were deleted
            List<AntiFraudRuleDocument> remainingTenant1 = repository.findByTenantId(tenant1Id);
            assertThat(remainingTenant1)
                    .as("Tenant 1's rules should be deleted")
                    .isEmpty();

            List<AntiFraudRuleDocument> remainingTenant2 = repository.findByTenantId(tenant2Id);
            assertThat(remainingTenant2)
                    .as("Tenant 2's rules should NOT be deleted")
                    .hasSize(1);
        }

        @Test
        @DisplayName("CRITICAL: Multiple rules per tenant should be isolated")
        void testMultipleRulesPerTenantIsolation() {
            // Create multiple rules for each tenant
            for (int i = 1; i <= 5; i++) {
                repository.save(new AntiFraudRuleDocument(
                        null,
                        tenant1Id,
                        "Tenant 1 Rule " + i,
                        null,
                        AntiFraudRule.RuleType.THRESHOLD,
                        true,
                        i * 10,
                        null,
                        null,
                        Instant.now(),
                        Instant.now(),
                        null,
                        null
                ));

                repository.save(new AntiFraudRuleDocument(
                        null,
                        tenant2Id,
                        "Tenant 2 Rule " + i,
                        null,
                        AntiFraudRule.RuleType.PATTERN,
                        true,
                        i * 20,
                        null,
                        null,
                        Instant.now(),
                        Instant.now(),
                        null,
                        null
                ));
            }

            // Verify each tenant sees only their rules
            List<AntiFraudRuleDocument> tenant1Rules = repository.findByTenantId(tenant1Id);
            assertThat(tenant1Rules)
                    .as("Tenant 1 should see only their 5 rules")
                    .hasSize(5);
            assertThat(tenant1Rules)
                    .allMatch(rule -> rule.tenantId().equals(tenant1Id),
                            "All rules must belong to tenant-1");

            List<AntiFraudRuleDocument> tenant2Rules = repository.findByTenantId(tenant2Id);
            assertThat(tenant2Rules)
                    .as("Tenant 2 should see only their 5 rules")
                    .hasSize(5);
            assertThat(tenant2Rules)
                    .allMatch(rule -> rule.tenantId().equals(tenant2Id),
                            "All rules must belong to tenant-2");
        }
    }
}
