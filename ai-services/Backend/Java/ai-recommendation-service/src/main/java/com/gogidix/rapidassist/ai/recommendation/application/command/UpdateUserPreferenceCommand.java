package com.gogidix.rapidassist.ai.recommendation.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to update user preference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPreferenceCommand {

    private String tenantId;
    private String userId;
    private String itemType;
    private String itemId;
    private String preferenceKey;
    private String preferenceValue;
    private Double scoreDelta;
    private String metadata;
}
