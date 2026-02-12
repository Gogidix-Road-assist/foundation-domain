package com.gogidix.rapidassist.ai.categorization.integration;

import com.gogidix.rapidassist.ai.categorization.application.port.out.CategorizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategoryRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationRequest;
import com.gogidix.rapidassist.ai.categorization.domain.model.Category;
import com.gogidix.rapidassist.ai.categorization.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.categorization.infrastructure.tenant.TenantContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 *
 * This test ensures that:
 * - Tenant A cannot access Tenant B's data
 * - findById from different tenant returns empty
 * - Tenant filtering works correctly across all operations
 *
 * MUST pass for ALL services before production deployment.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tenant Isolation Tests")
public class TenantIsolationTest {

    @Autowired
    private CategorizationRequestRepositoryPort categorizationRequestRepository;

    @Autowired
    private CategoryRepositoryPort categoryRepository;

    private static final String TENANT_A = "tenant-a-test";
    private static final String TENANT_B = "tenant-b-test";
    private static final String USER_A = "user-a";
    private static final String USER_B = "user-b";

    private UUID tenantACategorizationRequestId;
    private UUID tenantBRequestId;
    private UUID tenantACategoryId;
    private UUID tenantBCategoryId;

    @AfterAll
    void cleanup() {
        // Clean up test data
        try {
            RequestContextHolder.set(TenantContext.builder()
                    .tenantId(TENANT_A)
                    .userId(USER_A)
                    .correlationId(UUID.randomUUID().toString())
                    .build());

            categorizationRequestRepository.findById(TENANT_A, tenantACategorizationRequestId)
                    .ifPresent(request -> categorizationRequestRepository.delete(TENANT_A, request.getId()));

            categoryRepository.findById(TENANT_A, tenantACategoryId)
                    .ifPresent(category -> categoryRepository.delete(TENANT_A, category.getId()));

            RequestContextHolder.set(TenantContext.builder()
                    .tenantId(TENANT_B)
                    .userId(USER_B)
                    .correlationId(UUID.randomUUID().toString())
                    .build());

            categorizationRequestRepository.findById(TENANT_B, tenantBRequestId)
                    .ifPresent(request -> categorizationRequestRepository.delete(TENANT_B, request.getId()));

            categoryRepository.findById(TENANT_B, tenantBCategoryId)
                    .ifPresent(category -> categoryRepository.delete(TENANT_B, category.getId()));

        } finally {
            RequestContextHolder.clear();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Given: Tenant A and Tenant B both have categorization requests")
    void setUpTestData() {
        // Create data for Tenant A
        TenantContext tenantA = TenantContext.builder()
                .tenantId(TENANT_A)
                .userId(USER_A)
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(tenantA);

        CategorizationRequest requestA = CategorizationRequest.create(
                TENANT_A,
                "content-a",
                "text",
                "Test content for tenant A",
                UUID.randomUUID()
        );
        CategorizationRequest savedA = categorizationRequestRepository.save(TENANT_A, requestA);
        tenantACategorizationRequestId = savedA.getId();

        // Create data for Tenant B
        TenantContext tenantB = TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(tenantB);

        CategorizationRequest requestB = CategorizationRequest.create(
                TENANT_B,
                "content-b",
                "text",
                "Test content for tenant B",
                UUID.randomUUID()
        );
        CategorizationRequest savedB = categorizationRequestRepository.save(TENANT_B, requestB);
        tenantBRequestId = savedB.getId();

        RequestContextHolder.clear();

        assertThat(tenantACategorizationRequestId).isNotNull();
        assertThat(tenantBRequestId).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("When: Tenant A queries categorization requests, Then: Should only see Tenant A data")
    void whenTenantAQueriesCategorizationRequests_shouldOnlySeeTenantAData() {
        // Given: Set context to Tenant A
        TenantContext tenantA = TenantContext.builder()
                .tenantId(TENANT_A)
                .userId(USER_A)
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(tenantA);

        // When: Query all requests for Tenant A
        List<CategorizationRequest> tenantARequests = categorizationRequestRepository.findByTenantId(TENANT_A);

        // Then: Should only see Tenant A's data
        assertThat(tenantARequests).hasSize(1);
        assertThat(tenantARequests.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(tenantARequests.get(0).getContent()).contains("tenant A");

        RequestContextHolder.clear();
    }

    @Test
    @Order(3)
    @DisplayName("When: Tenant B queries categorization requests, Then: Should only see Tenant B data")
    void whenTenantBQueriesCategorizationRequests_shouldOnlySeeTenantBData() {
        // Given: Set context to Tenant B
        TenantContext tenantB = TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(tenantB);

        // When: Query all requests for Tenant B
        List<CategorizationRequest> tenantBRequests = categorizationRequestRepository.findByTenantId(TENANT_B);

        // Then: Should only see Tenant B's data
        assertThat(tenantBRequests).hasSize(1);
        assertThat(tenantBRequests.get(0).getTenantId()).isEqualTo(TENANT_B);
        assertThat(tenantBRequests.get(0).getContent()).contains("tenant B");

        RequestContextHolder.clear();
    }

    @Test
    @Order(4)
    @DisplayName("When: Tenant B tries to access Tenant A's request by ID, Then: Should return empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Set context to Tenant B
        TenantContext tenantB = TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(tenantB);

        // When: Tenant B tries to access Tenant A's request
        Optional<CategorizationRequest> result = categorizationRequestRepository.findById(
                TENANT_B,
                tenantACategorizationRequestId
        );

        // Then: Result should be empty (tenant isolation works)
        assertThat(result).isEmpty();

        RequestContextHolder.clear();
    }

    @Test
    @Order(5)
    @DisplayName("Given: Tenant A and Tenant B both have categories")
    void setUpCategoryTestData() {
        // Create category for Tenant A
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_A)
                .userId(USER_A)
                .correlationId(UUID.randomUUID().toString())
                .build());

        Category categoryA = Category.createRoot(
                TENANT_A,
                "Vehicle",
                "VEHICLE",
                "Vehicle-related categories"
        );
        Category savedA = categoryRepository.save(TENANT_A, categoryA);
        tenantACategoryId = savedA.getId();

        // Create category for Tenant B
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build());

        Category categoryB = Category.createRoot(
                TENANT_B,
                "Insurance",
                "INSURANCE",
                "Insurance-related categories"
        );
        Category savedB = categoryRepository.save(TENANT_B, categoryB);
        tenantBCategoryId = savedB.getId();

        RequestContextHolder.clear();

        assertThat(tenantACategoryId).isNotNull();
        assertThat(tenantBCategoryId).isNotNull();
    }

    @Test
    @Order(6)
    @DisplayName("When: Tenant A queries categories, Then: Should only see Tenant A categories")
    void whenTenantAQueriesCategories_shouldOnlySeeTenantACategories() {
        // Given: Set context to Tenant A
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_A)
                .userId(USER_A)
                .correlationId(UUID.randomUUID().toString())
                .build());

