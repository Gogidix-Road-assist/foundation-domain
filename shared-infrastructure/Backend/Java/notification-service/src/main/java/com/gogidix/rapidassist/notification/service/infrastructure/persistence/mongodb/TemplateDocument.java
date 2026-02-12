package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.model.NotificationTemplate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "notification_templates")
public record TemplateDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("template_id")
    String templateId,

    @Field("name")
    String name,

    @Field("type")
    Notification.NotificationType type,

    @Field("supported_channels")
    List<Notification.NotificationChannel> supportedChannels,

    @Field("contents")
    Map<String, TemplateContentEmbedded> contents,

    @Field("default_variables")
    Map<String, String> defaultVariables,

    @Field("is_active")
    boolean isActive,

    @Field("version")
    String version,

    @Field("created_at")
    Instant createdAt,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_by")
    String createdBy,

    @Field("approved_by")
    String approvedBy,

    @Field("db_version")
    Long versionNumber
) {

    public static TemplateDocument fromDomain(NotificationTemplate template) {
        return new TemplateDocument(
            null, // MongoDB will generate ID
            template.tenantId(),
            template.templateId(),
            template.name(),
            template.type(),
            template.supportedChannels(),
            template.contents().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new TemplateContentEmbedded(e.getValue())
                )),
            template.defaultVariables(),
            template.isActive(),
            template.version(),
            template.createdAt(),
            template.updatedAt(),
            template.createdBy(),
            template.approvedBy(),
            1L
        );
    }

    public static TemplateDocument updateFromDomain(TemplateDocument existing,
                                                   NotificationTemplate template) {
        return new TemplateDocument(
            existing.id(),
            template.tenantId(),
            template.templateId(),
            template.name(),
            template.type(),
            template.supportedChannels(),
            template.contents().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new TemplateContentEmbedded(e.getValue())
                )),
            template.defaultVariables(),
            template.isActive(),
            template.version(),
            existing.createdAt(),
            template.updatedAt(),
            existing.createdBy(),
            template.approvedBy(),
            existing.versionNumber() + 1
        );
    }

    public NotificationTemplate toDomain() {
        return new NotificationTemplate(
            tenantId(),
            templateId(),
            name(),
            type(),
            supportedChannels(),
            contents().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().toDomain()
                )),
            defaultVariables(),
            isActive(),
            version(),
            createdAt(),
            updatedAt(),
            createdBy(),
            approvedBy()
        );
    }

    public record TemplateContentEmbedded(
        String subject,
        String body,
        String htmlContent,
        Map<String, VariableDefinitionEmbedded> variables,
        List<String> attachments
    ) {

        public TemplateContentEmbedded(NotificationTemplate.TemplateContent content) {
            this(
                content.subject(),
                content.body(),
                content.htmlContent(),
                content.variables().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new VariableDefinitionEmbedded(e.getValue())
                    )),
                content.attachments()
            );
        }

        public NotificationTemplate.TemplateContent toDomain() {
            return new NotificationTemplate.TemplateContent(
                subject(),
                body(),
                htmlContent(),
                variables().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toDomain()
                    )),
                attachments()
            );
        }
    }

    public record VariableDefinitionEmbedded(
        String name,
        String type,
        boolean required,
        String defaultValue,
        String description
    ) {

        public VariableDefinitionEmbedded(NotificationTemplate.VariableDefinition definition) {
            this(
                definition.name(),
                definition.type(),
                definition.required(),
                definition.defaultValue(),
                definition.description()
            );
        }

        public NotificationTemplate.VariableDefinition toDomain() {
            return new NotificationTemplate.VariableDefinition(
                name(),
                type(),
                required(),
                defaultValue(),
                description()
            );
        }
    }
}