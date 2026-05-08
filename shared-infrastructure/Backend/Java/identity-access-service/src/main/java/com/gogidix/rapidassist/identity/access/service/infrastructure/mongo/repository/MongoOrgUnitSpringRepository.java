package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository;

import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.OrgUnitDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoOrgUnitSpringRepository extends MongoRepository<OrgUnitDocument, String> {

    List<OrgUnitDocument> findByTenantIdAndParentId(String tenantId, String parentId);
}
