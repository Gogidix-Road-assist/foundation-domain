package com.gogidix.rapidassist.orchestration.matching.infrastructure.mongodb.repository;

import com.gogidix.rapidassist.orchestration.matching.domain.model.ProviderProfile;
import com.gogidix.rapidassist.orchestration.matching.domain.port.out.ProviderProfileRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProviderProfileRepositoryImpl implements ProviderProfileRepositoryPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public ProviderProfile save(ProviderProfile profile) {
        return mongoTemplate.save(profile);
    }

    @Override
    public Optional<ProviderProfile> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, ProviderProfile.class));
    }

    @Override
    public Optional<ProviderProfile> findByProviderId(String providerId) {
        Query query = new Query(Criteria.where("providerId").is(providerId));
        return Optional.ofNullable(mongoTemplate.findOne(query, ProviderProfile.class));
    }

    @Override
    public List<ProviderProfile> findByTenantId(String tenantId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findByStatus(ProviderProfile.ProviderStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findByStatusAndIsActive(
        ProviderProfile.ProviderStatus status,
        Boolean isActive
    ) {
        Query query = new Query(Criteria.where("status").is(status)
            .and("isActive").is(isActive));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ProviderProfile> findProvidersNearLocation(
        Double longitude,
        Double latitude,
        Double radiusKm
    ) {
        // Geospatial query using $nearSphere with Point
        org.springframework.data.geo.Point point = new org.springframework.data.geo.Point(latitude, longitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        Query query = new Query(
            Criteria.where("currentLocation").nearSphere(point).maxDistance(radiusKm / 6371.0)
        );
        query.addCriteria(Criteria.where("isActive").is(true));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findByCapabilitiesContaining(String capability) {
        Query query = new Query(Criteria.where("capabilities").in(capability));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findActiveProviders() {
        Query query = new Query(Criteria.where("isActive").is(true));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), ProviderProfile.class);
    }

    @Override
    public void deleteByProviderId(String providerId) {
        mongoTemplate.remove(Query.query(Criteria.where("providerId").is(providerId)), ProviderProfile.class);
    }

    @Override
    public boolean existsByProviderId(String providerId) {
        Query query = new Query(Criteria.where("providerId").is(providerId));
        return mongoTemplate.exists(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findByProviderIdIn(List<String> providerIds) {
        Query query = new Query(Criteria.where("providerId").in(providerIds));
        return mongoTemplate.find(query, ProviderProfile.class);
    }

    @Override
    public List<ProviderProfile> findByTenantIdAndStatus(
        String tenantId,
        ProviderProfile.ProviderStatus status
    ) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
            .and("status").is(status));
        return mongoTemplate.find(query, ProviderProfile.class);
    }
}
