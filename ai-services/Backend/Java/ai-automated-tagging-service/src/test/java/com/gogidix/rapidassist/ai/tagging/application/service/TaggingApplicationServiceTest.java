package com.gogidix.rapidassist.ai.tagging.application.service;

import com.gogidix.rapidassist.ai.tagging.application.command.CreateTagCommand;
import com.gogidix.rapidassist.ai.tagging.application.dto.TagDto;
import com.gogidix.rapidassist.ai.tagging.application.mapper.ContentTagMapper;
import com.gogidix.rapidassist.ai.tagging.application.mapper.TagCategoryMapper;
import com.gogidix.rapidassist.ai.tagging.application.mapper.TagMapper;
import com.gogidix.rapidassist.ai.tagging.application.mapper.TaggingRuleMapper;
import com.gogidix.rapidassist.ai.tagging.application.mapper.TagSuggestionMapper;
import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import com.gogidix.rapidassist.ai.tagging.domain.repository.ContentTagRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagCategoryRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TaggingRuleRepositoryPort;
import com.gogidix.rapidassist.ai.tagging.domain.repository.TagSuggestionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaggingApplicationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tagging Application Service Tests")
class TaggingApplicationServiceTest {

    @Mock
    private TagRepositoryPort tagRepository;

    @Mock
    private TagCategoryRepositoryPort categoryRepository;

    @Mock
    private ContentTagRepositoryPort contentTagRepository;

    @Mock
    private TaggingRuleRepositoryPort ruleRepository;

    @Mock
    private TagSuggestionRepositoryPort suggestionRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private TagCategoryMapper categoryMapper;

    @Mock
    private ContentTagMapper contentTagMapper;

    @Mock
    private TaggingRuleMapper ruleMapper;

    @Mock
    private TagSuggestionMapper suggestionMapper;

    @InjectMocks
    private TaggingApplicationService taggingApplicationService;

    private CreateTagCommand createTagCommand;
    private Tag tag;
    private TagDto tagDto;

    @BeforeEach
    void setUp() {
        createTagCommand = CreateTagCommand.builder()
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items")
                .color("#FF0000")
                .createdBy("user-123")
                .build();

        tag = Tag.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items")
                .color("#FF0000")
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(0)
                .version(1L)
                .build();

        tagDto = TagDto.builder()
                .id(tag.getId())
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items")
                .color("#FF0000")
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(0)
                .version(1L)
                .build();
    }

    @Test
    @DisplayName("Should create tag successfully")
    void shouldCreateTagSuccessfully() {
        when(tagRepository.existsByTenantIdAndName("tenant-123", "Urgent")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toDto(tag)).thenReturn(tagDto);

        TagDto result = taggingApplicationService.createTag(createTagCommand);

        assertNotNull(result);
        assertEquals("Urgent", result.getName());
        assertEquals("tenant-123", result.getTenantId());
        verify(tagRepository, times(1)).save(any(Tag.class));
        verify(tagMapper, times(1)).toDto(tag);
    }

    @Test
    @DisplayName("Should throw exception when tag name already exists")
    void shouldThrowExceptionWhenTagNameExists() {
        when(tagRepository.existsByTenantIdAndName("tenant-123", "Urgent")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            taggingApplicationService.createTag(createTagCommand);
        });

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Should get tag by ID")
    void shouldGetTagById() {
        UUID tagId = tag.getId();
        when(tagRepository.findByTenantIdAndId("tenant-123", tagId)).thenReturn(Optional.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(tagDto);

        TagDto result = taggingApplicationService.getTag(
                com.gogidix.rapidassist.ai.tagging.application.query.GetTagQuery.builder()
                        .tenantId("tenant-123")
                        .tagId(tagId)
                        .build()
        );

        assertNotNull(result);
        assertEquals(tagId, result.getId());
        verify(tagRepository, times(1)).findByTenantIdAndId("tenant-123", tagId);
        verify(tagMapper, times(1)).toDto(tag);
    }

    @Test
    @DisplayName("Should throw exception when tag not found")
    void shouldThrowExceptionWhenTagNotFound() {
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findByTenantIdAndId("tenant-123", tagId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            taggingApplicationService.getTag(
                    com.gogidix.rapidassist.ai.tagging.application.query.GetTagQuery.builder()
                            .tenantId("tenant-123")
                            .tagId(tagId)
                            .build()
            );
        });
    }

    @Test
    @DisplayName("Should list tags for tenant")
    void shouldListTagsForTenant() {
        when(tagRepository.findByTenantId("tenant-123")).thenReturn(java.util.List.of(tag));
        when(tagMapper.toDtoList(java.util.List.of(tag))).thenReturn(java.util.List.of(tagDto));

        var result = taggingApplicationService.listTags(
                com.gogidix.rapidassist.ai.tagging.application.query.ListTagsQuery.builder()
                        .tenantId("tenant-123")
                        .build()
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Urgent", result.get(0).getName());
        verify(tagRepository, times(1)).findByTenantId("tenant-123");
        verify(tagMapper, times(1)).toDtoList(java.util.List.of(tag));
    }

    @Test
    @DisplayName("Should delete tag successfully")
    void shouldDeleteTagSuccessfully() {
        UUID tagId = tag.getId();
        when(tagRepository.findByTenantIdAndId("tenant-123", tagId)).thenReturn(Optional.of(tag));
        when(contentTagRepository.countByTenantIdAndTagId("tenant-123", tagId)).thenReturn(0L);

        taggingApplicationService.deleteTag("tenant-123", tagId);

        verify(tagRepository, times(1)).deleteByTenantIdAndId("tenant-123", tagId);
    }

    @Test
    @DisplayName("Should throw exception when deleting tag in use")
    void shouldThrowExceptionWhenDeletingTagInUse() {
        UUID tagId = tag.getId();
        when(tagRepository.findByTenantIdAndId("tenant-123", tagId)).thenReturn(Optional.of(tag));
        when(contentTagRepository.countByTenantIdAndTagId("tenant-123", tagId)).thenReturn(5L);

        assertThrows(IllegalStateException.class, () -> {
            taggingApplicationService.deleteTag("tenant-123", tagId);
        });

        verify(tagRepository, never()).deleteByTenantIdAndId(any(), any());
    }
}
