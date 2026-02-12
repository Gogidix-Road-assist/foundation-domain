package com.gogidix.rapidassist.notification.service.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record NotificationTemplate(
    String tenantId,
    String templateId,
    String name,
    Notification.NotificationType type,
    List<Notification.NotificationChannel> supportedChannels,
    Map<String, TemplateContent> contents,
    Map<String, String> defaultVariables,
    boolean isActive,
    String version,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String approvedBy
) {

    public TemplateContent getContentForChannel(Notification.NotificationChannel channel) {
        return contents.get(channel.name().toLowerCase());
    }

    public record TemplateContent(
        String subject,
        String body,
        String htmlContent,
        Map<String, VariableDefinition> variables,
        List<String> attachments
    ) {}

    public record VariableDefinition(
        String name,
        String type,
        boolean required,
        String defaultValue,
        String description
    ) {}
}