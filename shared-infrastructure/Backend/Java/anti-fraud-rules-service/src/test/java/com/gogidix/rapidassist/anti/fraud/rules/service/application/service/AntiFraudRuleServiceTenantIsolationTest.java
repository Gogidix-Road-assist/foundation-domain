package com.gogidix.rapidassist.anti.fraud.rules.service.application.service;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import com.gogidix.rapidassist.anti.fraud.rules.service.domain.port.in.AntiFraudRuleService;
import com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb.AntiFraudRuleDocument;
import com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb.AntiFraudRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CRITICAL SECURITY TESTS: Tenant Isolation for AntiFraudRuleService
 *
 * These tests verify that tenant isolation is enforced at the service layer.
 * Failure of any test indicates a DATA LEAKAGE VULNERABILITY.
 */
@SpringBootTest
class AntiFraudRuleServiceTenantIsolationTest {

    @Autowired
    private AntiFraudRuleService ruleService;

    @Autowired
    private AntiFraudRuleRepository repository;

    private final String tenant1Id = "tenant-1";
    private final String tenant2Id = "tenant-2";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("CRITICAL: Service Layer Tenant Isolation")
    class ServiceLayerTenantIsolation {

        @Test
        @DisplayName("CRITICAL: createRule should set tenantId correctly")
        void testCreateRuleSetsTenantId() {
            AntiFraudRuleService.CreateRuleRequest request =
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Test Rule",
                            "Description",
                            AntiFraudRule.RuleType.THRESHOLD,
                            true,
                            100,
                            Map.of("amount", 10000),
                            Map.of("action", "alert"),
                            "test-user"
                    );

            AntiFraudRule rule = ruleService.createRule(tenant1Id, request);

            assertThat(rule.tenantId())
                    .as("Created rule must belong to the specified tenant")
                    .isEqualTo(tenant1Id);
        }

