package com.gogidix.rapidassist.ai.categorization.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Category domain model.
 */
class CategoryTest {

    @Test
    void testCategoryBuilder() {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant1")
                .code("VEHICLE")
                .name("Vehicle")
                .description("Vehicle related category")
                .level(1)
                .weight(10)
                .status(CategoryStatus.ACTIVE)
                .path("/VEHICLE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .children(new ArrayList<>())
                .metadata(new HashMap<>())
                .build();

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("VEHICLE", category.getCode());
        assertEquals("Vehicle", category.getName());
        assertEquals(1, category.getLevel());
        assertEquals(10, category.getWeight());
        assertEquals(CategoryStatus.ACTIVE, category.getStatus());
        assertNotNull(category.getChildren());
    }

    @Test
    void testCreateRootCategory() {
        Category category = Category.createRoot("tenant1", "Technology", "TECH", "Technology items");

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("tenant1", category.getTenantId());
        assertEquals("Technology", category.getName());
        assertEquals("TECH", category.getCode());
        assertEquals("Technology items", category.getDescription());
        assertEquals(CategoryStatus.ACTIVE, category.getStatus());
        assertEquals(0, category.getLevel());
        assertEquals("/TECH", category.getPath());
        assertNull(category.getParentCategoryId());
        assertTrue(category.isRoot());
        assertFalse(category.hasChildren());
    }

    @Test
    void testCreateChildCategory() {
        UUID parentId = UUID.randomUUID();
        Category category = Category.createChild("tenant1", "Software", "SOFT", "Software items", parentId, "/TECH");

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("tenant1", category.getTenantId());
        assertEquals("Software", category.getName());
        assertEquals("SOFT", category.getCode());
        assertEquals(parentId, category.getParentCategoryId());
        assertEquals("/TECH/SOFT", category.getPath());
        assertFalse(category.isRoot());
    }

    @Test
    void testAddChild() {
        Category parent = Category.createRoot("tenant1", "Tech", "TECH", "Tech");
        Category child = Category.createChild("tenant1", "Soft", "SOFT", "Soft", parent.getId(), parent.getPath());

        parent.addChild(child);

        assertTrue(parent.hasChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(1, child.getLevel());
        assertEquals("/TECH/SOFT", child.getPath());
    }

    @Test
    void testActivate() {
        Category category = Category.createRoot("tenant1", "Test", "TEST", "Test");
        category.deactivate();

        category.activate();

        assertEquals(CategoryStatus.ACTIVE, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testDeactivate() {
        Category category = Category.createRoot("tenant1", "Test", "TEST", "Test");

        category.deactivate();

        assertEquals(CategoryStatus.INACTIVE, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testArchive() {
        Category category = Category.createRoot("tenant1", "Test", "TEST", "Test");

        category.archive();

        assertEquals(CategoryStatus.ARCHIVED, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testIsActive() {
        Category active = Category.createRoot("tenant1", "Active", "ACT", "Active");
        Category inactive = Category.createRoot("tenant1", "Inactive", "INACT", "Inactive");
        inactive.deactivate();

        assertTrue(active.isActive());
        assertFalse(inactive.isActive());
    }

    @Test
    void testIsRoot() {
        Category root = Category.createRoot("tenant1", "Root", "ROOT", "Root");
        Category child = Category.createChild("tenant1", "Child", "CHILD", "Child", root.getId(), root.getPath());

        assertTrue(root.isRoot());
        assertFalse(child.isRoot());
    }

    @Test
    void testHasChildren() {
        Category parent = Category.createRoot("tenant1", "Parent", "PARENT", "Parent");

        assertFalse(parent.hasChildren());

        Category child = Category.createChild("tenant1", "Child", "CHILD", "Child", parent.getId(), parent.getPath());
        parent.addChild(child);

        assertTrue(parent.hasChildren());
    }

    @Test
    void testUpdateDetails() {
        Category category = Category.createRoot("tenant1", "Old", "OLD", "Old description");

        category.updateDetails("New Name", "New description");

        assertEquals("New Name", category.getName());
        assertEquals("New description", category.getDescription());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    void testSetMetadata() {
        Category category = Category.createRoot("tenant1", "Test", "TEST", "Test");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);

        category.setMetadata(metadata);

        assertNotNull(category.getMetadata());
        assertEquals(2, category.getMetadata().size());
        assertEquals("value1", category.getMetadata().get("key1"));
        assertEquals(123, category.getMetadata().get("key2"));
    }
}
