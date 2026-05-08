package com.gogidix.rapidassist.crossdomain.bridge.repository;

import com.gogidix.rapidassist.crossdomain.bridge.entity.ClaimValidationEntity;
import com.gogidix.rapidassist.crossdomain.bridge.entity.ClaimValidationEntity.ValidationStatus;
import com.gogidix.rapidassist.shared.persistence.library.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Claim Validation entities.
 * Extends BaseRepository for soft delete support and common queries.
 */
@Repository
public interface ClaimValidationRepository extends BaseRepository<ClaimValidationEntity> {

    /**
     * Find validation by claim ID
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.claimId = :claimId AND v.deleted = false ORDER BY v.createdAt DESC")
    Optional<ClaimValidationEntity> findByClaimId(@Param("claimId") String claimId);

    /**
     * Find validation by reference
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.validationReference = :reference AND v.deleted = false")
    Optional<ClaimValidationEntity> findByReference(@Param("reference") String reference);

    /**
     * Find validations by policy number
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.policyNumber = :policyNumber AND v.deleted = false ORDER BY v.validatedAt DESC")
    List<ClaimValidationEntity> findByPolicyNumber(@Param("policyNumber") String policyNumber);

    /**
     * Find validations by workshop
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.workshopId = :workshopId AND v.deleted = false ORDER BY v.validatedAt DESC")
    List<ClaimValidationEntity> findByWorkshopId(@Param("workshopId") String workshopId);

    /**
     * Find pending validations
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.status = 'PENDING' AND v.deleted = false ORDER BY v.validatedAt ASC")
    List<ClaimValidationEntity> findPendingValidations();

    /**
     * Find validations within date range
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.validatedAt BETWEEN :start AND :end AND v.deleted = false ORDER BY v.validatedAt DESC")
    List<ClaimValidationEntity> findByValidatedAtBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Count validations by status
     */
    @Query("SELECT COUNT(v) FROM ClaimValidationEntity v WHERE v.status = :status AND v.deleted = false")
    Long countByStatus(@Param("status") ValidationStatus status);

    /**
     * Find rejected validations with rejection reasons
     */
    @Query("SELECT v FROM ClaimValidationEntity v WHERE v.status = 'REJECTED' AND v.deleted = false ORDER BY v.validatedAt DESC")
    List<ClaimValidationEntity> findRejectedValidations();

    /**
     * Get validation statistics for a workshop
     */
    @Query("""
        SELECT new com.gogidix.rapidassist.crossdomain.bridge.repository.ValidationStats(
            COUNT(v),
            SUM(CASE WHEN v.valid = true THEN 1 ELSE 0 END),
            SUM(CASE WHEN v.valid = false THEN 1 ELSE 0 END),
            COALESCE(SUM(v.approvedAmount), 0)
        )
        FROM ClaimValidationEntity v
        WHERE v.workshopId = :workshopId
          AND v.deleted = false
          AND v.validatedAt >= :since
        """)
    ValidationStats getWorkshopStats(
        @Param("workshopId") String workshopId,
        @Param("since") LocalDateTime since
    );

    /**
     * Validation stats record
     */
    record ValidationStats(
        Long totalCount,
        Long approvedCount,
        Long rejectedCount,
        Double totalApprovedAmount
    ) {}
}
