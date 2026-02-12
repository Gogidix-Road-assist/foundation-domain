package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for MatchingResult.
 * Maps to matching_result collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "matching_result")
public class MatchingResultEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String matchId;

    @Indexed
    private String sourceEntityType;

    @Indexed
    private String sourceEntityId;

    @Indexed
    private String targetEntityType;

    @Indexed
    private String targetEntityId;

    @Indexed
    private MatchingAlgorithmType algorithmType;

    private Double similarityScore;

    private Double confidenceScore;

    @Indexed
    private MatchingStatus status;

    private Map<String, Object> matchingAttributes;

    private Map<String, Object> metadata;

    private String version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;
}
