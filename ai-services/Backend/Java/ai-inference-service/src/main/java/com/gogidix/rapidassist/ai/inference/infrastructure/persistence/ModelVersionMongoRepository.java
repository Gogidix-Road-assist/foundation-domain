package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModelVersionMongoRepository extends MongoRepository<ModelVersion, UUID> {

    List<ModelVersion> findByModelId(String modelId);

    List<ModelVersion> findByTenantId(String tenantId);

    List<ModelVersion> findByStatus(ModelVersionStatus status);

    Optional<ModelVersion> findByModelIdAndVersion(String modelId, String version);

    Optional<ModelVersion> findByModelIdAndIsTrue(String modelId);

    boolean existsByModelIdAndVersion(String modelId, String version);
}
