package com.gogidix.rapidassist.notification.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.notification.service.domain.model.Notification;
import com.gogidix.rapidassist.notification.service.domain.model.NotificationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends MongoRepository<TemplateDocument, String> {

    TemplateDocument findByTenantIdAndTemplateId(String tenantId, String templateId);

    List<TemplateDocument> findByTenantId(String tenantId);

    List<TemplateDocument> findByTenantIdAndType(String tenantId, Notification.NotificationType type);

    List<TemplateDocument> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    @Query("{ 'tenantId': ?0, '$or': [{ 'name': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } }] }")
    List<TemplateDocument> searchTemplates(String tenantId, String keyword);

    void deleteByTenantIdAndTemplateId(String tenantId, String templateId);
}