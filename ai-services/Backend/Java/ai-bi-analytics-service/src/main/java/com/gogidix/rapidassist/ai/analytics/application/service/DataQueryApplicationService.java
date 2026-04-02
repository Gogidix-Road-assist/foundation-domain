package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.ExecuteQueryCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.DataQueryDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.DataQueryMapper;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.model.DataQuery;
import com.gogidix.rapidassist.ai.analytics.domain.model.QueryStatus;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DataQueryRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application service for Data Query operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataQueryApplicationService {

    private final DataQueryRepositoryPort queryRepository;
    private final DataQueryMapper queryMapper;

    /**
     * Execute a data query
     */
    @Transactional
    public DataQueryDto executeQuery(ExecuteQueryCommand command) {
        log.info("Executing query for tenant: {}", command.getTenantId());

        // Validate command
        validateExecuteQueryCommand(command);

        // Create query
        DataQuery dataQuery = createQueryFromCommand(command);

        // Save query
        dataQuery = queryRepository.save(dataQuery);

        // Execute query
        dataQuery = executeDataQuery(dataQuery);

        // Save updated query with results
        dataQuery = queryRepository.save(dataQuery);

        log.info("Query executed successfully: {}", dataQuery.getId());
        return queryMapper.toDto(dataQuery);
    }

    /**
     * Get query by ID
     */
    @Transactional(readOnly = true)
    public DataQueryDto getQuery(String queryId, String tenantId) {
        log.info("Getting query: {} for tenant: {}", queryId, tenantId);

        UUID id = UUID.fromString(queryId);
        DataQuery query = queryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new InvalidReportDataException("Query not found with ID: " + queryId));

        return queryMapper.toDto(query);
    }

    /**
     * List queries for a tenant
     */
    @Transactional(readOnly = true)
    public List<DataQueryDto> listQueries(String tenantId, Boolean isActive, String category) {
        log.info("Listing queries for tenant: {} with filters - isActive: {}, category: {}", tenantId, isActive, category);

        List<DataQuery> queries;

        if (isActive != null && category != null) {
            queries = queryRepository.findByTenantIdAndIsActive(tenantId, isActive).stream()
                    .filter(q -> category.equals(q.getCategory()))
                    .toList();
        } else if (isActive != null) {
            queries = queryRepository.findByTenantIdAndIsActive(tenantId, isActive);
        } else if (category != null) {
            queries = queryRepository.findByTenantIdAndCategory(tenantId, category);
        } else {
            queries = queryRepository.findByTenantId(tenantId);
        }

        return queryMapper.toDtoList(queries);
    }

    private void validateExecuteQueryCommand(ExecuteQueryCommand command) {
        if (command.getTenantId() == null || command.getTenantId().isBlank()) {
            throw new InvalidReportDataException("Tenant ID is required");
        }
        if (command.getQuery() == null || command.getQuery().isBlank()) {
            throw new InvalidReportDataException("Query is required");
        }
        if (command.getDataSource() == null || command.getDataSource().isBlank()) {
            throw new InvalidReportDataException("Data source is required");
        }
    }

    private DataQuery createQueryFromCommand(ExecuteQueryCommand command) {
        return DataQuery.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .query(command.getQuery())
                .status(QueryStatus.PENDING)
                .dataSource(command.getDataSource())
                .parameters(command.getParameters())
                .filters(command.getFilters())
                .groupBy(command.getGroupBy())
                .orderBy(command.getOrderBy())
                .limit(command.getLimit())
                .offset(command.getOffset())
                .createdBy(command.getExecutedBy())
                .updatedBy(command.getExecutedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isCached(command.getIsCached() != null ? command.getIsCached() : Boolean.FALSE)
                .isActive(Boolean.TRUE)
                .build();
    }

    private DataQuery executeDataQuery(DataQuery dataQuery) {
        log.info("Executing data query: {}", dataQuery.getId());

        dataQuery.setStatus(QueryStatus.EXECUTING);
        dataQuery.setUpdatedAt(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        try {
            // Simulate query execution
            // In a real implementation, this would execute the query against the data source
            Thread.sleep(50); // Simulate processing

            dataQuery.setStatus(QueryStatus.COMPLETED);
            dataQuery.setExecutedAt(LocalDateTime.now());
            dataQuery.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            dataQuery.setResultCount((int) (Math.random() * 100));

            // Set sample results
            dataQuery.setResults(Map.of(
                    "rows", List.of(
                            Map.of("id", 1, "name", "Item 1", "value", 100),
                            Map.of("id", 2, "name", "Item 2", "value", 200),
                            Map.of("id", 3, "name", "Item 3", "value", 300)
                    ),
                    "total", 3
            ));

            if (dataQuery.getIsCached()) {
                dataQuery.setCacheExpiryAt(LocalDateTime.now().plusHours(1));
            }

            log.info("Query executed successfully: {}", dataQuery.getId());
        } catch (Exception e) {
            log.error("Query execution failed for: {}", dataQuery.getId(), e);
            dataQuery.setStatus(QueryStatus.FAILED);
            dataQuery.setErrorMessage(e.getMessage());
            dataQuery.setExecutedAt(LocalDateTime.now());
            dataQuery.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return dataQuery;
    }
}
