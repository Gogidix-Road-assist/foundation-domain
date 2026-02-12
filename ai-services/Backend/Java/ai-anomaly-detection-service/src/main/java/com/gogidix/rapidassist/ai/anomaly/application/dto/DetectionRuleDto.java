package com.gogidix.rapidassist.ai.anomaly.application.dto;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DetectionRule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionRuleDto {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RuleType ruleType;
    private List<String> patternIds;
    private Map<String, Object> conditions;
    private String dataSource;
    private Integer priority;
    private Boolean isActive;
    private Boolean createAlert;
    private AlertSeverity alertSeverity;
    private List<String> notificationChannels;
    private String category;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
