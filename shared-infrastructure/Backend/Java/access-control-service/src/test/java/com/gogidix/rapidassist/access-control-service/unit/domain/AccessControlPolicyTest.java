package com.gogidix.rapidassist.access.control.service.unit.domain;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import com.gogidix.rapidassist.access.control.service.domain.aggregate.RoleAggregate;
import com.gogidix.rapidassist.access.control.service.domain.policy.AccessControlPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit Test: AccessControlPolicyTest
 *
 * Tests the AccessControlPolicy business rules.
 */
@DisplayName("Access Control Policy Tests")
class AccessControlPolicyTest {

    private final AccessControlPolicy policy = new AccessControlPolicy();

    @Nested
    @DisplayName("Allow Evaluation")
    class AllowEvaluation {
        @Test
        @DisplayName("Should allow when explicit ALLOW permission exists")
        void shouldAllowWhenExplicitPermission() {
            Permission allowPermission = Permission.builder()
                    .id("perm-1")
                    .tenantId("tenant-1")
                    .subjectId("user-1")
                    .subjectType("USER")
                    .resource("/api/v1/users")
                    .action("READ")
                    .effect("ALLOW")
                    .active(true)
                    .build();

            AccessControlPolicy.AccessDecision decision = policy.evaluate(
                    "user-1", "/api/v1/users", "READ",
                    List.of(allowPermission)
            );

            assertThat(decision.isAllowed()).isTrue();
        }

        @Test
        @DisplayName("Should allow with wildcard resource")
        void shouldAllowWithWildcardResource() {
            Permission wildcardPermission = Permission.builder()
                    .id("perm-1")
                    .tenantId("tenant-1")
                    .subjectId("user-1")
                    .subjectType("USER")
                    .resource("*")
                    .action("*")
                    .effect("ALLOW")
                    .active(true)
                    .build();

            AccessControlPolicy.AccessDecision decision = policy.evaluate(
                    "user-1", "/any/resource", "ANY",
                    List.of(wildcardPermission)
            );

            assertThat(decision.isAllowed()).isTrue();
        }
    }

    @Nested
    @DisplayName("Deny Evaluation")
    class DenyEvaluation {
        @Test
        @DisplayName("Should deny when no matching permissions")
        void shouldDenyWhenNoMatching() {
            AccessControlPolicy.AccessDecision decision = policy.evaluate(
                    "user-1", "/api/v1/users", "READ",
                    List.of()
            );

            assertThat(decision.isAllowed()).isFalse();
            assertThat(decision.getReason()).contains("No matching ALLOW permission");
        }

        @Test
        @DisplayName("Should deny when explicit DENY permission exists")
        void shouldDenyWhenExplicitDeny() {
            Permission denyPermission = Permission.builder()
                    .id("perm-1")
                    .tenantId("tenant-1")
                    .subjectId("user-1")
                    .subjectType("USER")
                    .resource("/api/v1/users")
                    .action("DELETE")
                    .effect("DENY")
                    .active(true)
                    .build();

            AccessControlPolicy.AccessDecision decision = policy.evaluate(
                    "user-1", "/api/v1/users", "DELETE",
                    List.of(denyPermission)
            );

            assertThat(decision.isAllowed()).isFalse();
            assertThat(decision.getReason()).contains("DENY permission");
        }

        @Test
        @DisplayName("Deny should override allow")
        void denyShouldOverrideAllow() {
            Permission allowPermission = Permission.builder()
                    .id("perm-1")
                    .tenantId("tenant-1")
                    .subjectId("user-1")
                    .subjectType("USER")
                    .resource("/api/v1/users")
                    .action("DELETE")
                    .effect("ALLOW")
                    .active(true)
                    .build();

            Permission denyPermission = Permission.builder()
                    .id("perm-2")
                    .tenantId("tenant-1")
                    .subjectId("user-1")
                    .subjectType("USER")
                    .resource("/api/v1/users")
                    .action("DELETE")
                    .effect("DENY")
                    .active(true)
                    .build();

            AccessControlPolicy.AccessDecision decision = policy.evaluate(
                    "user-1", "/api/v1/users", "DELETE",
                    List.of(allowPermission, denyPermission)
            );

            assertThat(decision.isAllowed()).isFalse();
        }
    }

    @Nested
    @DisplayName("Grant Validation")
    class GrantValidation {
        @Test
        @DisplayName("Should reject wildcard for non-admin")
        void shouldRejectWildcardForNonAdmin() {
            List<String> userRoles = List.of("USER");

            AccessControlPolicy.GrantPermissionResult result =
                    policy.validateGrant("tenant-1", "user-1", "*", "*",
                            "user-1", userRoles);

            assertThat(result.isApproved()).isFalse();
        }

        @Test
        @DisplayName("Should approve wildcard for admin")
        void shouldApproveWildcardForAdmin() {
            List<String> adminRoles = List.of("ADMIN");

            AccessControlPolicy.GrantPermissionResult result =
                    policy.validateGrant("tenant-1", "user-1", "*", "*",
                            "admin-1", adminRoles);

            assertThat(result.isApproved()).isTrue();
        }

        @Test
        @DisplayName("Should reject sensitive resource for non-admin")
        void shouldRejectSensitiveResourceForNonAdmin() {
            List<String> userRoles = List.of("USER");

            AccessControlPolicy.GrantPermissionResult result =
                    policy.validateGrant("tenant-1", "user-1",
                            "/api/v1/admin/config", "WRITE", "user-1", userRoles);

            assertThat(result.isApproved()).isFalse();
        }
    }
}
