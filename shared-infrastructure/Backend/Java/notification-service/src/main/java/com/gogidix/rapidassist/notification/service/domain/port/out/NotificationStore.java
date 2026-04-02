package com.gogidix.rapidassist.notification.service.domain.port.out;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationStore {

    Optional<Notification> find(String tenantId, String notificationId);

    List<Notification> findByTenantId(String tenantId);

    List<Notification> findByStatus(String tenantId, Notification.NotificationStatus status);

    List<Notification> findScheduledNotificationsReadyToSend();

    List<Notification> findByRecipient(String tenantId, String recipient);

    Notification save(Notification notification);

    void delete(String tenantId, String notificationId);

    List<Notification> findNotificationsForRetry(String tenantId, int maxRetries);

    List<Notification> findByDateRange(String tenantId,
                                       java.time.Instant from,
                                       java.time.Instant to);
}