package com.gogidix.rapidassist.orchestration.fleetorganization.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitStatus;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetUnit.UnitType;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository.FleetUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoFleetUnitRepository implements FleetUnitRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public FleetUnit save(FleetUnit fleetUnit) {
        log.debug("Saving fleet unit: {} for tenant: {}", fleetUnit.getUnitId(), fleetUnit.getTenantId());
        return mongoTemplate.save(fleetUnit);
    }

    @Override
    public Optional<FleetUnit> findByIdAndTenantId(String unitId, String tenantId) {
        Query query = Query.query(
                Criteria.where("unitId").is(unitId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, FleetUnit.class));
    }

    @Override
    public List<FleetUnit> findByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public List<FleetUnit> findByOrganizationIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public List<FleetUnit> findByStatusAndTenantId(UnitStatus status, String tenantId) {
        Query query = Query.query(
                Criteria.where("status").is(status)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public List<FleetUnit> findByTypeAndStatusAndTenantId(UnitType type, UnitStatus status, String tenantId) {
        Query query = Query.query(
                Criteria.where("unitType").is(type)
                        .and("status").is(status)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public List<FleetUnit> findByTenantIdAndIsActive(String tenantId, boolean isActive) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("isActive").is(isActive)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public List<FleetUnit> findByDriverIdAndTenantId(String driverId, String tenantId) {
        Query query = Query.query(
                Criteria.where("currentDriverId").is(driverId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.find(query, FleetUnit.class);
    }

    @Override
    public Optional<FleetUnit> findByVinAndTenantId(String vin, String tenantId) {
        Query query = Query.query(
                Criteria.where("vin").is(vin)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, FleetUnit.class));
    }

    @Override
    public Optional<FleetUnit> findByLicensePlateAndTenantId(String licensePlate, String tenantId) {
        Query query = Query.query(
                Criteria.where("licensePlate").is(licensePlate)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return Optional.ofNullable(mongoTemplate.findOne(query, FleetUnit.class));
    }

    @Override
    public void deleteByIdAndTenantId(String unitId, String tenantId) {
        Query query = Query.query(
                Criteria.where("unitId").is(unitId)
                        .and("tenantId").is(tenantId)
        );
        FleetUnit fleetUnit = mongoTemplate.findOne(query, FleetUnit.class);
        if (fleetUnit != null) {
            fleetUnit.softDelete();
            mongoTemplate.save(fleetUnit);
        }
    }

    @Override
    public boolean existsByIdAndTenantId(String unitId, String tenantId) {
        Query query = Query.query(
                Criteria.where("unitId").is(unitId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.exists(query, FleetUnit.class);
    }

    @Override
    public long countByOrganizationIdAndTenantId(String organizationId, String tenantId) {
        Query query = Query.query(
                Criteria.where("organizationId").is(organizationId)
                        .and("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.count(query, FleetUnit.class);
    }

    @Override
    public long countByTenantId(String tenantId) {
        Query query = Query.query(
                Criteria.where("tenantId").is(tenantId)
                        .and("deletedAt").is(null)
        );
        return mongoTemplate.count(query, FleetUnit.class);
    }
}
