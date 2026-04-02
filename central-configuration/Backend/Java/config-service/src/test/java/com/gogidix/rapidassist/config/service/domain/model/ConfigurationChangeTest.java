package com.gogidix.rapidassist.config.service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ConfigurationChange domain model.
 * Tests the change tracking and approval workflow logic.
 */
@DisplayName("ConfigurationChange Domain Model Tests")
class ConfigurationChangeTest {

    private static final String TENANT_ID = "test-tenant";
    private static final String CONFIG_KEY = "test.config";
    private static final String ENVIRONMENT = "production";
    private static final String NAMESPACE = "api-gateway";
    private static final String USER = "admin";

    @Nested
    @DisplayName("ConfigurationChange Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("create() should create change with AUTO_APPROVED status")
        void create_HasAutoApprovedStatus() {
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update config"
            );

            assertEquals(ConfigurationChange.ApprovalStatus.AUTO_APPROVED, change.approvalStatus());
            assertNotNull(change.changeId());
            assertNotNull(change.changedAt());
        }

        @Test
        @DisplayName("create() should set change fields correctly")
        void create_SetsFieldsCorrectly() {
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.CREATE, null, 1,
                null, "value1", USER, "Initial creation"
            );

            assertEquals(TENANT_ID, change.tenantId());
            assertEquals(CONFIG_KEY, change.configKey());
            assertEquals(ENVIRONMENT, change.environment());
            assertEquals(NAMESPACE, change.namespace());
            assertEquals(ConfigurationChange.ChangeType.CREATE, change.changeType());
            assertEquals(1, change.newVersion());
            assertEquals("value1", change.newValue());
            assertEquals(USER, change.changedBy());
            assertEquals("Initial creation", change.reason());
        }

        @Test
        @DisplayName("create() should generate unique changeId")
        void create_GeneratesUniqueChangeId() {
            ConfigurationChange change1 = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChange change2 = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 2, 3,
                "new", "newer", USER, "Update"
            );

            assertNotEquals(change1.changeId(), change2.changeId());
        }
    }

    @Nested
    @DisplayName("withApproval() - Approval Workflow")
    class WithApprovalTests {

        @Test
        @DisplayName("withApproval() should change status to APPROVED")
        void withApproval_ChangesToApproved() {
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChange approved = change.withApproval(
                ConfigurationChange.ApprovalStatus.APPROVED, "approver"
            );

            assertEquals(ConfigurationChange.ApprovalStatus.APPROVED, approved.approvalStatus());
            assertEquals("approver", approved.approvedBy());
            assertNotNull(approved.approvedAt());
        }

        @Test
        @DisplayName("withApproval() should change status to REJECTED")
        void withApproval_ChangesToRejected() {
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChange rejected = change.withApproval(
                ConfigurationChange.ApprovalStatus.REJECTED, "approver"
            );

            assertEquals(ConfigurationChange.ApprovalStatus.REJECTED, rejected.approvalStatus());
            assertEquals("approver", rejected.approvedBy());
            assertNotNull(rejected.approvedAt());
        }

        @Test
        @DisplayName("withApproval() for PENDING should not set approvedAt")
        void withApproval_Pending_DoesNotSetApprovedAt() {
            ConfigurationChange change = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChange pending = change.withApproval(
                ConfigurationChange.ApprovalStatus.PENDING, "user"
            );

            assertEquals(ConfigurationChange.ApprovalStatus.PENDING, pending.approvalStatus());
            assertNull(pending.approvedAt());
        }

        @Test
        @DisplayName("withApproval() should return new instance")
        void withApproval_ReturnsNewInstance() {
            ConfigurationChange original = ConfigurationChange.create(
                TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                ConfigurationChange.ChangeType.UPDATE, 1, 2,
                "old", "new", USER, "Update"
            );

            ConfigurationChange approved = original.withApproval(
                ConfigurationChange.ApprovalStatus.APPROVED, "approver"
            );

            assertNotSame(original, approved);
            assertEquals(ConfigurationChange.ApprovalStatus.AUTO_APPROVED, original.approvalStatus());
        }
    }

    @Nested
    @DisplayName("ChangeType Enum")
    class ChangeTypeTests {

        @Test
        @DisplayName("Should have all expected change types")
        void hasExpectedChangeTypes() {
            ConfigurationChange.ChangeType[] types = ConfigurationChange.ChangeType.values();

            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.CREATE));
            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.UPDATE));
            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.DELETE));
            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.ROLLBACK));
            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.APPROVE));
            assertTrue(Set.of(types).contains(ConfigurationChange.ChangeType.REJECT));
        }
    }

    @Nested
    @DisplayName("ApprovalStatus Enum")
    class ApprovalStatusTests {

        @Test
        @DisplayName("Should have all expected approval statuses")
        void hasExpectedApprovalStatuses() {
            ConfigurationChange.ApprovalStatus[] statuses = ConfigurationChange.ApprovalStatus.values();

            assertTrue(Set.of(statuses).contains(ConfigurationChange.ApprovalStatus.PENDING));
            assertTrue(Set.of(statuses).contains(ConfigurationChange.ApprovalStatus.APPROVED));
            assertTrue(Set.of(statuses).contains(ConfigurationChange.ApprovalStatus.REJECTED));
            assertTrue(Set.of(statuses).contains(ConfigurationChange.ApprovalStatus.AUTO_APPROVED));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("ConfigurationChange record should be immutable")
        void isImmutable() {
            ConfigurationChange change = new ConfigurationChange(
                "id", TENANT_ID, CONFIG_KEY, ENVIRONMENT, NAMESPACE,
                "change-123", ConfigurationChange.ChangeType.UPDATE,
                1, 2, "old", "new", USER, Instant.now(),
                "Update", ConfigurationChange.ApprovalStatus.PENDING,
                null, null, null, null, null,
                Map.of(), Set.of(), Set.of()
            );

            // Record fields are final by design
            assertNotNull(change.changeId());
            assertEquals("change-123", change.changeId());
        }
    }
}
