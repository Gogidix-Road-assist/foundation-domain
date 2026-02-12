package com.gogidix.rapidassist.ai.summarization.interfaces.rest.request;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationType;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryLength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSummarizationRequestRequest {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotNull(message = "Summarization type is required")
    private SummarizationType summarizationType;

    @NotNull(message = "Summary length is required")
    private SummaryLength summaryLength;

    @NotEmpty(message = "At least one source text is required")
    private List<String> sourceTexts;

    private List<String> documentUrls;

    private Map<String, Object> options;
}
