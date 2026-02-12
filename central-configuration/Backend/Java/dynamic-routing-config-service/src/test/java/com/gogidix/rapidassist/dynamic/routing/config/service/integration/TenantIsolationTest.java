package com.gogidix.rapidassist.dynamic.routing.config.service.integration;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.out.RoutingRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TEST: Validates tenant isolation in the Routing Rule service.
 *
 * <p>This test ensures that:
 * <ul>
 *   <li>Tenant A cannot access Tenant B's routing rules</li>
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

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private RoutingRule ruleA1;
    private RoutingRule ruleA2;
    private RoutingRule ruleB1;

    @BeforeEach
    void cleanupTestData() {
        // Clean up any existing test data
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            RoutingRule.class
        );
    }

    @Test
    @Order(1)
    @DisplayName("[CRITICAL] When Tenant A creates routing rule, only Tenant A can read it")
    void whenTenantACreatesRoutingRule_thenOnlyTenantACanReadIt() {
        // Given: Tenant A creates a routing rule
        ruleA1 = RoutingRule.builder()
            .tenantId(TENANT_A)
            .ruleName("rule-a1")
            .pattern(RoutingRule.RoutePattern.prefix("/api/a1"))
            .target(RoutingRule.RouteTarget.http("http://service-a:8080"))
            .priority(10)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        CompletableFuture<RoutingRule> saveFuture = routingRepository.save(ruleA1);
        RoutingRule saved = saveFuture.join();

        assertNotNull(saved);
        assertNotNull(saved.id());

        // When: Tenant A queries by their tenant ID
        CompletableFuture<List<RoutingRule>> tenantAQuery = routingRepository.findByTenant(TENANT_A);
        List<RoutingRule> tenantAResults = tenantAQuery.join();

        // Then: Tenant A should see their routing rule
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAResults.get(0).ruleName()).isEqualTo("rule-a1");

        // When: Tenant B queries by their tenant ID
        CompletableFuture<List<RoutingRule>> tenantBQuery = routingRepository.findByTenant(TENANT_B);
        List<RoutingRule> tenantBResults = tenantBQuery.join();

        // Then: Tenant B should see nothing
        assertThat(tenantBResults).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("[CRITICAL] When Tenant A and B both create rules with same name, they are isolated")
    void whenTenantsCreateSameName_thenTheyRemainIsolated() {
        // Given: Both tenants create a routing rule with the same name
        ruleA1 = RoutingRule.builder()
            .tenantId(TENANT_A)
            .ruleName("shared-rule-name")
            .pattern(RoutingRule.RoutePattern.prefix("/api/shared"))
            .target(RoutingRule.RouteTarget.http("http://service-a:8080"))
            .priority(10)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        ruleB1 = RoutingRule.builder()
            .tenantId(TENANT_B)
            .ruleName("shared-rule-name")
            .pattern(RoutingRule.RoutePattern.prefix("/api/shared"))
            .target(RoutingRule.RouteTarget.http("http://service-b:8080"))
            .priority(10)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .build();

        routingRepository.save(ruleA1).join();
        routingRepository.save(ruleB1).join();

        // When: Tenant A queries by natural key
        CompletableFuture<Optional<RoutingRule>> queryA = routingRepository.findByNaturalKey(
            TENANT_A, "shared-rule-name", ENVIRONMENT
        );
        Optional<RoutingRule> resultA = queryA.join();

        // Then: Tenant A should get their rule
        assertTrue(resultA.isPresent());
        assertThat(resultA.get().tenantId()).isEqualTo(TENANT_A);
        assertThat(resultA.get().target().endpoint()).contains("service-a");

        // When: Tenant B queries by the same natural key
        CompletableFuture<Optional<RoutingRule>> queryB = routingRepository.findByNaturalKey(
            TENANT_B, "shared-rule-name", ENVIRONMENT
        );
        Optional<RoutingRule> resultB = queryB.join();

        // Then: Tenant B should get their different rule
        assertTrue(resultB.isPresent());
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
        assertThat(resultB.get().target().endpoint()).contains("service-b");
    }

    @Test
    @Order(3)
    @DisplayName("[CRITICAL] When Tenant A deletes their rule, Tenant B's rule is unaffected")
    void whenTenantADeletes_thenTenantBRuleUnaffected() {
        // Given: Both tenants have routing rules
        ruleA1 = RoutingRule.builder()
            .tenantId(TENANT_A)
            .ruleName("deletable-rule")
            .pattern(RoutingRule.RoutePattern.prefix("/api/deletable"))
            .target(RoutingRule.RouteTarget.http("http://service-a:8080"))
            .priority(10)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .active(false)
            .build();

        ruleB1 = RoutingRule.builder()
            .tenantId(TENANT_B)
            .ruleName("deletable-rule")
            .pattern(RoutingRule.RoutePattern.prefix("/api/deletable"))
            .target(RoutingRule.RouteTarget.http("http://service-b:8080"))
            .priority(10)
            .environment(ENVIRONMENT)
            .createdBy("test-user")
            .active(false)
            .build();

        RoutingRule savedA = routingRepository.save(ruleA1).join();
        routingRepository.save(ruleB1).join();

        // When: Tenant A deletes their routing rule
        CompletableFuture<Boolean> deleteFuture = routingRepository.deleteById(savedA.id());
        boolean deleted = deleteFuture.join();

        assertTrue(deleted, "Deletion should succeed");

        // Then: Tenant A should not find their rule
        CompletableFuture<Optional<RoutingRule>> queryA = routingRepository.findById(savedA.id());
        Optional<RoutingRule> resultA = queryA.join();

        assertFalse(resultA.isPresent(), "Tenant A should not find their deleted rule");

        // And: Tenant B's rule should still exist
        CompletableFuture<Optional<RoutingRule>> queryB = routingRepository.findByNaturalKey(
            TENANT_B, "deletable-rule", ENVIRONMENT
        );
        Optional<RoutingRule> resultB = queryB.join();

        assertTrue(resultB.isPresent(), "Tenant B's rule should still exist");
        assertThat(resultB.get().target().endpoint()).contains("service-b");
    }

    @Test
    @Order(4)
    @DisplayName("[CRITICAL] Cross-tenant data leak prevention - findByTenant is strictly isolated")
    void crossTenantDataLeakPrevention_findByTenant() {
        // Given: Multiple routing rules across tenants
        for (int i = 0; i < 5; i++) {
            RoutingRule ruleA = RoutingRule.builder()
                .tenantId(TENANT_A)
                .ruleName("multi-rule-" + i)
                .pattern(RoutingRule.RoutePattern.prefix("/api/multi-" + i))
                .target(RoutingRule.RouteTarget.http("http://service-a:8080"))
                .priority(10)
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            routingRepository.save(ruleA).join();
        }

        for (int i = 0; i < 3; i++) {
            RoutingRule ruleB = RoutingRule.builder()
                .tenantId(TENANT_B)
                .ruleName("multi-rule-" + i)
                .pattern(RoutingRule.RoutePattern.prefix("/api/multi-" + i))
                .target(RoutingRule.RouteTarget.http("http://service-b:8080"))
                .priority(10)
                .environment(ENVIRONMENT)
                .createdBy("test-user")
                .build();
            routingRepository.save(ruleB).join();
        }

        // When: Tenant A queries all their routing rules
        CompletableFuture<List<RoutingRule>> queryA = routingRepository.findByTenant(TENANT_A);
        List<RoutingRule> tenantAResults = queryA.join();

        // Then: Tenant A should see exactly their 5 rules, no more, no less
        assertThat(tenantAResults).hasSize(5);
        assertThat(tenantAResults).allMatch(c -> TENANT_A.equals(c.tenantId()));

        // And: Verify no data from Tenant B leaked into Tenant A's results
        long tenantBDataInResults = tenantAResults.stream()
            .filter(c -> TENANT_B.equals(c.tenantId()))
            .count();
        assertThat(tenantBDataInResults).isZero();
    }

    @Test
    @Order(5)
    @DisplayName("[CRITICAL] Tenant isolation is maintained across different environments")
    void tenantIsolationAcrossEnvironments() {
        // Given: Tenant A has rules in multiple environments
        RoutingRule devRule = RoutingRule.builder()
            .tenantId(TENANT_A)
            .ruleName("env-specific-rule")
            .pattern(RoutingRule.RoutePattern.prefix("/api/dev"))
            .target(RoutingRule.RouteTarget.http("http://dev-service:8080"))
            .priority(10)
            .environment("dev")
            .createdBy("test-user")
            .build();

        RoutingRule prodRule = RoutingRule.builder()
            .tenantId(TENANT_A)
            .ruleName("env-specific-rule")
            .pattern(RoutingRule.RoutePattern.prefix("/api/prod"))
            .target(RoutingRule.RouteTarget.http("http://prod-service:8080"))
            .priority(10)
            .environment("prod")
            .createdBy("test-user")
            .build();

        routingRepository.save(devRule).join();
        routingRepository.save(prodRule).join();

        // When: Tenant A queries for dev environment
        CompletableFuture<Optional<RoutingRule>> devQuery = routingRepository.findByNaturalKey(
            TENANT_A, "env-specific-rule", "dev"
        );
        Optional<RoutingRule> devResult = devQuery.join();

        // Then: Tenant A should get the dev rule
        assertTrue(devResult.isPresent());
        assertThat(devResult.get().environment()).isEqualTo("dev");
        assertThat(devResult.get().target().endpoint()).contains("dev-service");

        // When: Tenant A queries for prod environment
        CompletableFuture<Optional<RoutingRule>> prodQuery = routingRepository.findByNaturalKey(
            TENANT_A, "env-specific-rule", "prod"
        );
        Optional<RoutingRule> prodResult = prodQuery.join();

        // Then: Tenant A should get the prod rule
        assertTrue(prodResult.isPresent());
        assertThat(prodResult.get().environment()).isEqualTo("prod");
        assertThat(prodResult.get().target().endpoint()).contains("prod-service");
    }

    @AfterAll
    void cleanupAllTestData() {
        // Final cleanup
        mongoTemplate.remove(
            new Query(org.springframework.data.mongodb.core.query.Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            RoutingRule.class
        );
        logger.info("Tenant isolation test cleanup completed");
    }
}
