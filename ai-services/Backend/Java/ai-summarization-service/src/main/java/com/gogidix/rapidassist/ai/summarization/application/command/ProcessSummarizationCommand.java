package com.gogidix.rapidassist.ai.summarization.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSummarizationCommand {
    private String tenantId;
    private UUID requestId;
}
