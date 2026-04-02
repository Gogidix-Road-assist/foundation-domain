package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TranslationSession.
 * Maps to translation_session collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "translation_session")
public class TranslationSessionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed(unique = true)
    private String sessionId;

    @Indexed
    private com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession.SessionStatus status;

    @Indexed
    private com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession.SessionType sessionType;

    @Indexed
    private String defaultSourceLanguage;

    @Indexed
    private String defaultTargetLanguage;

    private String channel;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastActivityAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
