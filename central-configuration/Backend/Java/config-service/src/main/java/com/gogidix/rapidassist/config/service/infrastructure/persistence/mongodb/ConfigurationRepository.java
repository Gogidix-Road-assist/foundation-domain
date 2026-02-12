package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends MongoRepository<ConfigurationDocument, String> {

    Optional<ConfigurationDocument> findByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
        String tenantId, String configKey, String environment, String namespace);

    List<ConfigurationDocument> findByTenantIdAndEnvironmentAndNamespace(
        String tenantId, String environment, String namespace);

    List<ConfigurationDocument> findByTenantIdAndConfigKey(String tenantId, String configKey);

    List<ConfigurationDocument> findByTenantId(String tenantId);

    List<ConfigurationDocument> findByTenantIdAndStatus(
        String tenantId, Configuration.ConfigurationStatus status);

    @Query("{ 'tenantId': ?0, 'tags': { $in: ?1 } }")
    List<ConfigurationDocument> findByTenantIdAndTags(String tenantId, List<String> tags);

    @Query("{ 'tenantId': ?0, 'updatedAt': { $gte: ?1 } }")
    List<ConfigurationDocument> findByTenantIdAndUpdatedAfter(String tenantId, Instant since);

    @Query(value = "{ 'tenantId': ?0, 'namespace': ?1, 'environment': ?2 }",
           sort = "{ 'updatedAt': -1 }")
    List<ConfigurationDocument> findLatestByTenantNamespaceAndEnvironment(
        String tenantId, String namespace, String environment);

    @Query(value = "{ 'tenantId': ?0, 'configKey': ?1, 'environment': ?2 }",
           sort = "{ 'version': -1 }")
    Optional<ConfigurationDocument> findLatestVersion(
        String tenantId, String configKey, String environment);

    @Query(value = "{ 'tenantId': ?0, 'configKey': ?1, 'environment': ?2 }",
           sort = "{ 'version': -1 }")
    List<ConfigurationDocument> findAllVersions(
        String tenantId, String configKey, String environment);

    @Query("{ 'tenantId': ?0, '$text': { '$search': ?1 } }")
    List<ConfigurationDocument> searchByKeyword(String tenantId, String keyword);

    // Note: incrementVersion is implemented via MongoConfigurationStore.customIncrementVersion()
    // Spring Data MongoDB doesn't support @Update annotation directly on repository methods

    void deleteByTenantIdAndConfigKeyAndEnvironmentAndNamespace(
        String tenantId, String configKey, String environment, String namespace);
}