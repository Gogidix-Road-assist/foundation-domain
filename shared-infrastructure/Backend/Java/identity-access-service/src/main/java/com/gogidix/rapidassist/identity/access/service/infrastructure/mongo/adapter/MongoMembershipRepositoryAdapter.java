package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.adapter;

import com.gogidix.rapidassist.identity.access.service.domain.model.Membership;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.MembershipRepository;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.MembershipDocument;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository.MongoMembershipSpringRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("!test")
public class MongoMembershipRepositoryAdapter implements MembershipRepository {

    private final MongoMembershipSpringRepository springRepository;

    public MongoMembershipRepositoryAdapter(MongoMembershipSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Membership save(Membership membership) {
        MembershipDocument d = toDoc(membership);
        MembershipDocument saved = springRepository.save(d);
        return toDomain(saved);
    }

    @Override
    public List<Membership> findByUserId(String tenantId, String userId) {
        return springRepository.findByTenantIdAndUserId(tenantId, userId).stream().map(MongoMembershipRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Membership> findByOrgUnitId(String tenantId, String orgUnitId) {
        return springRepository.findByTenantIdAndOrgUnitId(tenantId, orgUnitId).stream().map(MongoMembershipRepositoryAdapter::toDomain).toList();
    }

    private static MembershipDocument toDoc(Membership membership) {
        MembershipDocument d = new MembershipDocument();
        d.setId(membership.id());
        d.setTenantId(membership.tenantId());
        d.setOrgUnitId(membership.orgUnitId());
        d.setUserId(membership.userId());
        d.setRoleIds(membership.roleIds());
        return d;
    }

    private static Membership toDomain(MembershipDocument d) {
        return new Membership(d.getId(), d.getTenantId(), d.getOrgUnitId(), d.getUserId(), d.getRoleIds());
    }
}
