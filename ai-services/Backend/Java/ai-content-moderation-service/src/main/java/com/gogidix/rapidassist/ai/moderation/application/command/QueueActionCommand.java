package com.gogidix.rapidassist.ai.moderation.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command for queue actions (approve/reject)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueActionCommand {

    @NotBlank(message = "Reviewer ID is required")
    private String reviewerId;

    private String reviewerName;

    private String notes;

    private String reason;
}
