package com.gogidix.rapidassist.ai.tagging.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a tag by ID
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTagQuery {

    private String tenantId;
    private UUID tagId;
}
