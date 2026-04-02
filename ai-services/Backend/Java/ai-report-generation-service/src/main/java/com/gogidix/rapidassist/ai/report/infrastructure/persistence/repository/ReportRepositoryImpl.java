package com.gogidix.rapidassist.ai.report.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.report.domain.aggregate.Report;
import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;
import com.gogidix.rapidassist.ai.report.domain.repository.ReportRepositoryPort;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportEntity;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.mapper.ReportPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ReportRepositoryPort using MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryPort {

    private final SpringDataReportRepository springDataRepository;
    private final ReportPersistenceMapper mapper;

    @Override
    public Report save(String tenantId, Report report) {
        log.debug("Saving report: {} for tenant: {}", report.getId(), tenantId);
        ReportEntity entity = mapper.toEntity(report);
        ReportEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Report> findById(String tenantId, UUID reportId) {
        log.debug("Finding report by ID: {} for tenant: {}", reportId, tenantId);
        return springDataRepository.findByUuidAndTenantId(reportId, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Report> findByTenantId(String tenantId) {
        log.debug("Finding all reports for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByStatus(String tenantId, ReportStatus status) {
        log.debug("Finding reports by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByRequestedBy(String tenantId, String requestedBy) {
        log.debug("Finding reports by requestedBy: {} for tenant: {}", requestedBy, tenantId);
        return springDataRepository.findByRequestedByAndTenantId(requestedBy, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByTemplateId(String tenantId, UUID templateId) {
        log.debug("Finding reports by templateId: {} for tenant: {}", templateId, tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .filter(r -> templateId.equals(r.getUuid()))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByScheduleId(String tenantId, UUID scheduleId) {
        log.debug("Finding reports by scheduleId: {} for tenant: {}", scheduleId, tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .filter(r -> scheduleId.equals(r.getUuid()))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding reports between dates for tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findPendingReports(String tenantId) {
        log.debug("Finding pending reports for tenant: {}", tenantId);
        List<ReportStatus> statuses = List.of(ReportStatus.PENDING, ReportStatus.IN_PROGRESS);
        return springDataRepository.findByTenantIdAndStatusIn(tenantId, statuses).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findReportsReadyForDistribution(String tenantId) {
        log.debug("Finding reports ready for distribution for tenant: {}", tenantId);
        return springDataRepository.findByStatusAndTenantId(ReportStatus.COMPLETED, tenantId).stream()
                .map(mapper::toDomain)
                .filter(Report::canBeDistributed)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findFailedReportsForRetry(String tenantId, int maxRetryCount) {
        log.debug("Finding failed reports for retry for tenant: {}", tenantId);
        return springDataRepository.findFailedReportsForRetry(tenantId, maxRetryCount).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID reportId) {
        log.debug("Deleting report: {} for tenant: {}", reportId, tenantId);
        springDataRepository.deleteByUuidAndTenantId(reportId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID reportId) {
        return springDataRepository.existsByUuidAndTenantId(reportId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByStatus(String tenantId, ReportStatus status) {
        return springDataRepository.countByStatusAndTenantId(status, tenantId);
    }

    @Override
    public List<Report> findOldCompletedReports(String tenantId, LocalDateTime olderThan) {
        log.debug("Finding old completed reports for tenant: {}", tenantId);
        return springDataRepository.findOldCompletedReports(tenantId, olderThan).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
