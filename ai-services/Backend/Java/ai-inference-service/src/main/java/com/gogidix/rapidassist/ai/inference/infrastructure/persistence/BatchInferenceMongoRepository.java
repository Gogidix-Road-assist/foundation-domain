package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.BatchInferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.BatchStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchInferenceMongoRepository extends MongoRepository<BatchInferenceRequest, UUID> {

    Optional<BatchInferenceRequest> findByBatchId(String batchId);

    List<BatchInferenceRequest> findByTenantId(String tenantId);

    List<BatchInferenceRequest> findByStatus(BatchStatus status);

    List<BatchInferenceRequest> findByCreatedBy(String createdBy);

    boolean existsByBatchId(String batchId);
}
