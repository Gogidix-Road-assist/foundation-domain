package com.gogidix.rapidassist.ai.tagging.integration;

import com.gogidix.rapidassist.ai.tagging.application.command.CreateTagCommand;
import com.gogidix.rapidassist.ai.tagging.application.dto.TagDto;
import com.gogidix.rapidassist.ai.tagging.application.service.TagCommandService;
import com.gogidix.rapidassist.ai.tagging.application.service.TagQueryService;
import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.infrastructure.tenant.RequestContext;
import com.gogidix.rapidassist.ai.tagging.infrastructure.tenant.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for ALL services before production
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class TenantIsolationTest {

    @Autowired
    private TagRepositoryPort repository;

    @Autowired
    private TagCommandService commandService;

    @Autowired
    private TagQueryService queryService;

    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";
    private static final String USER_A = "user-a";
    private static final String USER_B = "user-b";

    @BeforeEach
    void setUp() {
        // Clear context before each test
        RequestContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        // Clear context after each test
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Tenant A and Tenant B contexts
        RequestContext contextA = RequestContext.builder()
            .tenantId(TENANT_A)
            .userId(USER_A)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContext contextB = RequestContext.builder()
            .tenantId(TENANT_B)
            .userId(USER_B)
            .correlationId(UUID.randomUUID().toString())
            .build();

        // When: Create tags for both tenants
        RequestContextHolder.set(contextA);
        CreateTagCommand commandA = new CreateTagCommand(
            TENANT_A,
            "Tag A",
            "Description A",
            null,
            "#FF0000",
            Tag.TagStatus.ACTIVE.name()
        );
        TagDto tagA = commandService.createTag(commandA);

        RequestContextHolder.set(contextB);
        CreateTagCommand commandB = new CreateTagCommand(
            TENANT_B,
            "Tag B",
            "Description B",
            null,
            "#00FF00",
            Tag.TagStatus.ACTIVE.name()
        );
        TagDto tagB = commandService.createTag(commandB);

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(contextA);
        List<TagDto> tenantATags = queryService.findByTenantId(TENANT_A);

        log.info("Tenant A tags count: {}", tenantATags.size());
        assertThat(tenantATags).hasSize(1);
        assertThat(tenantATags.get(0).getTenantId()).isEqualTo(TENANT_A);
        assertThat(tenantATags.get(0).getName()).isEqualTo("Tag A");

        // Then: Tenant B should NOT see Tenant A's data
        RequestContextHolder.set(contextB);
        List<TagDto> tenantBTags = queryService.findByTenantId(TENANT_B);

        log.info("Tenant B tags count: {}", tenantBTags.size());
        assertThat(tenantBTags).hasSize(1);
        assertThat(tenantBTags.get(0).getTenantId()).isEqualTo(TENANT_B);
        assertThat(tenantBTags.get(0).getName()).isEqualTo("Tag B");

        // Verify cross-tenant isolation
        assertThat(tenantATags).doesNotContain(tagB);
        assertThat(tenantBTags).doesNotContain(tagA);
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        RequestContext contextA = RequestContext.builder()
            .tenantId(TENANT_A)
            .userId(USER_A)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(contextA);
        TagDto tagA = commandService.createTag(new CreateTagCommand(
            TENANT_A,
            "Tag A",
            "Description",
            null,
            "#FF0000",
            Tag.TagStatus.ACTIVE.name()
        ));
        UUID tagId = tagA.getId();

        // When: Tenant B tries to access same entity
        RequestContext contextB = RequestContext.builder()
            .tenantId(TENANT_B)
            .userId(USER_B)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(contextB);
        var result = queryService.findByTenantIdAndId(TENANT_B, tagId);

        // Then: Result should be empty (tenant B cannot access tenant A's tag)
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Multiple tenants can have tags with same name")
    void whenMultipleTenantsCreateTagsWithSameName_bothShouldSucceed() {
        // Given: Two tenants want to create tags with same name
        RequestContext contextA = RequestContext.builder()
            .tenantId(TENANT_A)
            .userId(USER_A)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContext contextB = RequestContext.builder()
            .tenantId(TENANT_B)
            .userId(USER_B)
            .correlationId(UUID.randomUUID().toString())
            .build();

        // When: Both create tags with same name
        RequestContextHolder.set(contextA);
        TagDto tagA = commandService.createTag(new CreateTagCommand(
            TENANT_A,
            "priority",
            "High priority tag",
            null,
            "#FF0000",
            Tag.TagStatus.ACTIVE.name()
        ));

        RequestContextHolder.set(contextB);
        TagDto tagB = commandService.createTag(new CreateTagCommand(
            TENANT_B,
            "priority",
            "High priority tag",
            null,
            "#00FF00",
            Tag.TagStatus.ACTIVE.name()
        ));

        // Then: Both should succeed with different IDs
        assertThat(tagA.getId()).isNotNull();
        assertThat(tagB.getId()).isNotNull();
        assertThat(tagA.getId()).isNotEqualTo(tagB.getId());

        // Verify each tenant sees only their own tag
        RequestContextHolder.set(contextA);
        List<TagDto> tenantATags = queryService.findByTenantId(TENANT_A);
        assertThat(tenantATags).hasSize(1);
        assertThat(tenantATags.get(0).getName()).isEqualTo("priority");

        RequestContextHolder.set(contextB);
        List<TagDto> tenantBTags = queryService.findByTenantId(TENANT_B);
        assertThat(tenantBTags).hasSize(1);
        assertThat(tenantBTags.get(0).getName()).isEqualTo("priority");
    }

    @Test
    @DisplayName("Delete operation respects tenant boundaries")
    void whenDeletingTag_shouldOnlyDeleteFromCurrentTenant() {
        // Given: Both tenants have tags
        RequestContext contextA = RequestContext.builder()
            .tenantId(TENANT_A)
            .userId(USER_A)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContext contextB = RequestContext.builder()
            .tenantId(TENANT_B)
            .userId(USER_B)
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(contextA);
        TagDto tagA = commandService.createTag(new CreateTagCommand(
            TENANT_A,
            "Tag A",
            "Description",
            null,
            "#FF0000",
            Tag.TagStatus.ACTIVE.name()
        ));

        RequestContextHolder.set(contextB);
        TagDto tagB = commandService.createTag(new CreateTagCommand(
            TENANT_B,
            "Tag B",
            "Description",
            null,
            "#00FF00",
            Tag.TagStatus.ACTIVE.name()
        ));

        // When: Tenant A deletes all their tags
        RequestContextHolder.set(contextA);
        List<TagDto> tenantATagsBefore = queryService.findByTenantId(TENANT_A);
        UUID tagAToDelete = tenantATagsBefore.get(0).getId();

        commandService.deleteTag(TENANT_A, tagAToDelete);

        // Then: Tenant A should have no tags
        List<TagDto> tenantATagsAfter = queryService.findByTenantId(TENANT_A);
        assertThat(tenantATagsAfter).isEmpty();

        // Then: Tenant B's tags should still exist
        RequestContextHolder.set(contextB);
        List<TagDto> tenantBTagsAfter = queryService.findByTenantId(TENANT_B);
        assertThat(tenantBTagsAfter).hasSize(1);
        assertThat(tenantBTagsAfter.get(0).getName()).isEqualTo("Tag B");
    }
}
