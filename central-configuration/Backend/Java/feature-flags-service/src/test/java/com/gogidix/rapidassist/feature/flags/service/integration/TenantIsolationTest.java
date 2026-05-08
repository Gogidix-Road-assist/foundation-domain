package com.gogidix.rapidassist.feature.flags.service.integration;

import com.gogidix.rapidassist.feature.flags.service.adapters.infrastructure.MongoFeatureFlagRepository;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.port.out.FeatureFlagRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TEST: Validates tenant isolation in the Feature Flags service.
 *
 * <p>This test ensures that:
 * <ul>
 *   <li>Tenant A cannot access Tenant B's feature flags</li>
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
    private FeatureFlagRepository featureFlagRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private FeatureFlag flagA1;
    private FeatureFlag flagA2;
    private FeatureFlag flagB1;

    @BeforeEach
    void cleanupTestData() {
        // Clean up any existing test data
        mongoTemplate.remove(
            new Query(Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            MongoFeatureFlagRepository.FeatureFlagDocument.class
        );
    }

    @Test
    @Order(1)
    @DisplayName("[CRITICAL] When Tenant A creates feature flag, only Tenant A can read it")
    void whenTenantACreatesFeatureFlag_thenOnlyTenantACanReadIt() {
        // Given: Tenant A creates a feature flag
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("flag.a1")
            .name("Flag A1")
            .description("Tenant A's flag 1")
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        CompletableFuture<FeatureFlag> saveFuture = featureFlagRepository.save(flagA1);
        FeatureFlag saved = saveFuture.join();

        assertNotNull(saved);
        assertNotNull(saved.id());

        // When: Tenant A queries by their tenant ID
        CompletableFuture<List<FeatureFlag>> tenantAQuery = featureFlagRepository.findByTenant(TENANT_A);
        List<FeatureFlag> tenantAResults = tenantAQuery.join();

        // Then: Tenant A should see their feature flag
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAResults.get(0).key()).isEqualTo("flag.a1");

        // When: Tenant B queries by their tenant ID
        CompletableFuture<List<FeatureFlag>> tenantBQuery = featureFlagRepository.findByTenant(TENANT_B);
        List<FeatureFlag> tenantBResults = tenantBQuery.join();

        // Then: Tenant B should see nothing
        assertThat(tenantBResults).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("[CRITICAL] When Tenant A and B both create flags with same key, they are isolated")
    void whenTenantsCreateSameKey_thenTheyRemainIsolated() {
        // Given: Both tenants create a feature flag with the same key
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("shared.flag.key")
            .name("Shared Flag")
            .description("Tenant A's shared flag")
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagB1 = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("shared.flag.key")
            .name("Shared Flag")
            .description("Tenant B's shared flag")
            .enabled(false)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        featureFlagRepository.save(flagA1).join();
        featureFlagRepository.save(flagB1).join();

        // When: Tenant A queries by natural key
        CompletableFuture<Optional<FeatureFlag>> queryA = featureFlagRepository.findByKey(
            TENANT_A, "shared.flag.key", ENVIRONMENT
        );
        Optional<FeatureFlag> resultA = queryA.join();

        // Then: Tenant A should get their flag
        assertTrue(resultA.isPresent());
        assertThat(resultA.get().tenantId()).isEqualTo(TENANT_A);
        assertThat(resultA.get().enabled()).isTrue();

        // When: Tenant B queries by the same natural key
        CompletableFuture<Optional<FeatureFlag>> queryB = featureFlagRepository.findByKey(
            TENANT_B, "shared.flag.key", ENVIRONMENT
        );
        Optional<FeatureFlag> resultB = queryB.join();

        // Then: Tenant B should get their different flag
        assertTrue(resultB.isPresent());
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
        assertThat(resultB.get().enabled()).isFalse();
    }

    @Test
    @Order(3)
    @DisplayName("[CRITICAL] When Tenant A deletes their flag, Tenant B's flag is unaffected")
    void whenTenantADeletes_thenTenantBFlagUnaffected() {
        // Given: Both tenants have feature flags
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("deletable.flag")
            .name("Deletable Flag")
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagB1 = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("deletable.flag")
            .name("Deletable Flag")
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        FeatureFlag savedA = featureFlagRepository.save(flagA1).join();
        featureFlagRepository.save(flagB1).join();

        // When: Tenant A deletes their feature flag
        CompletableFuture<Boolean> deleteFuture = featureFlagRepository.deleteById(savedA.id());
        boolean deleted = deleteFuture.join();

        assertTrue(deleted, "Deletion should succeed");

        // Then: Tenant A should not find their flag
        CompletableFuture<Optional<FeatureFlag>> queryA = featureFlagRepository.findById(savedA.id());
        Optional<FeatureFlag> resultA = queryA.join();

        assertFalse(resultA.isPresent(), "Tenant A should not find their deleted flag");

        // And: Tenant B's flag should still exist
        CompletableFuture<List<FeatureFlag>> queryB = featureFlagRepository.findByTenant(TENANT_B);
        List<FeatureFlag> tenantBFlags = queryB.join();

        assertThat(tenantBFlags).hasSize(1);
        assertThat(tenantBFlags.get(0).tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(4)
    @DisplayName("[CRITICAL] When Tenant A updates their flag, Tenant B's flag is unaffected")
    void whenTenantAUpdates_thenTenantBFlagUnaffected() {
        // Given: Both tenants have feature flags with the same key
        FeatureFlag originalA = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("updatable.flag")
            .name("Updatable Flag")
            .enabled(false)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        FeatureFlag originalB = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("updatable.flag")
            .name("Updatable Flag")
            .enabled(false)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        featureFlagRepository.save(originalA).join();
        featureFlagRepository.save(originalB).join();

        // When: Tenant A updates their feature flag
        FeatureFlag updatedA = originalA.toBuilder().enabled(true).build();
        featureFlagRepository.save(updatedA).join();

        // Then: Tenant A should see the updated flag
        CompletableFuture<Optional<FeatureFlag>> queryA = featureFlagRepository.findByKey(
            TENANT_A, "updatable.flag", ENVIRONMENT
        );
        Optional<FeatureFlag> resultA = queryA.join();

        assertTrue(resultA.isPresent());
        assertThat(resultA.get().enabled()).isTrue();

        // And: Tenant B should still have their original flag
        CompletableFuture<Optional<FeatureFlag>> queryB = featureFlagRepository.findByKey(
            TENANT_B, "updatable.flag", ENVIRONMENT
        );
        Optional<FeatureFlag> resultB = queryB.join();

        assertTrue(resultB.isPresent());
        assertThat(resultB.get().enabled()).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("[CRITICAL] When Tenant A lists by environment, only Tenant A flags are returned")
    void whenTenantAListsByEnvironment_thenOnlyTenantAFlagsReturned() {
        // Given: Tenant A has 2 flags, Tenant B has 1 flag in the same environment
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("environment.flag1")
            .name("Environment Flag 1")
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagA2 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("environment.flag2")
            .name("Environment Flag 2")
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagB1 = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("environment.flag1")
            .name("Environment Flag 1")
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        featureFlagRepository.save(flagA1).join();
        featureFlagRepository.save(flagA2).join();
        featureFlagRepository.save(flagB1).join();

        // When: Tenant A lists flags by environment
        CompletableFuture<List<FeatureFlag>> queryA = featureFlagRepository.findByTenantAndEnvironment(
            TENANT_A, ENVIRONMENT
        );
        List<FeatureFlag> tenantAResults = queryA.join();

        // Then: Tenant A should see only their 2 flags
        assertThat(tenantAResults).hasSize(2);
        assertThat(tenantAResults).allMatch(f -> TENANT_A.equals(f.tenantId()));

        // When: Tenant B lists flags by environment
        CompletableFuture<List<FeatureFlag>> queryB = featureFlagRepository.findByTenantAndEnvironment(
            TENANT_B, ENVIRONMENT
        );
        List<FeatureFlag> tenantBResults = queryB.join();

        // Then: Tenant B should see only their 1 flag
        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(6)
    @DisplayName("[CRITICAL] Cross-tenant data leak prevention - findByTenant is strictly isolated")
    void crossTenantDataLeakPrevention_findByTenant() {
        // Given: Multiple feature flags across tenants
        for (int i = 0; i < 5; i++) {
            FeatureFlag flagA = FeatureFlag.builder()
                .tenantId(TENANT_A)
                .key("multi.flag." + i)
                .name("Multi Flag " + i)
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            featureFlagRepository.save(flagA).join();
        }

        for (int i = 0; i < 3; i++) {
            FeatureFlag flagB = FeatureFlag.builder()
                .tenantId(TENANT_B)
                .key("multi.flag." + i)
                .name("Multi Flag " + i)
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            featureFlagRepository.save(flagB).join();
        }

        // When: Tenant A queries all their feature flags
        CompletableFuture<List<FeatureFlag>> queryA = featureFlagRepository.findByTenant(TENANT_A);
        List<FeatureFlag> tenantAResults = queryA.join();

        // Then: Tenant A should see exactly their 5 flags, no more, no less
        assertThat(tenantAResults).hasSize(5);
        assertThat(tenantAResults).allMatch(f -> TENANT_A.equals(f.tenantId()));

        // And: Verify no data from Tenant B leaked into Tenant A's results
        long tenantBDataInResults = tenantAResults.stream()
            .filter(f -> TENANT_B.equals(f.tenantId()))
            .count();
        assertThat(tenantBDataInResults).isZero();
    }

    @Test
    @Order(7)
    @DisplayName("[CRITICAL] Direct MongoDB query also respects tenant isolation")
    void directMongoQueryRespectsTenantIsolation() {
        // Given: Feature flags from both tenants
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("direct.query.flag")
            .name("Direct Query Flag")
            .enabled(true)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagB1 = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("direct.query.flag")
            .name("Direct Query Flag")
            .enabled(false)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        featureFlagRepository.save(flagA1).join();
        featureFlagRepository.save(flagB1).join();

        // When: Querying MongoDB directly for Tenant A
        Query queryA = new Query();
        queryA.addCriteria(Criteria.where("tenantId").is(TENANT_A));
        List<MongoFeatureFlagRepository.FeatureFlagDocument> directQueryA =
            mongoTemplate.find(queryA, MongoFeatureFlagRepository.FeatureFlagDocument.class);

        // Then: Should return only Tenant A's flag
        assertThat(directQueryA).hasSize(1);
        assertThat(directQueryA.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(directQueryA.get(0).isEnabled()).isTrue();

        // When: Querying MongoDB directly for Tenant B
        Query queryB = new Query();
        queryB.addCriteria(Criteria.where("tenantId").is(TENANT_B));
        List<MongoFeatureFlagRepository.FeatureFlagDocument> directQueryB =
            mongoTemplate.find(queryB, MongoFeatureFlagRepository.FeatureFlagDocument.class);

        // Then: Should return only Tenant B's flag
        assertThat(directQueryB).hasSize(1);
        assertThat(directQueryB.get(0).getTenantId()).isEqualTo(TENANT_B);
        assertThat(directQueryB.get(0).isEnabled()).isFalse();
    }

    @Test
    @Order(8)
    @DisplayName("[CRITICAL] Search by keyword respects tenant isolation")
    void searchByKeywordRespectsTenantIsolation() {
        // Given: Feature flags from both tenants with similar keywords
        flagA1 = FeatureFlag.builder()
            .tenantId(TENANT_A)
            .key("search.feature.new")
            .name("New Feature")
            .description("Searchable feature for tenant A")
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        flagB1 = FeatureFlag.builder()
            .tenantId(TENANT_B)
            .key("search.feature.beta")
            .name("Beta Feature")
            .description("Searchable feature for tenant B")
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        featureFlagRepository.save(flagA1).join();
        featureFlagRepository.save(flagB1).join();

        // When: Tenant A searches for "feature"
        CompletableFuture<List<FeatureFlag>> searchA = featureFlagRepository.searchByKeyword(TENANT_A, "feature");
        List<FeatureFlag> resultsA = searchA.join();

        // Then: Tenant A should only see their flags
        assertThat(resultsA).isNotEmpty();
        assertThat(resultsA).allMatch(f -> TENANT_A.equals(f.tenantId()));

        // When: Tenant B searches for "feature"
        CompletableFuture<List<FeatureFlag>> searchB = featureFlagRepository.searchByKeyword(TENANT_B, "feature");
        List<FeatureFlag> resultsB = searchB.join();

        // Then: Tenant B should only see their flags
        assertThat(resultsB).isNotEmpty();
        assertThat(resultsB).allMatch(f -> TENANT_B.equals(f.tenantId()));
    }

    @AfterAll
    void cleanupAllTestData() {
        // Final cleanup
        mongoTemplate.remove(
            new Query(Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            MongoFeatureFlagRepository.FeatureFlagDocument.class
        );
        logger.info("Tenant isolation test cleanup completed");
    }
}
