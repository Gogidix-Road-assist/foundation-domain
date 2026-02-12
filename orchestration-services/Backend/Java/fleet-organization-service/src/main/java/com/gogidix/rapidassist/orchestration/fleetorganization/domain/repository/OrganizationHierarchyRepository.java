package com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.OrganizationHierarchy;

import java.util.List;

/**
 * Domain repository port for OrganizationHierarchy closure table.
 * This is the interface that defines the contract for hierarchy operations.
 * Implemented by infrastructure layer (MongoDB).
 */
public interface OrganizationHierarchyRepository {

    /**
     * Save hierarchy entry
     */
    OrganizationHierarchy save(OrganizationHierarchy hierarchy);

    /**
     * Save all hierarchy entries (batch operation)
     */
    Iterable<OrganizationHierarchy> saveAll(List<OrganizationHierarchy> hierarchies);

    /**
     * Find all ancestors of a node (including self)
     */
    List<OrganizationHierarchy> findByDescendantId(String descendantId);

    /**
     * Find all descendants of a node (including self)
     */
    List<OrganizationHierarchy> findByAncestorId(String ancestorId);

    /**
     * Find direct children (depth = 1)
     */
    List<OrganizationHierarchy> findByAncestorIdAndDepth(String ancestorId, int depth);

    /**
     * Find specific ancestor-descendant relationship
     */
    OrganizationHierarchy findByAncestorIdAndDescendantIdAndDepth(
        String ancestorId, String descendantId, int depth
    );

    /**
     * Find all descendants at specific depth
     */
    List<OrganizationHierarchy> findByAncestorIdAndDepthGreaterThan(
        String ancestorId, int depth
    );

    /**
     * Delete all hierarchy entries for a node
     */
    void deleteByAncestorId(String ancestorId);

    /**
     * Delete all hierarchy entries for a node
     */
    void deleteByDescendantId(String descendantId);

    /**
     * Delete specific ancestor-descendant relationship
     */
    void deleteByAncestorIdAndDescendantId(String ancestorId, String descendantId);

    /**
     * Delete all hierarchy entries for a subtree
     */
    void deleteByDescendantIdIn(List<String> descendantIds);

    /**
     * Check if ancestor relationship exists
     */
    boolean existsByAncestorIdAndDescendantId(String ancestorId, String descendantId);

    /**
     * Get depth of relationship
     */
    Integer getDepth(String ancestorId, String descendantId);

    /**
     * Delete all entries and rebuild (for hierarchy updates)
     */
    void deleteAll();
}
