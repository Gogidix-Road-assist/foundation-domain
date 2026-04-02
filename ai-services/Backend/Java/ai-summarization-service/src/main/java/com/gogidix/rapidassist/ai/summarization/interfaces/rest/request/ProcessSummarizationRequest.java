package com.gogidix.rapidassist.ai.summarization.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSummarizationRequest {
    private String requestId;
}
