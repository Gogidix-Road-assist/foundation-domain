package com.gogidix.rapidassist.orchestration.fleetorganization.integration;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.OrganizationResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.service.OrganizationService;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.OrganizationRepository;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for service to be production-ready
 */
@SpringBootTest
class TenantIsolationTest {

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    private OrganizationService service;

    private static final String TENANT_A = "tenant-a-test";
    private static final String TENANT_B = "tenant-b-test";

    @BeforeEach
    void setUp() {
        // Clean up before tests
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        cleanupTestData();
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B organizations via repository")
    void whenTenantAQueries_shouldOnlySeeTenantAOrganizations() {
        // Given: Tenant A and Tenant B contexts
        RequestContext contextA = RequestContext.builder()
                .tenantId(TENANT_A)
                .correlationId("test-correlation-a")
                .build();

        RequestContext contextB = RequestContext.builder()
                .tenantId(TENANT_B)
                .correlationId("test-correlation-b")
                .build();

        // When: Create organizations for both tenants
        RequestContextHolder.set(contextA);
        Organization orgA = repository.save(Organization.builder()
                .organizationId("org-a-001")
                .name("Tenant A Organization")
                .organizationType(Organization.OrganizationType.ROOT)
                .level(0)
                .path("/org-a-001")
                .tenantId(TENANT_A)
                .isActive(true)
                .build());

        RequestContextHolder.set(contextB);
        Organization orgB = repository.save(Organization.builder()
                .organizationId("org-b-001")
                .name("Tenant B Organization")
                .organizationType(Organization.OrganizationType.ROOT)
                .level(0)
                .path("/org-b-001")
                .tenantId(TENANT_B)
                .isActive(true)
                .build());

        // Then: Tenant A should NOT see Tenant B's organizations
        RequestContextHolder.set(contextA);
        List<Organization> tenantAOrgs = repository.findByTenantId(TENANT_A);

        assertThat(tenantAOrgs).hasSize(1);
        assertThat(tenantAOrgs.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAOrgs.get(0).getOrganizationId()).isEqualTo("org-a-001");

        // Then: Tenant A should not be able to find Tenant B's org by ID
        var result = repository.findByIdAndTenantId("org-b-001", TENANT_A);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Tenant isolation via service layer")
    void whenTenantAUsesService_shouldOnlySeeTenantAOrganizations() {
        // Given: Two tenant contexts
        RequestContext contextA = RequestContext.builder()
                .tenantId(TENANT_A)
                .correlationId("test-correlation-a")
                .build();

        RequestContext contextB = RequestContext.builder()
                .tenantId(TENANT_B)
                .correlationId("test-correlation-b")
                .build();

        // When: Create organizations via service
        RequestContextHolder.set(contextA);
        CreateOrganizationRequestDto requestA = CreateOrganizationRequestDto.builder()
                .name("Tenant A Org")
                .organizationType(Organization.OrganizationType.ROOT)
                .build();
        OrganizationResponseDto responseA = service.createOrganization(requestA);

        RequestContextHolder.set(contextB);
        CreateOrganizationRequestDto requestB = CreateOrganizationRequestDto.builder()
                .name("Tenant B Org")
                .organizationType(Organization.OrganizationType.ROOT)
                .build();
        OrganizationResponseDto responseB = service.createOrganization(requestB);

        // Then: Tenant A should only see their own org
        RequestContextHolder.set(contextA);
        List<OrganizationResponseDto> tenantAOrgs = service.getAllOrganizations();

        assertThat(tenantAOrgs).hasSize(1);
        assertThat(tenantAOrgs.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAOrgs.get(0).getName()).isEqualTo("Tenant A Org");
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Organization exists for Tenant A
        RequestContext contextA = RequestContext.builder()
                .tenantId(TENANT_A)
                .correlationId("test-a")
                .build();

        RequestContextHolder.set(contextA);
        Organization org = repository.save(Organization.builder()
                .organizationId("org-secret")
                .name("Secret Organization")
                .organizationType(Organization.OrganizationType.DIVISION)
                .level(1)
                .path("/org-root/org-secret")
                .tenantId(TENANT_A)
                .isActive(true)
                .build());

        String orgId = org.getOrganizationId();

        // When: Tenant B tries to access the organization
        RequestContext contextB = RequestContext.builder()
                .tenantId(TENANT_B)
                .correlationId("test-b")
                .build();

        RequestContextHolder.set(contextB);
        var result = repository.findByIdAndTenantId(orgId, TENANT_B);

        // Then: Result should be empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Delete operation respects tenant boundaries")
    void delete_whenDifferentTenant_shouldNotDelete() {
        // Given: Organization exists for Tenant A
        RequestContext contextA = RequestContext.builder()
                .tenantId(TENANT_A)
                .correlationId("test-a")
                .build();

        RequestContextHolder.set(contextA);
        Organization org = repository.save(Organization.builder()
                .organizationId("org-protected")
                .name("Protected Organization")
                .organizationType(Organization.OrganizationType.DIVISION)
                .level(1)
                .path("/org-root/org-protected")
                .tenantId(TENANT_A)
                .isActive(true)
                .build());

        String orgId = org.getOrganizationId();

        // When: Tenant B tries to delete the organization
        RequestContext contextB = RequestContext.builder()
                .tenantId(TENANT_B)
                .correlationId("test-b")
                .build();

        RequestContextHolder.set(contextB);
        repository.deleteByIdAndTenantId(orgId, TENANT_B);

        // Then: Organization should still exist for Tenant A
        RequestContextHolder.set(contextA);
        var result = repository.findByIdAndTenantId(orgId, TENANT_A);
        assertThat(result).isPresent();
        assertThat(result.get().getIsActive()).isTrue(); // Not deleted
    }

    private void cleanupTestData() {
        try {
            // Clean up Tenant A data
            List<Organization> orgsA = repository.findByTenantId(TENANT_A);
            orgsA.forEach(org -> repository.deleteByIdAndTenantId(org.getOrganizationId(), TENANT_A));

            // Clean up Tenant B data
            List<Organization> orgsB = repository.findByTenantId(TENANT_B);
            orgsB.forEach(org -> repository.deleteByIdAndTenantId(org.getOrganizationId(), TENANT_B));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
