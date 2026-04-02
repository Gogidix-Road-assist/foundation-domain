package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * MongoDB Document for Token.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "token")
public class TokenEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID uuid;
    @Indexed
    private String tenantId;
    @Indexed
    private UUID textProcessingId;
    private String text;
    private String lemma;
    private String partOfSpeech;
    @Indexed
    private int position;
    private int startPosition;
    private int endPosition;
    private String morphology;
    private String dependency;
    private String headToken;
}
