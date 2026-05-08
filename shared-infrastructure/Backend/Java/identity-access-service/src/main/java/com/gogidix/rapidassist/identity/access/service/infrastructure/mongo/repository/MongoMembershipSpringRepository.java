package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository;

import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.MembershipDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoMembershipSpringRepository extends MongoRepository<MembershipDocument, String> {

    List<MembershipDocument> findByTenantIdAndUserId(String tenantId, String userId);

    List<MembershipDocument> findByTenantIdAndOrgUnitId(String tenantId, String orgUnitId);
}
