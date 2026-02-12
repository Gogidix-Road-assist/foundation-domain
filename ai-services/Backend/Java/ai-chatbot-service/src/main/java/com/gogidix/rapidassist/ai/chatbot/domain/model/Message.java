package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing Message.
 * Pure domain model with MongoDB annotations for multi-tenancy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private UUID id;

    /**
     * MANDATORY: Tenant ID for multi-tenancy
     * All queries MUST filter by this field
     */
    @Indexed
    private String tenantId;

    private String name;
    private String description;
    private String status;
    private java.util.Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
