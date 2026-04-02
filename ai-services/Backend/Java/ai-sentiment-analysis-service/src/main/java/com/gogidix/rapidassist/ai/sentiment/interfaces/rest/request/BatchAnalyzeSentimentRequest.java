package com.gogidix.rapidassist.ai.sentiment.interfaces.rest.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST request to batch analyze sentiment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAnalyzeSentimentRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "Texts list cannot be empty")
    private List<String> texts;

    private String language;
    private String sourceType;
    private String sourceId;
    private Boolean includeEmotions;
    private Boolean includeAspects;
    private Map<String, Object> metadata;
}
