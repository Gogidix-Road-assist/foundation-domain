package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {

    NotificationDocument findByTenantIdAndNotificationId(String tenantId, String notificationId);

    List<NotificationDocument> findByTenantId(String tenantId);

    List<NotificationDocument> findByTenantIdAndStatus(String tenantId, Notification.NotificationStatus status);

    @Query("{ 'tenantId': ?0, 'scheduledAt': { $lte: ?1 }, 'status': 'SCHEDULED' }")
    List<NotificationDocument> findScheduledNotificationsReadyToSend(String tenantId, Instant now);

    @Query("{ 'tenantId': ?0, 'status': { $in: ['FAILED', 'PARTIALLY_DELIVERED'] }, 'retryCount': { $lt: ?1 } }")
    List<NotificationDocument> findNotificationsForRetry(String tenantId, int maxRetries);

    @Query("{ 'tenantId': ?0, 'recipients': ?1 }")
    List<NotificationDocument> findByRecipient(String tenantId, String recipient);

    @Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<NotificationDocument> findByDateRange(String tenantId, Instant from, Instant to);

    @Query("{ 'status': 'SCHEDULED', 'scheduledAt': { $lte: ?1 } }")
    List<NotificationDocument> findAllScheduledNotificationsReadyToSend(Instant now);

    void deleteByTenantIdAndNotificationId(String tenantId, String notificationId);
}