        // When: Query all categories for Tenant A
        List<Category> tenantACategories = categoryRepository.findByTenantId(TENANT_A);

        // Then: Should only see Tenant A's categories
        assertThat(tenantACategories).hasSize(1);
        assertThat(tenantACategories.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(tenantACategories.get(0).getName()).isEqualTo("Vehicle");

        RequestContextHolder.clear();
    }

    @Test
    @Order(7)
    @DisplayName("When: Tenant B tries to access Tenant A's category by ID, Then: Should return empty")
    void findCategoryById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Set context to Tenant B
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build());

        // When: Tenant B tries to access Tenant A's category
        Optional<Category> result = categoryRepository.findById(TENANT_B, tenantACategoryId);

        // Then: Result should be empty (tenant isolation works)
        assertThat(result).isEmpty();

        RequestContextHolder.clear();
    }

    @Test
    @Order(8)
    @DisplayName("When: Delete request for different tenant, Then: Should not affect other tenant's data")
    void deleteRequest_whenDifferentTenant_shouldNotAffectOtherTenant() {
        // Given: Both tenants have requests
        long initialCountA = categorizationRequestRepository.findByTenantId(TENANT_A).size();

        // When: Tenant B deletes a request
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_B)
                .userId(USER_B)
                .correlationId(UUID.randomUUID().toString())
                .build());

        categorizationRequestRepository.delete(TENANT_B, tenantBRequestId);

        // Then: Tenant A's data should be unchanged
        RequestContextHolder.set(TenantContext.builder()
                .tenantId(TENANT_A)
                .userId(USER_A)
                .correlationId(UUID.randomUUID().toString())
                .build());

        long finalCountA = categorizationRequestRepository.findByTenantId(TENANT_A).size();
        assertThat(finalCountA).isEqualTo(initialCountA);

        RequestContextHolder.clear();
    }
}
