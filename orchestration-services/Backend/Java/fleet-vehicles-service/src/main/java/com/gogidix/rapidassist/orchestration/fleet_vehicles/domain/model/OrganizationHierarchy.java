package com.gogidix.rapidassist.orchestration.fleetorganization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Organization Hierarchy closure table for efficient hierarchical queries.
 * Enables efficient ancestor/descendant lookups without recursive queries.
 *
 * Closure table pattern:
 * - depth = 0: self-reference
 * - depth = 1: direct child
 * - depth = 2: grandchild
 * - etc.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "organization_hierarchy")
@CompoundIndex(name = "ancestor_depth_idx", def = "{'ancestorId': 1, 'depth': 1}")
@CompoundIndex(name = "descendant_depth_idx", def = "{'descendantId': 1, 'depth': 1}")
@CompoundIndex(name = "ancestor_descendant_depth_idx", def = "{'ancestorId': 1, 'descendantId': 1, 'depth': 1}", unique = true)
public class OrganizationHierarchy {

    @Id
    private String id;

    @Indexed
    private String ancestorId;

    @Indexed
    private String descendantId;

    @Indexed
    private Integer depth; // 0 = self, 1 = direct child, 2 = grandchild, etc.

    /**
     * Domain logic: Validate hierarchy entry
     */
    public void validate() {
        if (ancestorId == null || ancestorId.isBlank()) {
            throw new IllegalArgumentException("Ancestor ID is required");
        }
        if (descendantId == null || descendantId.isBlank()) {
            throw new IllegalArgumentException("Descendant ID is required");
        }
        if (depth == null || depth < 0) {
            throw new IllegalArgumentException("Depth must be non-negative");
        }
    }

    /**
     * Check if this is a self-reference
     */
    public boolean isSelfReference() {
        return depth == 0;
    }

    /**
     * Check if descendant is a direct child
     */
    public boolean isDirectChild() {
        return depth == 1;
    }
}
