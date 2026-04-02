package com.gogidix.rapidassist.ai.tagging.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get tags for a specific content
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetContentTagsQuery {

    private String tenantId;
    private String contentId;
    private String contentType;
}
