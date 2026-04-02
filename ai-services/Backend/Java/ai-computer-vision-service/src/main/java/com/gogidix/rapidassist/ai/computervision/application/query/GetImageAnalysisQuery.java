package com.gogidix.rapidassist.ai.computervision.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get image analysis by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetImageAnalysisQuery {

    private UUID id;
    private String tenantId;
}
