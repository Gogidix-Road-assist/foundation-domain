package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.model.NotificationTemplate;
import com.gogidix.rapidassist.notification.service.domain.port.out.TemplateStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class MongoTemplateStore implements TemplateStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoTemplateStore.class);
    private static final String CACHE_PREFIX = "template:";
    private static final int MAX_CACHE_SIZE = 500;

    private final TemplateRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    // In-memory cache for frequently accessed templates
    private final ConcurrentHashMap<String, NotificationTemplate> templateCache = new ConcurrentHashMap<>();

    public MongoTemplateStore(TemplateRepository repository,
                             RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        logger.info("MongoTemplateStore initialized with MongoDB and Redis");
    }

    @Override
    public Optional<NotificationTemplate> find(String tenantId, String templateId) {
        String cacheKey = buildCacheKey(tenantId, templateId);

        // Check in-memory cache first
        if (templateCache.containsKey(cacheKey)) {
            logger.debug("In-memory cache hit for template: tenant={}, templateId={}", tenantId, templateId);
            return Optional.of(templateCache.get(cacheKey));
        }

        try {
            TemplateDocument document = repository.findByTenantIdAndTemplateId(tenantId, templateId);
            if (document != null) {
                NotificationTemplate template = document.toDomain();

                // Add to in-memory cache
                if (templateCache.size() < MAX_CACHE_SIZE) {
                    templateCache.put(cacheKey, template);
                }

                logger.debug("Retrieved template: tenant={}, templateId={}", tenantId, templateId);
                return Optional.of(template);
            }

            logger.debug("Template not found: tenant={}, templateId={}", tenantId, templateId);
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding template: tenant={}, templateId={}", tenantId, templateId, e);
            throw new RuntimeException("Failed to find template", e);
        }
    }

    @Override
    public List<NotificationTemplate> findByTenantId(String tenantId) {
        try {
            List<TemplateDocument> documents = repository.findByTenantId(tenantId);
            return documents.stream()
                .map(TemplateDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding templates for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to find templates", e);
        }
    }

    @Override
    public List<NotificationTemplate> findByType(String tenantId, Notification.NotificationType type) {
        try {
            List<TemplateDocument> documents = repository.findByTenantIdAndType(tenantId, type);
            return documents.stream()
                .map(TemplateDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding templates by type: tenant={}, type={}", tenantId, type, e);
            throw new RuntimeException("Failed to find templates", e);
        }
    }

    @Override
    public List<NotificationTemplate> findActiveTemplates(String tenantId) {
        try {
            List<TemplateDocument> documents = repository.findByTenantIdAndIsActive(tenantId, true);
            return documents.stream()
                .map(TemplateDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding active templates for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to find active templates", e);
        }
    }

    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        try {
            TemplateDocument existing = repository.findByTenantIdAndTemplateId(
                template.tenantId(),
                template.templateId()
            );

            TemplateDocument document;
            if (existing != null) {
                // Update existing
                document = TemplateDocument.updateFromDomain(existing, template);
                logger.debug("Updating existing template: tenant={}, templateId={}",
                    template.tenantId(), template.templateId());
            } else {
                // Create new
                document = TemplateDocument.fromDomain(template);
                logger.debug("Creating new template: tenant={}, templateId={}",
                    template.tenantId(), template.templateId());
            }

            // Save to MongoDB
            TemplateDocument saved = repository.save(document);
            NotificationTemplate savedTemplate = saved.toDomain();

            // Update caches
            String cacheKey = buildCacheKey(savedTemplate.tenantId(), savedTemplate.templateId());
            templateCache.put(cacheKey, savedTemplate);

            logger.info("Saved template: tenant={}, templateId={}, version={}",
                template.tenantId(),
                template.templateId(),
                template.version());

            return savedTemplate;

        } catch (OptimisticLockingFailureException e) {
            logger.error("Optimistic lock failure when saving template: tenant={}, templateId={}",
                template.tenantId(), template.templateId(), e);
            throw new RuntimeException("Concurrent modification detected", e);
        } catch (Exception e) {
            logger.error("Error saving template: tenant={}, templateId={}",
                template.tenantId(), template.templateId(), e);
            throw new RuntimeException("Failed to save template", e);
        }
    }

    @Override
    public void delete(String tenantId, String templateId) {
        try {
            repository.deleteByTenantIdAndTemplateId(tenantId, templateId);

            // Remove from cache
            String cacheKey = buildCacheKey(tenantId, templateId);
            templateCache.remove(cacheKey);

            logger.info("Deleted template: tenant={}, templateId={}", tenantId, templateId);
        } catch (Exception e) {
            logger.error("Error deleting template: tenant={}, templateId={}", tenantId, templateId, e);
            throw new RuntimeException("Failed to delete template", e);
        }
    }

    @Override
    public List<NotificationTemplate> searchTemplates(String tenantId, String keyword) {
        try {
            List<TemplateDocument> documents = repository.searchTemplates(tenantId, keyword);
            return documents.stream()
                .map(TemplateDocument::toDomain)
                .toList();
        } catch (Exception e) {
            logger.error("Error searching templates: tenant={}, keyword={}", tenantId, keyword, e);
            throw new RuntimeException("Failed to search templates", e);
        }
    }

    private String buildCacheKey(String tenantId, String templateId) {
        return tenantId + ":" + templateId;
    }
}