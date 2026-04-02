package com.gogidix.rapidassist.ai.categorization.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Category domain model.
 */
@DisplayName("Category Domain Model Tests")
class CategoryDomainModelTest {

    private final String tenantId = "tenant-123";
    private final UUID testId = UUID.randomUUID();

    @Test
    @DisplayName("Should create root Category using factory method")
    void shouldCreateRootCategory() {
        // When
        Category category = Category.createRoot(tenantId, "Technology", "TECH", "Technology related items");

        // Then
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(tenantId, category.getTenantId());
        assertEquals("Technology", category.getName());
        assertEquals("TECH", category.getCode());
        assertEquals("Technology related items", category.getDescription());
        assertEquals(CategoryStatus.ACTIVE, category.getStatus());
        assertEquals(0, category.getLevel());
        assertEquals("/TECH", category.getPath());
        assertNull(category.getParentCategoryId());
        assertTrue(category.isRoot());
        assertFalse(category.hasChildren());
    }

    @Test
    @DisplayName("Should create child Category using factory method")
    void shouldCreateChildCategory() {
        // Given
        UUID parentId = UUID.randomUUID();
        String parentPath = "/TECH";

        // When
        Category category = Category.createChild(tenantId, "Software", "SOFT", "Software items", parentId, parentPath);

        // Then
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(tenantId, category.getTenantId());
        assertEquals("Software", category.getName());
        assertEquals("SOFT", category.getCode());
        assertEquals(parentId, category.getParentCategoryId());
        assertEquals("/TECH/SOFT", category.getPath());
        assertFalse(category.isRoot());
    }

    @Test
    @DisplayName("Should add child category to parent")
    void shouldAddChildCategory() {
        // Given
        Category parent = Category.createRoot(tenantId, "Technology", "TECH", "Tech");
        Category child = Category.createChild(tenantId, "Software", "SOFT", "Software", parent.getId(), parent.getPath());

        // When
        parent.addChild(child);

        // Then
        assertTrue(parent.hasChildren());
        assertEquals(1, parent.getChildren().size());
        assertEquals(1, child.getLevel());
        assertEquals("/TECH/SOFT", child.getPath());
    }

