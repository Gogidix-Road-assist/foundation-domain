package com.gogidix.rapidassist.ai.analytics.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST request to execute a data query
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteQueryRequest {

    @NotBlank(message = "Query is required")
    private String query;

    @NotBlank(message = "Data source is required")
    private String dataSource;

    private Map<String, Object> parameters;

    private Map<String, Object> filters;

    private List<String> groupBy;

    private List<String> orderBy;

    private Integer limit;

    private Integer offset;

    private Boolean isCached;
}
