package com.gogidix.rapidassist.ai.categorization.application.service;

import com.gogidix.rapidassist.ai.categorization.application.port.out.CategoryRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategoryTaxonomyRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.Category;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategoryTaxonomy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryManagementService {

    private final CategoryRepositoryPort categoryRepository;
    private final CategoryTaxonomyRepositoryPort taxonomyRepository;

    // Category operations

    public Category createRootCategory(String tenantId, String name, String code, String description) {
        log.info("Creating root category: {} for tenant: {}", name, tenantId);
        Category category = Category.createRoot(tenantId, name, code, description);
        return categoryRepository.save(tenantId, category);
    }

    public Category createChildCategory(String tenantId, String name, String code, String description, UUID parentId) {
        log.info("Creating child category: {} for parent: {} in tenant: {}", name, parentId, tenantId);
        
        Category parent = categoryRepository.findById(tenantId, parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + parentId));
        
        Category child = Category.createChild(tenantId, name, code, description, parentId, parent.getPath());
        Category savedChild = categoryRepository.save(tenantId, child);
        parent.addChild(child);
        categoryRepository.save(tenantId, parent);
        
        return savedChild;
    }

    public Category getCategory(String tenantId, UUID categoryId) {
        return categoryRepository.findById(tenantId, categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
    }

    public List<Category> getAllCategories(String tenantId) {
        return categoryRepository.findByTenantId(tenantId);
    }

    public List<Category> getRootCategories(String tenantId) {
        return categoryRepository.findRootCategories(tenantId);
    }

    public List<Category> getChildCategories(String tenantId, UUID parentId) {
        return categoryRepository.findChildren(tenantId, parentId);
    }

    public Category updateCategory(String tenantId, UUID categoryId, String name, String description) {
        Category category = getCategory(tenantId, categoryId);
        category.updateDetails(name, description);
        return categoryRepository.save(tenantId, category);
    }

    public void activateCategory(String tenantId, UUID categoryId) {
        Category category = getCategory(tenantId, categoryId);
        category.activate();
        categoryRepository.save(tenantId, category);
    }

    public void deactivateCategory(String tenantId, UUID categoryId) {
        Category category = getCategory(tenantId, categoryId);
        category.deactivate();
        categoryRepository.save(tenantId, category);
    }

    public void deleteCategory(String tenantId, UUID categoryId) {
        categoryRepository.delete(tenantId, categoryId);
    }

    // Taxonomy operations

    public CategoryTaxonomy createTaxonomy(String tenantId, String name, String code, String description, String contentType) {
        log.info("Creating taxonomy: {} for tenant: {}", name, tenantId);
        CategoryTaxonomy taxonomy = CategoryTaxonomy.create(tenantId, name, code, description, contentType);
        return taxonomyRepository.save(tenantId, taxonomy);
    }

    public CategoryTaxonomy getTaxonomy(String tenantId, UUID taxonomyId) {
        return taxonomyRepository.findById(tenantId, taxonomyId)
                .orElseThrow(() -> new IllegalArgumentException("Taxonomy not found: " + taxonomyId));
    }

    public List<CategoryTaxonomy> getAllTaxonomies(String tenantId) {
        return taxonomyRepository.findByTenantId(tenantId);
    }

    public List<CategoryTaxonomy> getTaxonomiesByContentType(String tenantId, String contentType) {
        return taxonomyRepository.findByContentType(tenantId, contentType);
    }

    public CategoryTaxonomy updateTaxonomy(String tenantId, UUID taxonomyId, String name, String description) {
        CategoryTaxonomy taxonomy = getTaxonomy(tenantId, taxonomyId);
        taxonomy.updateDetails(name, description);
        return taxonomyRepository.save(tenantId, taxonomy);
    }

    public void deleteTaxonomy(String tenantId, UUID taxonomyId) {
        taxonomyRepository.delete(tenantId, taxonomyId);
    }
}
