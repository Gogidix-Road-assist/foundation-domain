package com.gogidix.rapidassist.ai.search.optimization.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to rank search results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResultsCommand {

    private String tenantId;
    private UUID queryId;
    private String rankingAlgorithm;
    private Map<String, Object> parameters;
}
