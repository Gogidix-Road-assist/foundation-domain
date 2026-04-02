package com.gogidix.rapidassist.ai.computervision.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get available computer vision models
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAvailableModelsQuery {

    private String modelType;
}
