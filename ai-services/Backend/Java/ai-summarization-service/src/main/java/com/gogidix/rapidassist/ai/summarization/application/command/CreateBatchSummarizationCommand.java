package com.gogidix.rapidassist.ai.summarization.application.command;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationType;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryLength;
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
public class CreateBatchSummarizationCommand {
    private String tenantId;
    private String jobId;
    private SummarizationType summarizationType;
    private SummaryLength summaryLength;
    private List<String> sourceTexts;
    private List<String> documentUrls;
    private Map<String, Object> options;
    private String createdBy;
}
