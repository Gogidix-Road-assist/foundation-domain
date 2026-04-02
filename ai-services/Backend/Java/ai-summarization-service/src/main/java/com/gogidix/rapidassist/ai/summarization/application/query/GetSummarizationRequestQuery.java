package com.gogidix.rapidassist.ai.summarization.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSummarizationRequestQuery {
    private String tenantId;
    private UUID requestId;
}
