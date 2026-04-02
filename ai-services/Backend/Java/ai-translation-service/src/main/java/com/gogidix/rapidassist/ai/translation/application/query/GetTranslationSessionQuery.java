package com.gogidix.rapidassist.ai.translation.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a translation session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTranslationSessionQuery {

    private String tenantId;
    private UUID sessionId;
    private Boolean includeRequests;
}
