package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository;

import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.RoleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoRoleSpringRepository extends MongoRepository<RoleDocument, String> {

    List<RoleDocument> findByTenantIdAndIdIn(String tenantId, List<String> ids);
}
