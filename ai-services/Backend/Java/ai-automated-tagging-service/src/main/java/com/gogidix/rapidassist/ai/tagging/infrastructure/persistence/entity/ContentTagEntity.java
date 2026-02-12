package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for ContentTag.
 * Maps to content_tag collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "content_tag")
@CompoundIndex(name = "tenant_content_tag", def = "{'tenantId': 1, 'contentId': 1, 'tagId': 1}", unique = true)
public class ContentTagEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String contentId;

    @Indexed
    private String contentType;

    @Indexed
    private UUID tagId;

    private ContentTag.TaggingSource taggingSource;

    private Double confidenceScore;

    private Boolean manuallyVerified;

    private String taggedBy;

    private String taggingMetadata;

    private LocalDateTime createdAt;

    private String createdBy;

    @Indexed
    private Long version;
}