    @Test
    @DisplayName("Should activate category")
    void shouldActivateCategory() {
        // Given
        Category category = Category.createRoot(tenantId, "Test", "TEST", "Test");
        category.deactivate();

        // When
        category.activate();

        // Then
        assertEquals(CategoryStatus.ACTIVE, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should deactivate category")
    void shouldDeactivateCategory() {
        // Given
        Category category = Category.createRoot(tenantId, "Test", "TEST", "Test");

        // When
        category.deactivate();

        // Then
        assertEquals(CategoryStatus.INACTIVE, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should archive category")
    void shouldArchiveCategory() {
        // Given
        Category category = Category.createRoot(tenantId, "Test", "TEST", "Test");

        // When
        category.archive();

        // Then
        assertEquals(CategoryStatus.ARCHIVED, category.getStatus());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update category details")
    void shouldUpdateCategoryDetails() {
        // Given
        Category category = Category.createRoot(tenantId, "Old Name", "OLD", "Old description");

        // When
        category.updateDetails("New Name", "New description");

        // Then
        assertEquals("New Name", category.getName());
        assertEquals("New description", category.getDescription());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should access metadata")
    void shouldAccessMetadata() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("color", "blue");
        metadata.put("priority", "high");

        Category category = Category.createRoot(tenantId, "Test", "TEST", "Test");
        category.setMetadata(metadata);

        // Then
        assertNotNull(category.getMetadata());
        assertEquals(2, category.getMetadata().size());
        assertEquals("blue", category.getMetadata().get("color"));
        assertEquals("high", category.getMetadata().get("priority"));
    }

    @Test
    @DisplayName("Should check if category is active")
    void shouldCheckIfCategoryIsActive() {
        // Given
        Category activeCategory = Category.createRoot(tenantId, "Active", "ACT", "Active");
        Category inactiveCategory = Category.createRoot(tenantId, "Inactive", "INACT", "Inactive");
        inactiveCategory.deactivate();

        // Then
        assertTrue(activeCategory.isActive());
        assertFalse(inactiveCategory.isActive());
    }

    @Test
    @DisplayName("Should check if category is root")
    void shouldCheckIfCategoryIsRoot() {
        // Given
        Category root = Category.createRoot(tenantId, "Root", "ROOT", "Root");
        Category child = Category.createChild(tenantId, "Child", "CHILD", "Child", root.getId(), root.getPath());

        // Then
        assertTrue(root.isRoot());
        assertFalse(child.isRoot());
    }

    @Test
    @DisplayName("Should handle different category statuses")
    void shouldHandleDifferentCategoryStatuses() {
        // Given
        Category active = Category.createRoot(tenantId, "Active", "ACT", "Active");
        Category inactive = Category.createRoot(tenantId, "Inactive", "INACT", "Inactive");
        inactive.deactivate();
        Category archived = Category.createRoot(tenantId, "Archived", "ARCH", "Archived");
        archived.archive();

        // Then
        assertEquals(CategoryStatus.ACTIVE, active.getStatus());
        assertEquals(CategoryStatus.INACTIVE, inactive.getStatus());
        assertEquals(CategoryStatus.ARCHIVED, archived.getStatus());

        // Verify enum values
        assertEquals(3, CategoryStatus.values().length);
    }

    @Test
    @DisplayName("Should set max depth correctly")
    void shouldSetMaxDepthCorrectly() {
        // Given
        CategoryTaxonomy taxonomy = CategoryTaxonomy.create(tenantId, "Test", "TEST", "Test", "article");

        // When
        taxonomy.setMaxDepth(10);

        // Then
        assertEquals(10, taxonomy.getMaxDepth());
    }

    @Test
    @DisplayName("Should set default max depth for invalid values")
    void shouldSetDefaultMaxDepthForInvalidValues() {
        // Given
        CategoryTaxonomy taxonomy = CategoryTaxonomy.create(tenantId, "Test", "TEST", "Test", "article");

        // When
        taxonomy.setMaxDepth(-1);
        assertEquals(5, taxonomy.getMaxDepth()); // Should default to 5

        taxonomy.setMaxDepth(0);
        assertEquals(5, taxonomy.getMaxDepth()); // Should default to 5

        taxonomy.setMaxDepth(15);
        assertEquals(15, taxonomy.getMaxDepth()); // Should accept valid value
    }

    @Test
    @DisplayName("Should update taxonomy configuration")
    void shouldUpdateTaxonomyConfiguration() {
        // Given
        CategoryTaxonomy taxonomy = CategoryTaxonomy.create(tenantId, "Test", "TEST", "Test", "article");

        // When
        taxonomy.updateConfiguration("key1", "value1");
        taxonomy.updateConfiguration("key2", 123);
        taxonomy.updateConfiguration("key3", true);

        // Then
        assertNotNull(taxonomy.getConfiguration());
        assertEquals(3, taxonomy.getConfiguration().size());
        assertEquals("value1", taxonomy.getConfiguration().get("key1"));
        assertEquals(123, taxonomy.getConfiguration().get("key2"));
        assertEquals(true, taxonomy.getConfiguration().get("key3"));
    }

    @Test
    @DisplayName("Should verify CategorizationResult fromRequest")
    void shouldVerifyCategorizationResultFromRequest() {
        // Given
        CategorizationRequest request = CategorizationRequest.create(tenantId, "content1", "article", "Test content", UUID.randomUUID());

        // When
        CategorizationResult result = CategorizationResult.fromRequest(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getTenantId(), result.getTenantId());
        assertEquals(request.getId(), result.getRequestId());
        assertEquals(request.getContentId(), result.getContentId());
        assertEquals(request.getContentType(), result.getContentType());
        assertEquals(request.getTaxonomyId(), result.getTaxonomyId());
        assertEquals(CategorizationStatus.PENDING, result.getStatus());
        assertNotNull(result.getPredictions());
        assertTrue(result.getPredictions().isEmpty());
    }

    @Test
    @DisplayName("Should add prediction to result")
    void shouldAddPredictionToResult() {
        // Given
        CategorizationRequest request = CategorizationRequest.create(tenantId, "content1", "article", "Test", UUID.randomUUID());
        CategorizationResult result = CategorizationResult.fromRequest(request);
        UUID categoryId = UUID.randomUUID();

        // When
        result.addPrediction(categoryId, "Technology", "/Tech", 0.85);

        // Then
        assertEquals(1, result.getPredictions().size());
        assertEquals(categoryId, result.getPredictions().get(0).getCategoryId());
        assertEquals("Technology", result.getPredictions().get(0).getCategoryName());
        assertEquals(0.85, result.getPredictions().get(0).getConfidence());
    }

    @Test
    @DisplayName("Should get high confidence predictions")
    void shouldGetHighConfidencePredictions() {
        // Given
        CategorizationRequest request = CategorizationRequest.create(tenantId, "content1", "article", "Test", UUID.randomUUID());
        CategorizationResult result = CategorizationResult.fromRequest(request);
        result.addPrediction(UUID.randomUUID(), "High", "/High", 0.85);
        result.addPrediction(UUID.randomUUID(), "Low", "/Low", 0.4);

        // When
        var highConfidence = result.getHighConfidencePredictions(0.7);

        // Then
        assertEquals(1, highConfidence.size());
        assertEquals("High", highConfidence.get(0).getCategoryName());
    }

    @Test
    @DisplayName("Should mark result as completed")
    void shouldMarkResultAsCompleted() {
        // Given
        CategorizationRequest request = CategorizationRequest.create(tenantId, "content1", "article", "Test", UUID.randomUUID());
        CategorizationResult result = CategorizationResult.fromRequest(request);

        // When
        result.markAsCompleted("model-v1.0", 150.5);

        // Then
        assertEquals(CategorizationStatus.COMPLETED, result.getStatus());
        assertEquals("model-v1.0", result.getModelVersion());
        assertEquals(150.5, result.getProcessingTimeMs());
        assertTrue(result.isSuccessful());
    }
}
