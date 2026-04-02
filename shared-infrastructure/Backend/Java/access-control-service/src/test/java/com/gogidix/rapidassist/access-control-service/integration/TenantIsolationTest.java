package com.gogidix.rapidassist.access.control.service.integration;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import com.gogidix.rapidassist.access.control.service.domain.model.Subject;
import com.gogidix.rapidassist.access.control.service.domain.repository.PermissionRepository;
import com.gogidix.rapidassist.access.control.service.domain.repository.SubjectRepository;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Test: TenantIsolationTest
 *
 * CRITICAL TEST: Verifies tenant isolation works correctly.
 * Ensures that Tenant A cannot access Tenant B's data.
 *
 * This test MUST pass before the service can be deployed to production.
 */
@SpringBootTest
@Testcontainers
@Disabled("Docker required - run in cloud CI/CD")
@DisplayName("Tenant Isolation Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantIsolationTest {

    @Container
    static final MongoDBContainer mongoDB = new MongoDBContainer(
            DockerImageName.parse("mongo:6"))
            .withReuse(true);

    @Container
    @SuppressWarnings("rawtypes")
    static final GenericContainer redis = new GenericContainer(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private final String tenantA = "tenant-a";
    private final String tenantB = "tenant-b";

    @BeforeEach
    void setUp() {
        // Clear context before each test
        RequestContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B permissions")
    void whenTenantAQueries_shouldOnlySeeTenantAPermissions() {
        // Given: Set context to Tenant A
        RequestContext contextA = RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(contextA);

        // Create permission for Tenant A
        Permission permissionA = Permission.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantA)
                .subjectId("user-a")
                .subjectType("USER")
                .resource("/api/v1/data")
                .action("READ")
                .effect("ALLOW")
                .grantedAt(Instant.now())
                .active(true)
                .build();
        permissionRepository.save(permissionA);

        // Create permission for Tenant B (with different context)
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantB)
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build());

        Permission permissionB = Permission.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantB)
                .subjectId("user-b")
                .subjectType("USER")
                .resource("/api/v1/data")
                .action("READ")
                .effect("ALLOW")
                .grantedAt(Instant.now())
                .active(true)
                .build();
        permissionRepository.save(permissionB);

        // When: Tenant A queries their permissions
        RequestContextHolder.set(contextA);
        List<Permission> tenantAPermissions = permissionRepository.findBySubjectId("user-a", tenantA);

        // Then: Should only see Tenant A's permission
        assertThat(tenantAPermissions).hasSize(1);
        assertThat(tenantAPermissions.get(0).getTenantId()).isEqualTo(tenantA);
        assertThat(tenantAPermissions).noneMatch(p -> p.getTenantId().equals(tenantB));
    }

    @Test
    @DisplayName("findById should not return other tenant's permission")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Create permission for Tenant A
        RequestContext contextA = RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(contextA);

        Permission permission = Permission.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantA)
                .subjectId("user-a")
                .subjectType("USER")
                .resource("/api/v1/data")
                .action("READ")
                .effect("ALLOW")
                .grantedAt(Instant.now())
                .active(true)
                .build();
        Permission saved = permissionRepository.save(permission);
        String permissionId = saved.getId();

        // When: Tenant B tries to access the same permission
        RequestContext contextB = RequestContext.builder()
                .tenantId(tenantB)
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();
        RequestContextHolder.set(contextB);

        // Then: Should return empty
        var result = permissionRepository.findById(permissionId, tenantB);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteById should not affect other tenant's permission")
    void deleteById_whenDifferentTenant_shouldNotDelete() {
        // Given: Create permission for Tenant A
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build());

        Permission permission = Permission.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantA)
                .subjectId("user-a")
                .subjectType("USER")
                .resource("/api/v1/data")
                .action("READ")
                .effect("ALLOW")
                .grantedAt(Instant.now())
                .active(true)
                .build();
        Permission saved = permissionRepository.save(permission);
        String permissionId = saved.getId();

        // When: Tenant B tries to delete the permission
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantB)
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build());

        boolean deleted = permissionRepository.deleteById(permissionId, tenantB);

        // Then: Deletion should fail
        assertThat(deleted).isFalse();

        // And: Permission should still exist for Tenant A
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build());

        var stillExists = permissionRepository.findById(permissionId, tenantA);
        assertThat(stillExists).isPresent();
    }

    @Test
    @DisplayName("Subject queries are tenant isolated")
    void subjectQueriesAreTenantIsolated() {
        // Given: Create subjects for both tenants
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build());

        Subject subjectA = Subject.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantA)
                .subjectType("USER")
                .subjectKey("user-a")
                .displayName("User A")
                .createdAt(Instant.now())
                .active(true)
                .build();
        subjectRepository.save(subjectA);

        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantB)
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build());

        Subject subjectB = Subject.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantB)
                .subjectType("USER")
                .subjectKey("user-b")
                .displayName("User B")
                .createdAt(Instant.now())
                .active(true)
                .build();
        subjectRepository.save(subjectB);

        // When: Tenant A queries subjects
        RequestContextHolder.set(RequestContext.builder()
                .tenantId(tenantA)
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build());

        List<Subject> tenantASubjects = subjectRepository.findByTenantId(tenantA);

        // Then: Should only see Tenant A's subject
        assertThat(tenantASubjects).hasSize(1);
        assertThat(tenantASubjects.get(0).getTenantId()).isEqualTo(tenantA);
    }
}
