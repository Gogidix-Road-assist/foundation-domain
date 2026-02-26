package com.gogidix.rapidassist.orchestration.matching.domain.port.out;

import com.gogidix.rapidassist.orchestration.matching.domain.model.MatchingCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for MatchingCriteria repository operations
 */
public interface MatchingCriteriaRepositoryPort {

    MatchingCriteria save(MatchingCriteria criteria);

    Optional<MatchingCriteria> findById(String id);

    Optional<MatchingCriteria> findByCriteriaId(String criteriaId);

    List<MatchingCriteria> findByTenantId(String tenantId);

    List<MatchingCriteria> findByTenantIdAndIsActive(
        String tenantId,
        Boolean isActive
    );

    List<MatchingCriteria> findByServiceType(String serviceType);

    List<MatchingCriteria> findValidCriteria(
        String tenantId,
        LocalDateTime currentTime
    );

    void deleteById(String id);

    void deleteByCriteriaId(String criteriaId);

    boolean existsByCriteriaId(String criteriaId);
}
