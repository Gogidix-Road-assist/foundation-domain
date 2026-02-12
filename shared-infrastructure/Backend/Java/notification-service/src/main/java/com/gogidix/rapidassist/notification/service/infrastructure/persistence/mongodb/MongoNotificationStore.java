package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.port.out.NotificationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class MongoNotificationStore implements NotificationStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoNotificationStore.class);
    private static final String CACHE_PREFIX = "notification:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    private static final String PENDING_QUEUE = "notifications:pending";

    private final NotificationRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    // In-memory cache for frequently accessed notifications
    private final ConcurrentHashMap<String, Notification> recentCache = new ConcurrentHashMap<>();
    private static final int MAX_RECENT_CACHE_SIZE = 1000;

    public MongoNotificationStore(NotificationRepository repository,
                                 RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        logger.info("MongoNotificationStore initialized with MongoDB and Redis");
    }

    @Override
    public Optional<Notification> find(String tenantId, String notificationId) {
        // Check recent cache first
        String cacheKey = tenantId + ":" + notificationId;
        if (recentCache.containsKey(cacheKey)) {
            logger.debug("Cache hit for notification: tenant={}, notificationId={}", tenantId, notificationId);
            return Optional.of(recentCache.get(cacheKey));
        }

        try {
            NotificationDocument document = repository.findByTenantIdAndNotificationId(tenantId, notificationId);
            if (document != null) {
                Notification notification = document.toDomain();

                // Add to recent cache
                if (recentCache.size() < MAX_RECENT_CACHE_SIZE) {
                    recentCache.put(cacheKey, notification);
                }

                logger.debug("Retrieved notification: tenant={}, notificationId={}", tenantId, notificationId);
                return Optional.of(notification);
            }

            logger.debug("Notification not found: tenant={}, notificationId={}", tenantId, notificationId);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding notification: tenant={}, notificationId={}", tenantId, notificationId, e);
            throw new RuntimeException("Failed to find notification", e);
        }
    }

    @Override
    public List<Notification> findByTenantId(String tenantId) {
        try {
            List<NotificationDocument> documents = repository.findByTenantId(tenantId);
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding notifications for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to find notifications", e);
        }
    }

    @Override
    public List<Notification> findByStatus(String tenantId, Notification.NotificationStatus status) {
        try {
            List<NotificationDocument> documents = repository.findByTenantIdAndStatus(tenantId, status);
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding notifications by status: tenant={}, status={}", tenantId, status, e);
            throw new RuntimeException("Failed to find notifications", e);
        }
    }

    @Override
    public List<Notification> findScheduledNotificationsReadyToSend() {
        try {
            List<NotificationDocument> documents = repository.findAllScheduledNotificationsReadyToSend(Instant.now());
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding scheduled notifications ready to send", e);
            throw new RuntimeException("Failed to find scheduled notifications", e);
        }
    }

    @Override
    public List<Notification> findByRecipient(String tenantId, String recipient) {
        try {
            List<NotificationDocument> documents = repository.findByRecipient(tenantId, recipient);
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding notifications by recipient: tenant={}, recipient={}", tenantId, recipient, e);
            throw new RuntimeException("Failed to find notifications", e);
        }
    }

    @Override
    public Notification save(Notification notification) {
        try {
            NotificationDocument existing = repository.findByTenantIdAndNotificationId(
                notification.tenantId(),
                notification.notificationId()
            );

            NotificationDocument document;
            if (existing != null) {
                // Update existing
                document = NotificationDocument.updateFromDomain(existing, notification);
                logger.debug("Updating existing notification: tenant={}, notificationId={}",
                    notification.tenantId(), notification.notificationId());
            } else {
                // Create new
                document = NotificationDocument.fromDomain(notification);
                logger.debug("Creating new notification: tenant={}, notificationId={}",
                    notification.tenantId(), notification.notificationId());
            }

            // Save to MongoDB
            NotificationDocument saved = repository.save(document);
            Notification savedNotification = saved.toDomain();

            // Update recent cache
            String cacheKey = savedNotification.tenantId() + ":" + savedNotification.notificationId();
            recentCache.put(cacheKey, savedNotification);

            // Add to Redis queue if pending
            if (notification.status() == Notification.NotificationStatus.PENDING) {
                redisTemplate.opsForList().rightPush(PENDING_QUEUE, savedNotification.notificationId());
                redisTemplate.expire(PENDING_QUEUE, Duration.ofHours(24));
            }

            logger.info("Saved notification: tenant={}, notificationId={}, status={}",
                notification.tenantId(),
                notification.notificationId(),
                notification.status());

            return savedNotification;

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic lock failure when saving notification: tenant={}, notificationId={}",
                notification.tenantId(), notification.notificationId(), e);
            throw new RuntimeException("Concurrent modification detected", e);
        } catch (Exception e) {
            logger.error("Error saving notification: tenant={}, notificationId={}",
                notification.tenantId(), notification.notificationId(), e);
            throw new RuntimeException("Failed to save notification", e);
        }
    }

    @Override
    public void delete(String tenantId, String notificationId) {
        try {
            repository.deleteByTenantIdAndNotificationId(tenantId, notificationId);

            // Remove from caches
            String cacheKey = tenantId + ":" + notificationId;
            recentCache.remove(cacheKey);

            // Remove from Redis queue
            redisTemplate.opsForList().remove(PENDING_QUEUE, 1, notificationId);

            logger.info("Deleted notification: tenant={}, notificationId={}", tenantId, notificationId);
        } catch (Exception e) {
            logger.error("Error deleting notification: tenant={}, notificationId={}", tenantId, notificationId, e);
            throw new RuntimeException("Failed to delete notification", e);
        }
    }

    @Override
    public List<Notification> findNotificationsForRetry(String tenantId, int maxRetries) {
        try {
            List<NotificationDocument> documents = repository.findNotificationsForRetry(tenantId, maxRetries);
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding notifications for retry: tenant={}, maxRetries={}", tenantId, maxRetries, e);
            throw new RuntimeException("Failed to find notifications for retry", e);
        }
    }

    @Override
    public List<Notification> findByDateRange(String tenantId, Instant from, Instant to) {
        try {
            List<NotificationDocument> documents = repository.findByDateRange(tenantId, from, to);
            return documents.stream()
                .map(NotificationDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding notifications by date range: tenant={}, from={}, to={}", tenantId, from, to, e);
            throw new RuntimeException("Failed to find notifications", e);
        }
    }
}