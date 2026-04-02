package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.aggregate.InferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InferenceRequestMongoRepository extends MongoRepository<InferenceRequest, UUID> {

    List<InferenceRequest> findByTenantId(String tenantId);

    List<InferenceRequest> findByModelId(String modelId);

    List<InferenceRequest> findByStatus(InferenceStatus status);

    List<InferenceRequest> findByTenantIdAndStatus(String tenantId, InferenceStatus status);

    List<InferenceRequest> findByRequestedBy(String requestedBy);

    boolean existsByRequestId(String requestId);

    InferenceRequest findByRequestId(String requestId);
}
