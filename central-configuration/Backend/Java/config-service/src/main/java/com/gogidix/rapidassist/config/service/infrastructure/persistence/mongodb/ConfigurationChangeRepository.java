package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationChangeRepository extends MongoRepository<ConfigurationChangeDocument, String> {

    Optional<ConfigurationChangeDocument> findByChangeId(String changeId);

    List<ConfigurationChangeDocument> findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
        String tenantId, String configKey, String environment, String namespace);

    List<ConfigurationChangeDocument> findByTenantIdAndConfigKey(String tenantId, String configKey);

    List<ConfigurationChangeDocument> findByTenantId(String tenantId);

    List<ConfigurationChangeDocument> findByTenantIdAndChangedBy(String tenantId, String changedBy);

    @Query("{ 'tenantId': ?0, 'changedAt': { $gte: ?1, $lte: ?2 } }")
    List<ConfigurationChangeDocument> findByTenantIdAndTimeRange(
        String tenantId, Instant from, Instant to);

    @Query(value = "{ 'tenantId': ?0 }", sort = "{ 'changedAt': -1 }")
    List<ConfigurationChangeDocument> findLatestByTenantId(String tenantId);

    @Query(value = "{ 'tenantId': ?0, 'configKey': ?1 }", sort = "{ 'changedAt': -1 }")
    List<ConfigurationChangeDocument> findLatestByTenantAndConfigKey(String tenantId, String configKey);

    @Query("{ 'tenantId': ?0, 'approvalStatus': ?1 }")
    List<ConfigurationChangeDocument> findByTenantIdAndApprovalStatus(
        String tenantId, ConfigurationChange.ApprovalStatus approvalStatus);

    @Query("{ 'tenantId': ?0, 'changeType': ?1 }")
    List<ConfigurationChangeDocument> findByTenantIdAndChangeType(
        String tenantId, ConfigurationChange.ChangeType changeType);

    @Query(value = "{ 'tenantId': ?0, '$text': { '$search': ?1 } }")
    List<ConfigurationChangeDocument> searchByKeyword(String tenantId, String keyword);

    List<ConfigurationChangeDocument> findByAffectedServicesContaining(String serviceName);
}