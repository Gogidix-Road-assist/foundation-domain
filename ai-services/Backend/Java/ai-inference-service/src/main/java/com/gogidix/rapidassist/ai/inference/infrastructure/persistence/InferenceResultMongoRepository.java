package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InferenceResultMongoRepository extends MongoRepository<InferenceResult, UUID> {

    List<InferenceResult> findByTenantId(String tenantId);

    List<InferenceResult> findByInferenceRequestId(UUID inferenceRequestId);

    void deleteByInferenceRequestId(UUID inferenceRequestId);
}
