package com.gogidix.rapidassist.notification.service.domain.port.in;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationCommand {

    Notification sendImmediate(String tenantId, Notification.NotificationType type,
                             List<String> recipients, String subject, String content,
                             List<Notification.NotificationChannel> channels);

    Notification sendFromTemplate(String tenantId, String templateId,
                                 List<String> recipients, Map<String, Object> templateData,
                                 List<Notification.NotificationChannel> channels);

    Notification scheduleNotification(String tenantId, Notification.NotificationType type,
                                    List<String> recipients, String subject, String content,
                                    java.time.Instant scheduledAt,
                                    List<Notification.NotificationChannel> channels);

    boolean cancelNotification(String tenantId, String notificationId);

    Notification resendNotification(String tenantId, String notificationId);

    List<Notification> getPendingNotifications(String tenantId);

    List<Notification> getScheduledNotifications(String tenantId);

    Map<Notification.NotificationStatus, Long> getNotificationStatistics(String tenantId,
                                                                         java.time.Instant from,
                                                                         java.time.Instant to);
}