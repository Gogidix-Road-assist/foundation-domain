package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for DetectionRule.
 * Maps to detection_rule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "detection_rule")
public class DetectionRuleEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private RuleType ruleType;

    private List<String> patternIds;

    private Map<String, Object> conditions;

    @Indexed
    private String dataSource;

    @Indexed
    private Integer priority;

    @Indexed
    private Boolean isActive;

    private Boolean createAlert;

    private AlertSeverity alertSeverity;

    private List<String> notificationChannels;

    @Indexed
    private String category;

    private Map<String, Object> metadata;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
