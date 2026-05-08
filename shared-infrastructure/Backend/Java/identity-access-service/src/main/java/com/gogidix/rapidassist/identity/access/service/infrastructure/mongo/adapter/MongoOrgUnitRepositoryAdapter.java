package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.adapter;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnit;
import com.gogidix.rapidassist.identity.access.service.domain.port.out.OrgUnitRepository;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document.OrgUnitDocument;
import com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.repository.MongoOrgUnitSpringRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!test")
public class MongoOrgUnitRepositoryAdapter implements OrgUnitRepository {

    private final MongoOrgUnitSpringRepository springRepository;

    public MongoOrgUnitRepositoryAdapter(MongoOrgUnitSpringRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public OrgUnit save(OrgUnit orgUnit) {
        OrgUnitDocument doc = toDoc(orgUnit);
        OrgUnitDocument saved = springRepository.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<OrgUnit> findById(String id) {
        return springRepository.findById(id).map(MongoOrgUnitRepositoryAdapter::toDomain);
    }

    @Override
    public List<OrgUnit> findChildren(String tenantId, String parentId) {
        return springRepository.findByTenantIdAndParentId(tenantId, parentId).stream()
                .map(MongoOrgUnitRepositoryAdapter::toDomain)
                .toList();
    }

    private static OrgUnitDocument toDoc(OrgUnit orgUnit) {
        OrgUnitDocument d = new OrgUnitDocument();
        d.setId(orgUnit.id());
        d.setTenantId(orgUnit.tenantId());
        d.setCountry(orgUnit.country());
        d.setType(orgUnit.type());
        d.setName(orgUnit.name());
        d.setParentId(orgUnit.parentId());
        return d;
    }

    private static OrgUnit toDomain(OrgUnitDocument d) {
        return new OrgUnit(d.getId(), d.getTenantId(), d.getCountry(), d.getType(), d.getName(), d.getParentId());
    }
}
