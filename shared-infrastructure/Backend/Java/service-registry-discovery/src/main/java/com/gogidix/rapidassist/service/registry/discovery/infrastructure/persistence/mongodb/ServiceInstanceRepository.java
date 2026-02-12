package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceInstanceRepository extends MongoRepository<ServiceInstanceDocument, String> {

    Optional<ServiceInstanceDocument> findByTenantIdAndServiceNameAndInstanceId(
        String tenantId, String serviceName, String instanceId);

    List<ServiceInstanceDocument> findByTenantIdAndServiceName(
        String tenantId, String serviceName);

    List<ServiceInstanceDocument> findByTenantId(String tenantId);

    List<ServiceInstanceDocument> findByTenantIdAndServiceNameAndStatus(
        String tenantId, String serviceName, String status);

    List<String> findDistinctServiceNameByTenantId(String tenantId);

    List<ServiceInstanceDocument> findByTenantIdAndTagsContaining(
        String tenantId, String tag);

    @Query("{'tenantId': ?0, 'tags': {$in: ?1}}")
    List<ServiceInstanceDocument> findByTenantIdAndTagsIn(
        String tenantId, List<String> tags);

    List<ServiceInstanceDocument> findByTenantIdAndServiceNameAndVersion(
        String tenantId, String serviceName, String version);

    List<ServiceInstanceDocument> findByTenantIdAndEnvironment(
        String tenantId, String environment);

    @Query("{'tenantId': ?0, $or: [" +
           "{'serviceName': {$regex: ?1, $options: 'i'}}, " +
           "{'instanceId': {$regex: ?1, $options: 'i'}}, " +
           "{'host': {$regex: ?1, $options: 'i'}}" +
           "]}")
    List<ServiceInstanceDocument> searchByTenantIdAndKeyword(
        String tenantId, String keyword);

    @Query("{'lastHeartbeat': {$lt: ?0}}")
    List<ServiceInstanceDocument> findByLastHeartbeatBefore(Instant threshold);

    long countByTenantIdAndServiceName(String tenantId, String serviceName);
}
