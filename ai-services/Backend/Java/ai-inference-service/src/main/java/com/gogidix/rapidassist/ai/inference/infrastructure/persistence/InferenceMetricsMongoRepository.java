package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InferenceMetricsMongoRepository extends MongoRepository<InferenceMetrics, UUID> {

    List<InferenceMetrics> findByTenantId(String tenantId);

    List<InferenceMetrics> findByInferenceRequestId(UUID inferenceRequestId);

    List<InferenceMetrics> findByMetricType(String metricType);

    void deleteByInferenceRequestId(UUID inferenceRequestId);
}
