package com.gogidix.rapidassist.ai.search.optimization.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to optimize a search query.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizeQueryCommand {

    private String tenantId;
    private String userId;
    private String originalQuery;
    private Map<String, Object> context;
    private Map<String, Object> metadata;
}
