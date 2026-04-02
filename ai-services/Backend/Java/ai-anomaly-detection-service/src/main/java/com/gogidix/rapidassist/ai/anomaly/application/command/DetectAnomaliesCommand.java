package com.gogidix.rapidassist.ai.anomaly.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Command to detect anomalies in data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectAnomaliesCommand {

    @NotBlank(message = "Data source is required")
    private String dataSource;

    @NotNull(message = "Data is required")
    private Map<String, Object> data;

    private String detectionMethod;

    private String tenantId;

    private String createdBy;
}
