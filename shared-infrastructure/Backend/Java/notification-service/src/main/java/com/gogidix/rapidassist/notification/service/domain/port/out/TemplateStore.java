package com.gogidix.rapidassist.notification.service.domain.port.out;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.model.NotificationTemplate;

import java.util.List;
import java.util.Optional;

public interface TemplateStore {

    Optional<NotificationTemplate> find(String tenantId, String templateId);

    List<NotificationTemplate> findByTenantId(String tenantId);

    List<NotificationTemplate> findByType(String tenantId, Notification.NotificationType type);

    List<NotificationTemplate> findActiveTemplates(String tenantId);

    NotificationTemplate save(NotificationTemplate template);

    void delete(String tenantId, String templateId);

    List<NotificationTemplate> searchTemplates(String tenantId, String keyword);
}