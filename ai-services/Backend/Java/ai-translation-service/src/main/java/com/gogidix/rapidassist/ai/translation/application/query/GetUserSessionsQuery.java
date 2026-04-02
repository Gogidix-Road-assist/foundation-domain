package com.gogidix.rapidassist.ai.translation.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get user sessions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserSessionsQuery {

    private String tenantId;
    private String userId;
    private String status;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
