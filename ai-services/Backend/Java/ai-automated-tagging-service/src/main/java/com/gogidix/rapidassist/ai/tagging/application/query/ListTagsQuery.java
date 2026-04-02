package com.gogidix.rapidassist.ai.tagging.application.query;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to list tags with filters
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListTagsQuery {

    private String tenantId;
    private Tag.TagStatus status;
    private UUID categoryId;
    private String searchKeyword;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
