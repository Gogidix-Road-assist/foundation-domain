package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJob;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TrainingJob domain operations.
 */
public interface TrainingJobRepositoryPort {

    /**
     * Save a training job.
     */
    TrainingJob save(String tenantId, TrainingJob trainingJob);

    /**
     * Find training job by ID.
     */
    Optional<TrainingJob> findById(String tenantId, UUID id);

    /**
     * Find all jobs for a model.
     */
    List<TrainingJob> findByModelId(String tenantId, UUID modelId);

    /**
     * Find jobs by status.
     */
    List<TrainingJob> findByStatus(String tenantId, TrainingJobStatus status);

    /**
     * Find job by name.
     */
    Optional<TrainingJob> findByName(String tenantId, String name);

    /**
     * Find running jobs.
     */
    List<TrainingJob> findRunningJobs(String tenantId);

    /**
     * Find completed jobs for a model.
     */
    List<TrainingJob> findCompletedJobs(String tenantId, UUID modelId);

    /**
     * Check if job exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete a training job.
     */
    void delete(String tenantId, UUID id);

    /**
     * Delete all jobs for a model.
     */
    void deleteByModelId(String tenantId, UUID modelId);
}
