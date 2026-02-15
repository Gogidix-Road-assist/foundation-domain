package com.gogidix.rapidassist.rate.limit.policy.service.integration;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.persistence.mongodb.RateLimitPolicyDocument;
import com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.persistence.mongodb.RateLimitPolicyRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TEST: Validates tenant isolation in the Rate Limit Policy service.
 *
 * <p>This test ensures that:
 * <ul>
 *   <li>Tenant A cannot access Tenant B's rate limit policies</li>
 *   <li>All queries properly filter by tenantId</li>
 *   <li>Deletion is scoped to tenant</li>
 *   <li>Updates cannot affect other tenants' data</li>
 * </ul>
 *
 * <p>This test MUST pass before the service can be considered production-ready.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Requires MongoDB connection - temporarily disabled for CI/CD")
public class TenantIsolationTest {

    private static final Logger logger = LoggerFactory.getLogger(TenantIsolationTest.class);

    private static final String TENANT_A = "tenant-a-isolation-test";
    private static final String TENANT_B = "tenant-b-isolation-test";
    private static final String ENVIRONMENT = "test";

    @Autowired
    private RateLimitPolicyRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private RateLimitPolicy policyA1;
    private RateLimitPolicy policyA2;
    private RateLimitPolicy policyB1;

    @BeforeEach
    void cleanupTestData() {
        // Clean up any existing test data
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            RateLimitPolicyDocument.class
        );
    }

    @Test
    @Order(1)
    @DisplayName("[CRITICAL] When Tenant A creates policy, only Tenant A can read it")
    void whenTenantACreatesPolicy_thenOnlyTenantACanReadIt() {
        // Given: Tenant A creates a policy
        policyA1 = createTestPolicy(TENANT_A, "policy.a1");

        RateLimitPolicyDocument saved = repository.save(RateLimitPolicyDocument.fromDomain(policyA1));

        assertNotNull(saved);
        assertNotNull(saved.id());

        // When: Tenant A queries by their tenant ID
        Page<RateLimitPolicyDocument> tenantAQuery = repository.findByTenantId(TENANT_A, PageRequest.of(0, 10));

        // Then: Tenant A should see their policy
        assertThat(tenantAQuery.getContent()).hasSize(1);
        assertThat(tenantAQuery.getContent().get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAQuery.getContent().get(0).policyKey()).isEqualTo("policy.a1");

        // When: Tenant B queries by their tenant ID
        Page<RateLimitPolicyDocument> tenantBQuery = repository.findByTenantId(TENANT_B, PageRequest.of(0, 10));

        // Then: Tenant B should see nothing
        assertThat(tenantBQuery.getContent()).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("[CRITICAL] When Tenant A and B both create policies with same key, they are isolated")
    void whenTenantsCreateSameKey_thenTheyRemainIsolated() {
        // Given: Both tenants create a policy with the same key
        policyA1 = createTestPolicy(TENANT_A, "shared.policy.key");

        policyB1 = createTestPolicy(TENANT_B, "shared.policy.key");

        repository.save(RateLimitPolicyDocument.fromDomain(policyA1));
        repository.save(RateLimitPolicyDocument.fromDomain(policyB1));

        // When: Tenant A queries by natural key
        Optional<RateLimitPolicyDocument> resultA = repository.findByTenantIdAndPolicyKey(TENANT_A, "shared.policy.key");

        // Then: Tenant A should get their policy
        assertTrue(resultA.isPresent());
        assertThat(resultA.get().tenantId()).isEqualTo(TENANT_A);
        assertThat(resultA.get().name()).isEqualTo("Policy A1");

        // When: Tenant B queries by the same natural key
        Optional<RateLimitPolicyDocument> resultB = repository.findByTenantIdAndPolicyKey(TENANT_B, "shared.policy.key");

        // Then: Tenant B should get their different policy
        assertTrue(resultB.isPresent());
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
        assertThat(resultB.get().name()).isEqualTo("Policy B1");
    }

    @Test
    @Order(3)
    @DisplayName("[CRITICAL] When Tenant A deletes their policy, Tenant B's policy is unaffected")
    void whenTenantADeletes_thenTenantBPolicyUnaffected() {
        // Given: Both tenants have policies
        policyA1 = createTestPolicy(TENANT_A, "deletable.policy");

        policyB1 = createTestPolicy(TENANT_B, "deletable.policy");

        RateLimitPolicyDocument savedA = repository.save(RateLimitPolicyDocument.fromDomain(policyA1));
        repository.save(RateLimitPolicyDocument.fromDomain(policyB1));

        // When: Tenant A deletes their policy
        repository.deleteById(savedA.id());

        // Then: Tenant A should not find their policy
        Optional<RateLimitPolicyDocument> resultA = repository.findByTenantIdAndPolicyKey(TENANT_A, "deletable.policy");

        assertFalse(resultA.isPresent(), "Tenant A should not find their deleted policy");

        // And: Tenant B's policy should still exist
        Optional<RateLimitPolicyDocument> resultB = repository.findByTenantIdAndPolicyKey(TENANT_B, "deletable.policy");

        assertTrue(resultB.isPresent(), "Tenant B's policy should still exist");
        assertThat(resultB.get().name()).isEqualTo("Policy B1");
    }

    @Test
    @Order(4)
    @DisplayName("[CRITICAL] When Tenant A updates their policy, Tenant B's policy is unaffected")
    void whenTenantAUpdates_thenTenantBPolicyUnaffected() {
        // Given: Both tenants have policies with the same key
        RateLimitPolicy originalA = createTestPolicy(TENANT_A, "updatable.policy");

        RateLimitPolicy originalB = createTestPolicy(TENANT_B, "updatable.policy");

        RateLimitPolicyDocument savedA = repository.save(RateLimitPolicyDocument.fromDomain(originalA));
        repository.save(RateLimitPolicyDocument.fromDomain(originalB));

        // When: Tenant A updates their policy
        RateLimitPolicy updatedA = RateLimitPolicy.builder()
            .id(savedA.id())
            .tenantId(TENANT_A)
            .policyKey("updatable.policy")
            .name("Updated Policy A")
            .description("Updated description")
            .limitType(RateLimitPolicy.LimitType.API_KEY_BASED)
            .config(originalA.config())
            .scope(originalA.scope())
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy(originalA.createdBy())
            .createdAt(originalA.createdAt())
            .updatedBy("test-user")
            .updatedAt(Instant.now())
            .version(originalA.version())
            .build();

        repository.save(RateLimitPolicyDocument.fromDomain(updatedA));

        // Then: Tenant A should see the updated value
        Optional<RateLimitPolicyDocument> resultA = repository.findByTenantIdAndPolicyKey(TENANT_A, "updatable.policy");

        assertTrue(resultA.isPresent());
        assertThat(resultA.get().name()).isEqualTo("Updated Policy A");

        // And: Tenant B should still have their original value
        Optional<RateLimitPolicyDocument> resultB = repository.findByTenantIdAndPolicyKey(TENANT_B, "updatable.policy");

        assertTrue(resultB.isPresent());
        assertThat(resultB.get().name()).isEqualTo("Policy B1");
    }

    @Test
    @Order(5)
    @DisplayName("[CRITICAL] When Tenant A lists by environment, only Tenant A policies are returned")
    void whenTenantAListsByEnvironment_thenOnlyTenantAPoliciesReturned() {
        // Given: Tenant A has 2 policies, Tenant B has 1 policy in the same environment
        policyA1 = createTestPolicy(TENANT_A, "env.policy1");

        policyA2 = createTestPolicy(TENANT_A, "env.policy2");

        policyB1 = createTestPolicy(TENANT_B, "env.policy1");

        repository.save(RateLimitPolicyDocument.fromDomain(policyA1));
        repository.save(RateLimitPolicyDocument.fromDomain(policyA2));
        repository.save(RateLimitPolicyDocument.fromDomain(policyB1));

        // When: Tenant A lists policies by environment
        Page<RateLimitPolicyDocument> tenantAResults = repository.findByTenantIdAndEnvironment(
            TENANT_A, ENVIRONMENT, PageRequest.of(0, 10)
        );

        // Then: Tenant A should see only their 2 policies
        assertThat(tenantAResults.getContent()).hasSize(2);
        assertThat(tenantAResults.getContent()).allMatch(c -> TENANT_A.equals(c.tenantId()));

        // When: Tenant B lists policies by environment
        Page<RateLimitPolicyDocument> tenantBResults = repository.findByTenantIdAndEnvironment(
            TENANT_B, ENVIRONMENT, PageRequest.of(0, 10)
        );

        // Then: Tenant B should see only their 1 policy
        assertThat(tenantBResults.getContent()).hasSize(1);
        assertThat(tenantBResults.getContent().get(0).tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(6)
    @DisplayName("[CRITICAL] Cross-tenant data leak prevention - findByTenantId is strictly isolated")
    void crossTenantDataLeakPrevention_findByTenantId() {
        // Given: Multiple policies across tenants
        for (int i = 0; i < 5; i++) {
            RateLimitPolicy policyA = createTestPolicy(TENANT_A, "multi.policy." + i);
            repository.save(RateLimitPolicyDocument.fromDomain(policyA));
        }

        for (int i = 0; i < 3; i++) {
            RateLimitPolicy policyB = createTestPolicy(TENANT_B, "multi.policy." + i);
            repository.save(RateLimitPolicyDocument.fromDomain(policyB));
        }

        // When: Tenant A queries all their policies
        Page<RateLimitPolicyDocument> tenantAResults = repository.findByTenantId(TENANT_A, PageRequest.of(0, 10));

        // Then: Tenant A should see exactly their 5 policies, no more, no less
        assertThat(tenantAResults.getContent()).hasSize(5);
        assertThat(tenantAResults.getContent()).allMatch(c -> TENANT_A.equals(c.tenantId()));

        // And: Verify no data from Tenant B leaked into Tenant A's results
        long tenantBDataInResults = tenantAResults.getContent().stream()
            .filter(c -> TENANT_B.equals(c.tenantId()))
            .count();
        assertThat(tenantBDataInResults).isZero();
    }

    @Test
    @Order(7)
    @DisplayName("[CRITICAL] Direct MongoDB query also respects tenant isolation")
    void directMongoQueryRespectsTenantIsolation() {
        // Given: Policies from both tenants
        policyA1 = createTestPolicy(TENANT_A, "direct.query.policy");

        policyB1 = createTestPolicy(TENANT_B, "direct.query.policy");

        repository.save(RateLimitPolicyDocument.fromDomain(policyA1));
        repository.save(RateLimitPolicyDocument.fromDomain(policyB1));

        // When: Querying MongoDB directly for Tenant A
        List<RateLimitPolicyDocument> directQueryA = repository.findByTenantId(TENANT_A, Pageable.unpaged());

        // Then: Should return only Tenant A's policy
        assertThat(directQueryA).hasSize(1);
        assertThat(directQueryA.get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(directQueryA.get(0).name()).isEqualTo("Policy A1");

        // When: Querying MongoDB directly for Tenant B
        List<RateLimitPolicyDocument> directQueryB = repository.findByTenantId(TENANT_B, Pageable.unpaged());

        // Then: Should return only Tenant B's policy
        assertThat(directQueryB).hasSize(1);
        assertThat(directQueryB.get(0).tenantId()).isEqualTo(TENANT_B);
        assertThat(directQueryB.get(0).name()).isEqualTo("Policy B1");
    }

    @AfterAll
    void cleanupAllTestData() {
        // Final cleanup
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            RateLimitPolicyDocument.class
        );
        logger.info("Tenant isolation test cleanup completed");
    }

    // Helper method to create test policies
    private RateLimitPolicy createTestPolicy(String tenantId, String policyKey) {
        String nameSuffix = tenantId.equals(TENANT_A) ? "A1" : "B1";

        return RateLimitPolicy.builder()
            .tenantId(tenantId)
            .policyKey(policyKey)
            .name("Policy " + nameSuffix)
            .description("Test policy for " + policyKey)
            .limitType(RateLimitPolicy.LimitType.API_KEY_BASED)
            .config(new RateLimitPolicy.RateLimitConfig(60, 1000, 10000, 10, 60000, "token-bucket"))
            .scope(new RateLimitPolicy.Scope(Set.of("*"), Set.of(), Set.of(), Map.of()))
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();
    }
}
