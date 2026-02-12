package com.gogidix.rapidassist.ai.analytics.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to execute a data query
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteQueryCommand {

    private String tenantId;
    private String query;
    private String dataSource;
    private Map<String, Object> parameters;
    private Map<String, Object> filters;
    private List<String> groupBy;
    private List<String> orderBy;
    private Integer limit;
    private Integer offset;
    private String executedBy;
    private Boolean isCached;
}
