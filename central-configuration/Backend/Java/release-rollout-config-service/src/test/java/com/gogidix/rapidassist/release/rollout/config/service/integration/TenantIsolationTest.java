package com.gogidix.rapidassist.release.rollout.config.service.integration;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;
import com.gogidix.rapidassist.release.rollout.config.service.domain.repository.ReleaseRolloutRepositoryInterface;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TEST: Validates tenant isolation in the Release Rollout Configuration service.
 *
 * <p>This test ensures that:
 * <ul>
 *   <li>Tenant A cannot access Tenant B's rollouts</li>
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
public class TenantIsolationTest {

    private static final Logger logger = LoggerFactory.getLogger(TenantIsolationTest.class);

    private static final String TENANT_A = "tenant-a-isolation-test";
    private static final String TENANT_B = "tenant-b-isolation-test";
    private static final String ENVIRONMENT = "test";
    private static final String VERSION = "1.0.0";

    @Autowired
    private ReleaseRolloutRepositoryInterface rolloutRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private ReleaseRollout rolloutA1;
    private ReleaseRollout rolloutA2;
    private ReleaseRollout rolloutB1;

    @BeforeEach
    void cleanupTestData() {
        // Clean up any existing test data
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            ReleaseRollout.class
        );
    }

    @Test
    @Order(1)
    @DisplayName("[CRITICAL] When Tenant A creates rollout, only Tenant A can read it")
    void whenTenantACreatesRollout_thenOnlyTenantACanReadIt() {
        // Given: Tenant A creates a rollout
        rolloutA1 = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("release-a1")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.BLUE_GREEN)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        CompletableFuture<ReleaseRollout> saveFuture = rolloutRepository.save(rolloutA1);
        ReleaseRollout saved = saveFuture.join();

        assertNotNull(saved);
        assertNotNull(saved.id());

        // When: Tenant A queries by their tenant ID
        CompletableFuture<List<ReleaseRollout>> tenantAQuery = rolloutRepository.findByTenantId(TENANT_A);
        List<ReleaseRollout> tenantAResults = tenantAQuery.join();

        // Then: Tenant A should see their rollout
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAResults.get(0).releaseId()).isEqualTo("release-a1");

        // When: Tenant B queries by their tenant ID
        CompletableFuture<List<ReleaseRollout>> tenantBQuery = rolloutRepository.findByTenantId(TENANT_B);
        List<ReleaseRollout> tenantBResults = tenantBQuery.join();

        // Then: Tenant B should see nothing
        assertThat(tenantBResults).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("[CRITICAL] When Tenant A and B both create rollouts with same release ID, they are isolated")
    void whenTenantsCreateSameReleaseId_thenTheyRemainIsolated() {
        // Given: Both tenants create a rollout with the same release ID
        rolloutA1 = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("shared-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.CANARY)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutB1 = ReleaseRollout.builder()
            .tenantId(TENANT_B)
            .releaseId("shared-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.CANARY)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutRepository.save(rolloutA1).join();
        rolloutRepository.save(rolloutB1).join();

        // When: Tenant A queries by natural key
        CompletableFuture<Optional<ReleaseRollout>> queryA = rolloutRepository.findByNaturalKey(
            TENANT_A, "shared-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultA = queryA.join();

        // Then: Tenant A should get their rollout
        assertTrue(resultA.isPresent());
        assertThat(resultA.get().tenantId()).isEqualTo(TENANT_A);
        assertThat(resultA.get().releaseId()).isEqualTo("shared-release");

        // When: Tenant B queries by the same natural key
        CompletableFuture<Optional<ReleaseRollout>> queryB = rolloutRepository.findByNaturalKey(
            TENANT_B, "shared-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultB = queryB.join();

        // Then: Tenant B should get their rollout
        assertTrue(resultB.isPresent());
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
        assertThat(resultB.get().releaseId()).isEqualTo("shared-release");
    }

    @Test
    @Order(3)
    @DisplayName("[CRITICAL] When Tenant A deletes their rollout, Tenant B's rollout is unaffected")
    void whenTenantADeletes_thenTenantBRolloutUnaffected() {
        // Given: Both tenants have rollouts
        rolloutA1 = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("deletable-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.GRADUAL)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutB1 = ReleaseRollout.builder()
            .tenantId(TENANT_B)
            .releaseId("deletable-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.GRADUAL)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        ReleaseRollout savedA = rolloutRepository.save(rolloutA1).join();
        rolloutRepository.save(rolloutB1).join();

        // When: Tenant A deletes their rollout
        CompletableFuture<Boolean> deleteFuture = rolloutRepository.deleteById(savedA.id());
        boolean deleted = deleteFuture.join();

        assertTrue(deleted, "Deletion should succeed");

        // Then: Tenant A should not find their rollout
        CompletableFuture<Optional<ReleaseRollout>> queryA = rolloutRepository.findByNaturalKey(
            TENANT_A, "deletable-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultA = queryA.join();

        assertFalse(resultA.isPresent(), "Tenant A should not find their deleted rollout");

        // And: Tenant B's rollout should still exist
        CompletableFuture<Optional<ReleaseRollout>> queryB = rolloutRepository.findByNaturalKey(
            TENANT_B, "deletable-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultB = queryB.join();

        assertTrue(resultB.isPresent(), "Tenant B's rollout should still exist");
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(4)
    @DisplayName("[CRITICAL] When Tenant A updates their rollout, Tenant B's rollout is unaffected")
    void whenTenantAUpdates_thenTenantBRolloutUnaffected() {
        // Given: Both tenants have rollouts with the same release ID
        ReleaseRollout originalA = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("updatable-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.BIG_BANG)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        ReleaseRollout originalB = ReleaseRollout.builder()
            .tenantId(TENANT_B)
            .releaseId("updatable-release")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.BIG_BANG)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutRepository.save(originalA).join();
        rolloutRepository.save(originalB).join();

        // When: Tenant A updates their rollout
        ReleaseRollout updatedA = ReleaseRollout.builder()
            .id(originalA.id())
            .tenantId(originalA.tenantId())
            .releaseId(originalA.releaseId())
            .version(originalA.version())
            .strategy(ReleaseRollout.RolloutStrategy.CANARY)
            .config(originalA.config())
            .status(ReleaseRollout.RolloutStatus.IN_PROGRESS)
            .environment(originalA.environment())
            .createdBy(originalA.createdBy())
            .createdAt(originalA.createdAt())
            .updatedBy("test-user")
            .updatedAt(Instant.now())
            .recordVersion(originalA.recordVersion() + 1)
            .build();

        rolloutRepository.save(updatedA).join();

        // Then: Tenant A should see the updated strategy
        CompletableFuture<Optional<ReleaseRollout>> queryA = rolloutRepository.findByNaturalKey(
            TENANT_A, "updatable-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultA = queryA.join();

        assertTrue(resultA.isPresent());
        assertThat(resultA.get().strategy()).isEqualTo(ReleaseRollout.RolloutStrategy.CANARY);

        // And: Tenant B should still have their original strategy
        CompletableFuture<Optional<ReleaseRollout>> queryB = rolloutRepository.findByNaturalKey(
            TENANT_B, "updatable-release", VERSION, ENVIRONMENT
        );
        Optional<ReleaseRollout> resultB = queryB.join();

        assertTrue(resultB.isPresent());
        assertThat(resultB.get().strategy()).isEqualTo(ReleaseRollout.RolloutStrategy.BIG_BANG);
    }

    @Test
    @Order(5)
    @DisplayName("[CRITICAL] When Tenant A lists by environment, only Tenant A rollouts are returned")
    void whenTenantAListsByEnvironment_thenOnlyTenantARolloutsReturned() {
        // Given: Tenant A has 2 rollouts, Tenant B has 1 rollout in the same environment
        rolloutA1 = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("release-1")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.BLUE_GREEN)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutA2 = ReleaseRollout.builder()
            .tenantId(TENANT_A)
            .releaseId("release-2")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.CANARY)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutB1 = ReleaseRollout.builder()
            .tenantId(TENANT_B)
            .releaseId("release-1")
            .version(VERSION)
            .strategy(ReleaseRollout.RolloutStrategy.BLUE_GREEN)
            .config(ReleaseRollout.RolloutConfig.standard())
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        rolloutRepository.save(rolloutA1).join();
        rolloutRepository.save(rolloutA2).join();
        rolloutRepository.save(rolloutB1).join();

        // When: Tenant A lists rollouts by environment
        CompletableFuture<List<ReleaseRollout>> queryA = rolloutRepository.findByEnvironment(TENANT_A, ENVIRONMENT);
        List<ReleaseRollout> tenantAResults = queryA.join();

        // Then: Tenant A should see only their 2 rollouts
        assertThat(tenantAResults).hasSize(2);
        assertThat(tenantAResults).allMatch(r -> TENANT_A.equals(r.tenantId()));

        // When: Tenant B lists rollouts by environment
        CompletableFuture<List<ReleaseRollout>> queryB = rolloutRepository.findByEnvironment(TENANT_B, ENVIRONMENT);
        List<ReleaseRollout> tenantBResults = queryB.join();

        // Then: Tenant B should see only their 1 rollout
        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(6)
    @DisplayName("[CRITICAL] Cross-tenant data leak prevention - findByTenantId is strictly isolated")
    void crossTenantDataLeakPrevention_findByTenantId() {
        // Given: Multiple rollouts across tenants
        for (int i = 0; i < 5; i++) {
            ReleaseRollout rolloutA = ReleaseRollout.builder()
                .tenantId(TENANT_A)
                .releaseId("multi-release-" + i)
                .version(VERSION)
                .strategy(ReleaseRollout.RolloutStrategy.CANARY)
                .config(ReleaseRollout.RolloutConfig.standard())
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            rolloutRepository.save(rolloutA).join();
        }

        for (int i = 0; i < 3; i++) {
            ReleaseRollout rolloutB = ReleaseRollout.builder()
                .tenantId(TENANT_B)
                .releaseId("multi-release-" + i)
                .version(VERSION)
                .strategy(ReleaseRollout.RolloutStrategy.CANARY)
                .config(ReleaseRollout.RolloutConfig.standard())
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            rolloutRepository.save(rolloutB).join();
        }

        // When: Tenant A queries all their rollouts
        CompletableFuture<List<ReleaseRollout>> queryA = rolloutRepository.findByTenantId(TENANT_A);
        List<ReleaseRollout> tenantAResults = queryA.join();

        // Then: Tenant A should see exactly their 5 rollouts, no more, no less
        assertThat(tenantAResults).hasSize(5);
        assertThat(tenantAResults).allMatch(r -> TENANT_A.equals(r.tenantId()));

        // And: Verify no data from Tenant B leaked into Tenant A's results
        long tenantBDataInResults = tenantAResults.stream()
            .filter(r -> TENANT_B.equals(r.tenantId()))
            .count();
        assertThat(tenantBDataInResults).isZero();
    }

    @AfterAll
    void cleanupAllTestData() {
        // Final cleanup
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            ReleaseRollout.class
        );
        logger.info("Tenant isolation test cleanup completed");
    }
}