        @Test
        @DisplayName("CRITICAL: findByTenant should only return rules for specified tenant")
        void testFindByTenantIsolation() {
            // Create rules for both tenants
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "Tenant 1 Rule", null, AntiFraudRule.RuleType.THRESHOLD,
                    true, 100, null, null, null));

            ruleService.createRule(tenant2Id, new AntiFraudRuleService.CreateRuleRequest(
                    "Tenant 2 Rule", null, AntiFraudRule.RuleType.PATTERN,
                    true, 200, null, null, null));

            // Query for tenant-1
            var tenant1Rules = ruleService.findByTenant(tenant1Id);
            assertThat(tenant1Rules)
                    .as("Tenant 1 should only see their own rules")
                    .hasSize(1);
            assertThat(tenant1Rules.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            // Query for tenant-2
            var tenant2Rules = ruleService.findByTenant(tenant2Id);
            assertThat(tenant2Rules)
                    .as("Tenant 2 should only see their own rules")
                    .hasSize(1);
            assertThat(tenant2Rules.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: findById should only return rule if it belongs to tenant")
        void testFindByIdTenantIsolation() {
            // Create rule for tenant-1
            AntiFraudRule rule1 = ruleService.createRule(tenant1Id,
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Tenant 1 Rule", null, AntiFraudRule.RuleType.THRESHOLD,
                            true, 100, null, null, null));

            // Tenant-1 should find their rule
            var foundForT1 = ruleService.findById(tenant1Id, rule1.id());
            assertThat(foundForT1)
                    .as("Tenant 1 should find their rule")
                    .isPresent();
            assertThat(foundForT1.get().tenantId())
                    .isEqualTo(tenant1Id);

            // Tenant-2 should NOT find tenant-1's rule
            var foundForT2 = ruleService.findById(tenant2Id, rule1.id());
            assertThat(foundForT2)
                    .as("Tenant 2 should NOT find tenant 1's rule")
                    .isEmpty();
        }

        @Test
        @DisplayName("CRITICAL: updateRule should only update rule if it belongs to tenant")
        void testUpdateRuleTenantIsolation() {
            // Create rule for tenant-1
            AntiFraudRule rule1 = ruleService.createRule(tenant1Id,
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Original Name", null, AntiFraudRule.RuleType.THRESHOLD,
                            true, 100, null, null, null));

            // Tenant-2 should NOT be able to update tenant-1's rule
            assertThatThrownBy(() ->
                    ruleService.updateRule(tenant2Id, rule1.id(),
                            new AntiFraudRuleService.UpdateRuleRequest(
                                    "Updated Name", null, null, true, 100, null, null, null)))
                    .as("Tenant 2 should not be able to update tenant 1's rule")
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not belong to tenant");

            // Verify rule was not updated
            var notUpdated = repository.findById(rule1.id()).get();
            assertThat(notUpdated.name())
                    .as("Rule should not be updated by tenant-2")
                    .isEqualTo("Original Name");

            // Tenant-1 should be able to update their rule
            AntiFraudRule updated = ruleService.updateRule(tenant1Id, rule1.id(),
                    new AntiFraudRuleService.UpdateRuleRequest(
                            "Updated Name", null, null, true, 100, null, null, null));
            assertThat(updated.name())
                    .isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("CRITICAL: deleteRule should only delete rule if it belongs to tenant")
        void testDeleteRuleTenantIsolation() {
            // Create rules for both tenants
            AntiFraudRule rule1 = ruleService.createRule(tenant1Id,
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Tenant 1 Rule", null, AntiFraudRule.RuleType.THRESHOLD,
                            true, 100, null, null, null));

            AntiFraudRule rule2 = ruleService.createRule(tenant2Id,
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Tenant 2 Rule", null, AntiFraudRule.RuleType.PATTERN,
                            true, 200, null, null, null));

            // Tenant-2 should NOT be able to delete tenant-1's rule
            assertThatThrownBy(() -> ruleService.deleteRule(tenant2Id, rule1.id()))
                    .as("Tenant 2 should not be able to delete tenant 1's rule")
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not belong to tenant");

            // Verify tenant-1's rule still exists
            var stillExists = repository.findById(rule1.id());
            assertThat(stillExists)
                    .as("Tenant 1's rule should still exist")
                    .isPresent();

            // Tenant-1 should be able to delete their rule
            ruleService.deleteRule(tenant1Id, rule1.id());
            var deleted = repository.findById(rule1.id());
            assertThat(deleted)
                    .as("Tenant 1's rule should be deleted")
                    .isEmpty();

            // Tenant-2's rule should still exist
            var t2StillExists = repository.findById(rule2.id());
            assertThat(t2StillExists)
                    .as("Tenant 2's rule should still exist")
                    .isPresent();
        }

        @Test
        @DisplayName("CRITICAL: setActive should only work for rules belonging to tenant")
        void testSetActiveTenantIsolation() {
            // Create rule for tenant-1
            AntiFraudRule rule1 = ruleService.createRule(tenant1Id,
                    new AntiFraudRuleService.CreateRuleRequest(
                            "Tenant 1 Rule", null, AntiFraudRule.RuleType.THRESHOLD,
                            true, 100, null, null, null));

            // Tenant-2 should NOT be able to deactivate tenant-1's rule
            assertThatThrownBy(() -> ruleService.setActive(tenant2Id, rule1.id(), false))
                    .as("Tenant 2 should not be able to deactivate tenant 1's rule")
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("does not belong to tenant");

            // Verify rule is still active
            var stillActive = repository.findById(rule1.id()).get();
            assertThat(stillActive.active())
                    .as("Rule should still be active")
                    .isTrue();

            // Tenant-1 should be able to deactivate their rule
            AntiFraudRule deactivated = ruleService.setActive(tenant1Id, rule1.id(), false);
            assertThat(deactivated.active())
                    .isFalse();
        }

        @Test
        @DisplayName("CRITICAL: findActiveByTenant should only return active rules for specified tenant")
        void testFindActiveByTenantIsolation() {
            // Create active and inactive rules for both tenants
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T1 Active", null, AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null, null));
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T1 Inactive", null, AntiFraudRule.RuleType.PATTERN, false, 100, null, null, null));
            ruleService.createRule(tenant2Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T2 Active", null, AntiFraudRule.RuleType.THRESHOLD, true, 200, null, null, null));
            ruleService.createRule(tenant2Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T2 Inactive", null, AntiFraudRule.RuleType.PATTERN, false, 200, null, null, null));

            // Query for active rules
            var tenant1Active = ruleService.findActiveByTenant(tenant1Id);
            assertThat(tenant1Active)
                    .as("Tenant 1 should only see their active rules")
                    .hasSize(1);
            assertThat(tenant1Active.get(0).tenantId())
                    .isEqualTo(tenant1Id);
            assertThat(tenant1Active.get(0).active())
                    .isTrue();

            var tenant2Active = ruleService.findActiveByTenant(tenant2Id);
            assertThat(tenant2Active)
                    .as("Tenant 2 should only see their active rules")
                    .hasSize(1);
            assertThat(tenant2Active.get(0).tenantId())
                    .isEqualTo(tenant2Id);
            assertThat(tenant2Active.get(0).active())
                    .isTrue();
        }

        @Test
        @DisplayName("CRITICAL: findActiveByTenantAndType should enforce tenant filtering")
        void testFindActiveByTenantAndTypeIsolation() {
            // Create threshold rules for both tenants
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T1 Threshold", null, AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null, null));
            ruleService.createRule(tenant2Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T2 Threshold", null, AntiFraudRule.RuleType.THRESHOLD, true, 200, null, null, null));

            // Query for threshold rules
            var tenant1Threshold = ruleService.findActiveByTenantAndType(tenant1Id, AntiFraudRule.RuleType.THRESHOLD);
            assertThat(tenant1Threshold)
                    .as("Tenant 1 should only see their threshold rules")
                    .hasSize(1);
            assertThat(tenant1Threshold.get(0).tenantId())
                    .isEqualTo(tenant1Id);

            var tenant2Threshold = ruleService.findActiveByTenantAndType(tenant2Id, AntiFraudRule.RuleType.THRESHOLD);
            assertThat(tenant2Threshold)
                    .as("Tenant 2 should only see their threshold rules")
                    .hasSize(1);
            assertThat(tenant2Threshold.get(0).tenantId())
                    .isEqualTo(tenant2Id);
        }

        @Test
        @DisplayName("CRITICAL: countActiveByTenant should only count for specified tenant")
        void testCountActiveByTenantIsolation() {
            // Create active rules for both tenants
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T1 Active 1", null, AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null, null));
            ruleService.createRule(tenant1Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T1 Active 2", null, AntiFraudRule.RuleType.PATTERN, true, 100, null, null, null));
            ruleService.createRule(tenant2Id, new AntiFraudRuleService.CreateRuleRequest(
                    "T2 Active", null, AntiFraudRule.RuleType.THRESHOLD, true, 200, null, null, null));

            long tenant1Count = ruleService.countActiveByTenant(tenant1Id);
            assertThat(tenant1Count)
                    .as("Tenant 1 should count only their active rules")
                    .isEqualTo(2);

            long tenant2Count = ruleService.countActiveByTenant(tenant2Id);
            assertThat(tenant2Count)
                    .as("Tenant 2 should count only their active rules")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("CRITICAL: Service should validate tenantId is not blank")
        void testTenantIdValidation() {
            assertThatThrownBy(() ->
                    ruleService.createRule("", new AntiFraudRuleService.CreateRuleRequest(
                            "Test", null, AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null, null)))
                    .as("Should reject blank tenantId")
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tenantId");

            assertThatThrownBy(() ->
                    ruleService.createRule(null, new AntiFraudRuleService.CreateRuleRequest(
                            "Test", null, AntiFraudRule.RuleType.THRESHOLD, true, 100, null, null, null)))
                    .as("Should reject null tenantId")
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tenantId");
        }
    }
}
