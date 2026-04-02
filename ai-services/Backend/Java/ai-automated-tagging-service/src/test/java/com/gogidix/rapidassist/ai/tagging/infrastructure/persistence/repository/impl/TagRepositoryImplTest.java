package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.impl;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagEntity;
import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository.SpringDataTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TagRepositoryImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tag Repository Implementation Tests")
class TagRepositoryImplTest {

    @Mock
    private SpringDataTagRepository springDataTagRepository;

    @InjectMocks
    private TagRepositoryImpl tagRepository;

    private Tag tag;
    private TagEntity tagEntity;

    @BeforeEach
    void setUp() {
        UUID tagId = UUID.randomUUID();
        tag = Tag.builder()
                .id(tagId)
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items")
                .color("#FF0000")
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(5)
                .version(1L)
                .build();

        tagEntity = TagEntity.builder()
                .id("mongo-id-123")
                .uuid(tagId)
                .tenantId("tenant-123")
                .name("Urgent")
                .description("Urgent items")
                .color("#FF0000")
                .status(Tag.TagStatus.ACTIVE)
                .usageCount(5)
                .version(1L)
                .build();
    }

    @Test
    @DisplayName("Should save tag")
    void shouldSaveTag() {
        when(springDataTagRepository.save(any(TagEntity.class))).thenReturn(tagEntity);

        Tag result = tagRepository.save(tag);

        assertNotNull(result);
        assertEquals(tag.getId(), result.getId());
        assertEquals("Urgent", result.getName());
        verify(springDataTagRepository, times(1)).save(any(TagEntity.class));
    }

    @Test
    @DisplayName("Should find tag by ID")
    void shouldFindTagById() {
        when(springDataTagRepository.findByUuid(tag.getId())).thenReturn(Optional.of(tagEntity));

        Optional<Tag> result = tagRepository.findById(tag.getId());

        assertTrue(result.isPresent());
        assertEquals(tag.getId(), result.get().getId());
        verify(springDataTagRepository, times(1)).findByUuid(tag.getId());
    }

    @Test
    @DisplayName("Should find tag by tenant ID and tag ID")
    void shouldFindTagByTenantIdAndId() {
        when(springDataTagRepository.findByTenantIdAndUuid("tenant-123", tag.getId())).thenReturn(tagEntity);

        Optional<Tag> result = tagRepository.findByTenantIdAndId("tenant-123", tag.getId());

        assertTrue(result.isPresent());
        assertEquals(tag.getId(), result.get().getId());
        verify(springDataTagRepository, times(1)).findByTenantIdAndUuid("tenant-123", tag.getId());
    }

    @Test
    @DisplayName("Should find tags by tenant ID")
    void shouldFindTagsByTenantId() {
        when(springDataTagRepository.findByTenantId("tenant-123")).thenReturn(List.of(tagEntity));

        List<Tag> result = tagRepository.findByTenantId("tenant-123");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Urgent", result.get(0).getName());
        verify(springDataTagRepository, times(1)).findByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should find tags by tenant ID and status")
    void shouldFindTagsByTenantIdAndStatus() {
        when(springDataTagRepository.findByTenantIdAndStatus("tenant-123", Tag.TagStatus.ACTIVE))
                .thenReturn(List.of(tagEntity));

        List<Tag> result = tagRepository.findByTenantIdAndStatus("tenant-123", Tag.TagStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Tag.TagStatus.ACTIVE, result.get(0).getStatus());
        verify(springDataTagRepository, times(1)).findByTenantIdAndStatus("tenant-123", Tag.TagStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should check if tag exists by tenant ID and name")
    void shouldCheckTagExistsByTenantIdAndName() {
        when(springDataTagRepository.existsByTenantIdAndName("tenant-123", "Urgent")).thenReturn(true);

        boolean result = tagRepository.existsByTenantIdAndName("tenant-123", "Urgent");

        assertTrue(result);
        verify(springDataTagRepository, times(1)).existsByTenantIdAndName("tenant-123", "Urgent");
    }

    @Test
    @DisplayName("Should delete tag by tenant ID and tag ID")
    void shouldDeleteTagByTenantIdAndId() {
        doNothing().when(springDataTagRepository).deleteByTenantIdAndUuid("tenant-123", tag.getId());

        tagRepository.deleteByTenantIdAndId("tenant-123", tag.getId());

        verify(springDataTagRepository, times(1)).deleteByTenantIdAndUuid("tenant-123", tag.getId());
    }

    @Test
    @DisplayName("Should count tags by tenant ID")
    void shouldCountTagsByTenantId() {
        when(springDataTagRepository.countByTenantId("tenant-123")).thenReturn(5L);

        long result = tagRepository.countByTenantId("tenant-123");

        assertEquals(5L, result);
        verify(springDataTagRepository, times(1)).countByTenantId("tenant-123");
    }

    @Test
    @DisplayName("Should handle null entity when converting to domain")
    void shouldHandleNullEntityWhenConverting() {
        UUID unknownId = UUID.randomUUID();
        when(springDataTagRepository.findByUuid(unknownId)).thenReturn(Optional.empty());

        Optional<Tag> result = tagRepository.findById(unknownId);

        assertFalse(result.isPresent());
    }
}
